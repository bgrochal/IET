package com.github.bgrochal.auctionsystem.model.actors

import akka.actor.ActorRef
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import com.github.bgrochal.auctionsystem.model.actors.AuctionActor.{auctionWon, bidTimerExceeded, bidUp, offerBid}
import com.github.bgrochal.auctionsystem.model.actors.FSMAuctionPersistentActor.{AuctionDataUpdated, DomainEvent, FSMData, PersistentFSMData, _}
import com.github.bgrochal.auctionsystem.model.actors.SellerActor.itemSold

import scala.collection.immutable.Seq
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.reflect._

/**
  * @author Barłomiej Grochal
  */
class FSMAuctionPersistentActor(val name: String, val seller: ActorRef) extends PersistentFSM[State, FSMData, DomainEvent] {

  override def persistenceId: String =
    "persistent-auction-" + name.replace(' ', '-')

  override def domainEventClassTag: ClassTag[DomainEvent] =
    classTag[DomainEvent]


  println(s"Auction created; name: $name.")

  startWith(Created, PersistentFSMData(AuctionState(0, null), "Created", 0))

  var bidTime: Long = 5000
  var startTime: Long = System.currentTimeMillis()

  def getTime: Long = {
    System.currentTimeMillis() - startTime
  }


  when(Created, stateTimeout = 5 seconds) {
    case Event(offer: offerBid, state: PersistentFSMData) =>
      println(s"First offer for the auction: $name, ${sender.path.name}, ${offer.amount}.")
      goto(Activated) applying AuctionDataUpdated(PersistentFSMData(AuctionState(offer.amount, sender()), "Activated", getTime))

    case Event(AuctionActor.bidTimerExceeded, state: PersistentFSMData) =>
      println("Auction ignored.")
      goto(Ignored) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Ignored", getTime))

    case Event(StateTimeout, state: PersistentFSMData) =>
      println("Auction ignored.")
      goto(Ignored) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Ignored", getTime))
  }

  when(Ignored, stateTimeout = 2 seconds) {
    case Event(StateTimeout, state: PersistentFSMData) =>
      println("Ignored auction finalized.")
      goto(Finalized) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Finalized", getTime))

    case Event(relist, state: PersistentFSMData) =>
      println("Auction relisted.")
      context.system.scheduler.scheduleOnce(5 seconds, self, bidTimerExceeded)
      goto(Created) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Created", getTime))
  }

  when(Activated) {
    case Event(offer: offerBid, state: PersistentFSMData) =>
      println(s"New offer received: $name, ${sender.path.name}, ${offer.amount}.")
      if (offer.amount > state.auctionState.amount) {
        state.auctionState.winner ! bidUp(self, offer.amount)
        stay() applying AuctionDataUpdated(PersistentFSMData(AuctionState(offer.amount, sender()), "Activated", getTime))
      } else {
        sender() ! bidUp(self, state.auctionState.amount)
        stay replying state
      }

    case Event(bidTimerExceeded, state: PersistentFSMData) =>
      println(s"Auction finished - subject sold: $name, ${state.auctionState.winner.path.name}, ${state.auctionState.amount}.")
      state.auctionState.winner ! auctionWon(self, name)
      seller ! itemSold(name, state.auctionState.amount, state.auctionState.winner)
      goto(Sold) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Sold", getTime))
  }

  when(Sold, stateTimeout = 2 seconds) {
    case Event(StateTimeout, state: PersistentFSMData) =>
      println("Finished auction finalized.")
      if (persistenceTimes.nonEmpty)
        println(s"Average persistence time: ${persistenceTimes.sum / persistenceTimes.length}")

      goto(Finalized) applying AuctionDataUpdated(PersistentFSMData(state.auctionState, "Finalized", getTime))
  }

  when(Finalized) {
    case Event(_, _) =>
      context.stop(self)
      stop()
  }


  var persistenceTimes = new ListBuffer[Long]()

  /* Note: it is a kind of hack for time measurement. */
  override def persistAll[A](events: Seq[A])(handler: (A) => Unit): Unit = {
    val persistStartTime: Long = System.currentTimeMillis()

    super.persistAll(events)((x: A) => {
      persistenceTimes += (System.currentTimeMillis() - persistStartTime)
      handler(x)
    })

  }

  var persistedEvents = 0
  var recoveryTime = System.currentTimeMillis()

  override def onRecoveryCompleted(): Unit = {
    super.onRecoveryCompleted()
    recoveryTime = System.currentTimeMillis() - recoveryTime

    if (bidTime > 0) {
      println(s"Bid timer set to $bidTime ms.")
      context.system.scheduler.scheduleOnce(bidTime milliseconds, self, bidTimerExceeded)
    } else {
      println("Bid timer not set.")
    }

    if (persistedEvents > 0)
      println(s"Average recovery time: ${recoveryTime / persistedEvents}")
  }

  override def applyEvent(event: DomainEvent, dataBefore: FSMData): FSMData = {
    event match {
      case AuctionDataUpdated(updatedData: PersistentFSMData) =>
        bidTime = bidTime + dataBefore.asInstanceOf[PersistentFSMData].time - updatedData.time
        persistedEvents += 1

        println(s"# Event: $dataBefore => $updatedData")
        updatedData
    }
  }

}

object FSMAuctionPersistentActor {

  /* States of the FSM. */
  sealed trait State extends FSMState

  case object Created extends State {
    override def identifier: String = "Created"
  }

  case object Ignored extends State {
    override def identifier: String = "Ignored"
  }

  case object Activated extends State {
    override def identifier: String = "Activated"
  }

  case object Sold extends State {
    override def identifier: String = "Sold"
  }

  case object Finalized extends State {
    override def identifier: String = "Finalized"
  }


  /* Internal state of the FSM. */
  sealed trait FSMData

  case class AuctionState(amount: BigInt, winner: ActorRef) extends FSMData {
    override def toString: String =
      if (winner != null)
        s"($amount, ${winner.path.name})"
      else
        s"($amount, $winner)"
  }

  case class PersistentFSMData(auctionState: AuctionState, stateName: String, time: Long) extends FSMData {
    override def toString: String =
      s"(${auctionState.toString}, $stateName, $time)"
  }


  /* Domain events for "recording" the sequence of persisting events. */
  sealed trait DomainEvent

  case class AuctionDataUpdated(updatedData: PersistentFSMData) extends DomainEvent

}
