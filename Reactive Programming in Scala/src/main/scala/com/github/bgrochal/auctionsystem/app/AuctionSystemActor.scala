package com.github.bgrochal.auctionsystem.app

import akka.actor.{Actor, Props}
import com.github.bgrochal.auctionsystem.model.actors.{BuyerActor, SellerActor}
import com.github.bgrochal.auctionsystem.remote.actors.NotifierActor
import com.github.bgrochal.auctionsystem.search.actors.MasterSearchActor

/**
  * @author Bartłomiej Grochal
  */
class AuctionSystemActor extends Actor {

  override def receive: Receive = {
    case initAuctionSystem =>
      println("Application started successfully")

      context.actorOf(Props(new MasterSearchActor(3)), "masterSearchActor")
      val notifier = context.actorOf(Props[NotifierActor], "notifierActor")

      context.actorOf(Props(new SellerActor("1", List("Audi A6 diesel manual", "Opel Astra diesel manual"), notifier)),
        "firstSellerActor")
      context.actorOf(Props(new SellerActor("2", List("Audi A5 diesel auto", "BMW E46 petrol manual"), notifier)),
        "secondSellerActor")

      // TODO: handle this in more elegant way.
      Thread.sleep(500)

      context.actorOf(Props(new BuyerActor(List("audi"), 1500)), "firstBuyer")
      context.actorOf(Props(new BuyerActor(List("diesel"), 2200)), "secondBuyer")
      context.actorOf(Props(new BuyerActor(List("auto", "petrol"), 1600)), "thirdBuyer")

    case AuctionSystemActor.closeAuctionSystem =>
      println("Closing message received.")
      context.system.terminate()

    case _ =>
      println("Received unhandled message.")
  }

}

object AuctionSystemActor {

  case object initAuctionSystem

  case object closeAuctionSystem

}
