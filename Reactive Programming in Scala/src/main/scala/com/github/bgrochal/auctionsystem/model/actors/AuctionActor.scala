package com.github.bgrochal.auctionsystem.model.actors

import akka.actor.ActorRef

/**
  * @author Bartłomiej Grochal
  */

object AuctionActor {

  sealed trait Command

  case object relist extends Command
  case object bidTimerExceeded extends Command

  case class bidUp(auction: ActorRef, currentAmount: BigInt) extends Command
  case class auctionWon(auction: ActorRef, name: String) extends Command

  case class offerBid(amount: BigInt) extends Command {
    require(amount > 0)
  }

}
