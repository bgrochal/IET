package com.github.bgrochal.auctionsystem.remote.actors

import akka.actor.Actor
import com.github.bgrochal.auctionsystem.remote.actors.NotifierActor.notification

/**
  * @author Bartłomiej Grochal
  */
class AuctionPublisherActor extends Actor {

  override def receive: Receive = {
    case notification(auction, currentWinner, currentAmount) =>
      println(s"publishing: [$auction, ${currentWinner.path.name}, $currentAmount] from ${sender.path.name}")
  }

}
