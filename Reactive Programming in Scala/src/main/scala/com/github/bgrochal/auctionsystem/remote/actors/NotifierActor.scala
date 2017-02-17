package com.github.bgrochal.auctionsystem.remote.actors

import akka.actor.SupervisorStrategy.{Resume, Stop}
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import com.github.bgrochal.auctionsystem.remote.actors.NotifierActor.{RemoteServerFailureException, notification, repetition}

/**
  * @author Bartłomiej Grochal
  */
class NotifierActor extends Actor {

  val auctionPublisherActor = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2552/user/auctionPublisherActor")
  var requests = Map[ActorRef, notification]()
  var index = 0


  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3) {
    case _: RemoteServerFailureException =>
      println(s"An error occurred while connecting to remote server. Failure in node: ${sender.path.name}. Resuming...")
      self ! repetition(requests(sender), sender)
      Resume

    case exc =>
      println(s"Unexpected error occurred: $exc")
      Stop
  }

  override def receive: Receive = {
    case notification(auction, currentWinner, currentAmount) =>
      val notifierRequestActor = context.actorOf(Props(new NotifierRequestActor(auctionPublisherActor)),
        s"notifierRequestActor_$index")
      requests += (notifierRequestActor -> notification(auction, currentWinner, currentAmount))
      index = index + 1

      notifierRequestActor ! notification(auction, currentWinner, currentAmount)

    case repetition(notification, notifierRequestActor) =>
      notifierRequestActor ! notification
  }

}

object NotifierActor {

  final case class notification(auction: String, currentWinner: ActorRef, currentAmount: BigInt)

  final case class repetition(notification: notification, notifierRequestActor: ActorRef)

  class RemoteServerFailureException extends Exception

}
