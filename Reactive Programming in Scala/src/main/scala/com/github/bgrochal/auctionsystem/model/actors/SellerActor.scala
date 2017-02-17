package com.github.bgrochal.auctionsystem.model.actors

import akka.actor.{Actor, ActorRef, Props}
import com.github.bgrochal.auctionsystem.model.actors.SellerActor.itemSold
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Done, Register, Unregister}

/**
  * @author Bartłomiej Grochal
  */
class SellerActor(val sellerID: String, val auctions: List[String], val notifier: ActorRef,
                  val searchEnginePath: String = "/user/auctionSystemActor/masterSearchActor") extends Actor {

  val masterSearchEngine = context.actorSelection(searchEnginePath)

  var auctionsMap = collection.mutable.Map[String, ActorRef]()
  var i = 0

  auctions.foreach(name => {
    val auctionActor = context.actorOf(Props(new FSMAuctionActor(name, self, notifier)), s"seller:${sellerID}_auction:$i")
    auctionsMap += (name.toLowerCase -> auctionActor)
    masterSearchEngine ! Register(name, auctionActor)

    i = i + 1
  })

  override def receive: Receive = {
    case itemSold(name, amount, winner) =>
      println(s"I (${self.path.name}) sold item $name for $amount to ${winner.path.name}.")
      masterSearchEngine ! Unregister(name)
      auctionsMap -= name

    case Done =>  // For testing purposes only
  }

}

object SellerActor {

  case class itemSold(name: String, amount: BigInt, winner: ActorRef)

}
