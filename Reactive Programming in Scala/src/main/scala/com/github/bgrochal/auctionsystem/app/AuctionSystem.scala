package com.github.bgrochal.auctionsystem.app

import akka.actor.{ActorSystem, Props}
import com.github.bgrochal.auctionsystem.remote.actors.AuctionPublisherActor
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * @author Bartłomiej Grochal
  */
object AuctionSystem extends App {

  val configuration = ConfigFactory.load()

  val clientActorSystem = ActorSystem("AuctionSystem", configuration.getConfig("clientapp").withFallback(configuration))
  val serverActorSystem = ActorSystem("AuctionSystem", configuration.getConfig("serverapp").withFallback(configuration))

  val auctionSystemActor = clientActorSystem.actorOf(Props[AuctionSystemActor], "auctionSystemActor")
  val auctionPublisherActor = serverActorSystem.actorOf(Props[AuctionPublisherActor], "auctionPublisherActor")

  auctionSystemActor ! AuctionSystemActor.initAuctionSystem

  Await.result(clientActorSystem.whenTerminated, Duration.Inf)
  Await.result(serverActorSystem.whenTerminated, Duration.Inf)

}
