package com.github.bgrochal.auctionsystem.model.actors

import akka.actor.{ActorRef, FSM}
import com.github.bgrochal.auctionsystem.model.actors.AuctionActor.{auctionWon, bidTimerExceeded, bidUp, offerBid}
import com.github.bgrochal.auctionsystem.model.actors.FSMAuctionActor._
import com.github.bgrochal.auctionsystem.model.actors.SellerActor.itemSold
import com.github.bgrochal.auctionsystem.remote.actors.NotifierActor.notification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * @author Bartłomiej Grochal
  */
class FSMAuctionActor(val name: String, val seller: ActorRef, val notifier: ActorRef) extends
  FSM[State, FSMAuctionActor.AuctionState] {

  println(s"Auction created; name: $name.")

  context.system.scheduler.scheduleOnce(5 seconds, self, bidTimerExceeded)
  startWith(Created, FSMAuctionActor.AuctionState(0, null))

  when(Created, stateTimeout = 5 seconds) {
    case Event(offer: offerBid, state: FSMAuctionActor.AuctionState) =>
      println(s"First offer for the auction: $name, ${sender.path.name}, ${offer.amount}.")
      notifier ! notification(name, sender, offer.amount)
      goto(Activated) using FSMAuctionActor.AuctionState(offer.amount, sender())

    case Event(AuctionActor.bidTimerExceeded, _) | Event(StateTimeout, _) =>
      println("Auction ignored.")
      goto(Ignored)
  }

  when(Ignored, stateTimeout = 2 seconds) {
    case Event(StateTimeout, _) =>
      println("Ignored auction finalized.")
      goto(Finalized)

    case relist =>
      println("Auction relisted.")
      context.system.scheduler.scheduleOnce(5 seconds, self, bidTimerExceeded)
      goto(Created)
  }

  when(Activated) {
    case Event(offer: offerBid, state: FSMAuctionActor.AuctionState) =>
      println(s"New offer received: $name, ${sender.path.name}, ${offer.amount}.")
      if (offer.amount > state.amount) {
        state.winner ! bidUp(self, offer.amount)
        notifier ! notification(name, sender, offer.amount)
        stay() using FSMAuctionActor.AuctionState(offer.amount, sender())
      } else {
        sender() ! bidUp(self, state.amount)
        stay()
      }

    case Event(bidTimerExceeded, state: FSMAuctionActor.AuctionState) =>
      println(s"Auction finished - subject sold: $name, ${state.winner.path.name}, ${state.amount}.")
      state.winner ! auctionWon(self, name)
      seller ! itemSold(name, state.amount, state.winner)
      goto(Sold)
  }

  when(Sold, stateTimeout = 2 seconds) {
    case Event(StateTimeout, _) =>
      println("Finished auction finalized.")
      goto(Finalized)
  }

  when(Finalized) {
    case Event(_, _) =>
      context.stop(self)
      stop()
  }

  initialize()

}

object FSMAuctionActor {

  /* States of the FSM. */
  sealed trait State

  case object Created extends State

  case object Ignored extends State

  case object Activated extends State

  case object Sold extends State

  case object Finalized extends State

  /* Internal state of the FSM. */
  final case class AuctionState(amount: BigInt, winner: ActorRef)

}
