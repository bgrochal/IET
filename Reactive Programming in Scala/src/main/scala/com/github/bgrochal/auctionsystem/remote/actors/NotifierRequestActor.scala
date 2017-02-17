package com.github.bgrochal.auctionsystem.remote.actors

import akka.actor.{Actor, ActorSelection}
import com.github.bgrochal.auctionsystem.remote.actors.NotifierActor.{RemoteServerFailureException, notification}

import scala.concurrent.forkjoin.ThreadLocalRandom

/**
  * @author Bartłomiej Grochal
  */
class NotifierRequestActor(val auctionPublisherActor: ActorSelection) extends Actor {

  def throwRandomException(): Unit =
    if (ThreadLocalRandom.current().nextDouble() < 0.1)
      throw new RemoteServerFailureException


  override def receive: Receive = {
    case notification(auction, currentWinner, currentAmount) =>
      throwRandomException()
      auctionPublisherActor ! notification(auction, currentWinner, currentAmount)
      context.stop(self)
  }

}
