package com.github.bgrochal.auctionsystem.search.actors

import akka.actor.{Actor, Props}
import akka.routing._
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Register, Search, Unregister}

import scala.concurrent.duration._

/**
  * @author Bartłomiej Grochal
  */
class MasterSearchActor(val routeesNumber: Int) extends Actor {

  var index = 0
  val routees = Vector.fill(routeesNumber) {
    val auctionSearchActor = context.actorOf(Props[AuctionSearchActor], s"auctionSearchActor_$index")
    index = index + 1
    context.watch(auctionSearchActor)
    ActorRefRoutee(auctionSearchActor)
  }

  var registrationRouter = {
    Router(BroadcastRoutingLogic(), routees)
  }

  var searchingRouter = {
    Router(RoundRobinRoutingLogic(), routees)
    // Router(ScatterGatherFirstCompletedRoutingLogic(within = 5 minutes), routees) // For testing purposes only
  }


  override def receive: Receive = {
    case message: Register =>
      registrationRouter.route(message, sender)
    case message: Unregister =>
      registrationRouter.route(message, sender)
    case message: Search =>
      searchingRouter.route(message, sender)
  }

}
