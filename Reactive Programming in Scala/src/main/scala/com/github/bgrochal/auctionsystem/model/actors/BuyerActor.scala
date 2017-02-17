package com.github.bgrochal.auctionsystem.model.actors

import akka.actor.{Actor, ActorRef}
import com.github.bgrochal.auctionsystem.model.actors.AuctionActor.{auctionWon, bidUp, offerBid}
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Search, SearchResult}

import scala.util.Random

/**
  * @author Bartłomiej Grochal
  */
class BuyerActor(val auctionKeys: List[String], val maxAmount: BigInt,
                 val searchEnginePath: String = "/user/auctionSystemActor/masterSearchActor") extends Actor {

  val masterSearchEngine = context.actorSelection(searchEnginePath)
  masterSearchEngine ! Search(auctionKeys)

  var auctions = Set[(ActorRef, ActorRef)]()

  override def receive: Receive = {
    case SearchResult(result) =>
      auctions = auctions ++ result
      result.map(result => result._1).foreach(_ ! offerBid(getRandomAmount))

    case bidUp(auction, currentAmount) =>
      if (currentAmount < maxAmount) {
        Thread.sleep(500 + Random.nextInt(500))
        auction ! offerBid(getNewOffer(currentAmount))
      }

    case auctionWon(auction, name) =>
      println(s"I (${self.path.name}) won the auction $name.")
  }

  def getRandomAmount: BigInt = {
    500 + Random.nextInt(200)
  }

  def getNewOffer(currentAmount: BigInt): BigInt = {
    val newOffer = currentAmount + 50 + Random.nextInt(200)
    if (newOffer < maxAmount) newOffer else maxAmount
  }

}
