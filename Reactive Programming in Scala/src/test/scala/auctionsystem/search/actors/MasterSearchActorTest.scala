package auctionsystem.search.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import auctionsystem.search.actors.HelperActor._
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Done, Register, Search, SearchResult}
import com.github.bgrochal.auctionsystem.search.actors.MasterSearchActor
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random


/**
  * @author Bartłomiej Grochal
  */
class MasterSearchActorTest extends TestKit(ActorSystem("MasterSearchActorTest")) with WordSpecLike with Matchers
  with BeforeAndAfterAll with ImplicitSender {

  "Master Search Actor" must {
    "route requests to Auction Search actors" in {
      val helperActor = TestActorRef[HelperActor]

      implicit val timeout = Timeout(300 seconds)
      val future = helperActor ? Init
      val result = Await.result(future, timeout.duration)

      Await.result(system.whenTerminated, Duration.Inf)
    }
  }

}


class HelperActor extends Actor {

  val auctionSearchActorInstances = 8
  val underTest = context.actorOf(Props(new MasterSearchActor(auctionSearchActorInstances)))

  val auctions = ListBuffer.empty[String]
  var registered = 0
  var searched = 0

  var time: Long = 0

  var testScopeActor: ActorRef = _


  override def receive: Receive = {
    case Init =>
      testScopeActor = sender
      self ! CreateAuctions

    case CreateAuctions =>
      var index = 0
      for (index <- 0 to 49999) {
        auctions += s"auction_$index"
      }
      self ! RegisterAuctions

    case RegisterAuctions =>
      auctions.foreach(name => {
        underTest ! Register(name, null)
      })

    case Done =>
      registered = registered + 1
      if (registered == auctionSearchActorInstances * 50000) {
        self ! SearchAuctions
      }

    case SearchAuctions =>
      val generatedIndices = Random.shuffle((1 to 50000).toList).take(10000)
      time = System.currentTimeMillis()
      generatedIndices.foreach(index => {
        underTest ! Search(List(auctions(index - 1)))
      })

    case SearchResult(results) =>
      searched = searched + 1
      if (searched == 10000) {
        time = System.currentTimeMillis() - time
        println(s"Searching time: $time.")
        testScopeActor ! TestDone
      }
  }
}

object HelperActor {

  case object Init

  case object CreateAuctions

  case object RegisterAuctions

  case object SearchAuctions

  case object TestDone

}
