package com.github.bgrochal.auctionsystem.search.actors

import akka.actor.{Actor, ActorRef}
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor._

/**
  * @author Bartłomiej Grochal
  */
class AuctionSearchActor extends Actor {

  var registeredAuctions = collection.mutable.Map[String, (ActorRef, ActorRef)]()
  println(s"Auction search actor (${self.path.name}) created.")

  override def receive: Receive = {
    case Register(name, auctionActor) =>
      println(s"Auction registered by ${self.path.name}; name: $name, seller: $sender")
      registeredAuctions += (name.toLowerCase() -> (auctionActor, sender))
      sender ! Done // For testing purposes only

    case Unregister(name) =>
      println(s"Auction unregistered by ${self.path.name}; name: $name, by: $sender")
      registeredAuctions -= name.toLowerCase()

    case Search(keys) =>
      println(s"Auction search request handled by ${self.path.name}.")
      var results = Set[(ActorRef, ActorRef)]()
      keys.map(key => key.toLowerCase()).foreach(key =>
        results = results ++ registeredAuctions.filterKeys(_.contains(key.toLowerCase())).values.toSet)
      sender ! SearchResult(results)
  }

}

object AuctionSearchActor {

  case class Register(name: String, auctionActor: ActorRef)

  case class Unregister(name: String)

  case class Search(keys: List[String])

  case class SearchResult(result: Set[(ActorRef, ActorRef)])

  case object Done // For testing purposes only

}
