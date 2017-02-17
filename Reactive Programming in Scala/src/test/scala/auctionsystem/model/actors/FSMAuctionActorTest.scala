package auctionsystem.model.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit, TestProbe}
import com.github.bgrochal.auctionsystem.model.actors.AuctionActor.{bidUp, offerBid, relist}
import com.github.bgrochal.auctionsystem.model.actors.FSMAuctionActor
import com.github.bgrochal.auctionsystem.model.actors.FSMAuctionActor.{Activated, Created, Ignored}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


/**
  * @author Bartłomiej Grochal
  */
class FSMAuctionActorTest extends TestKit(ActorSystem("AuctionActorTest")) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  "FSM Auction Actor" must {

    "change state to Activated when first offer received" in {
      val buyerProbe = TestProbe()
      val sellerProbe = TestProbe()
      val notifierProbe = TestProbe()
      val underTest = TestFSMRef(new FSMAuctionActor("Test auction", sellerProbe.ref, notifierProbe.ref))

      underTest.tell(offerBid(1), buyerProbe.ref)
      underTest.stateName shouldBe Activated
    }

    //    Only one timeout scenario tested because of time reasons
    "change state to Ignored when no offer received" in {
      val sellerProbe = TestProbe()
      val notifierProbe = TestProbe()
      val underTest = TestFSMRef(new FSMAuctionActor("Test auction", sellerProbe.ref, notifierProbe.ref))

      awaitAssert(underTest.stateName shouldBe Ignored, 7 seconds, 1 second)
    }

    "be back after relisting" in {
      val sellerProbe = TestProbe()
      val notifierProbe = TestProbe()
      val underTest = TestFSMRef(new FSMAuctionActor("Test auction", sellerProbe.ref, notifierProbe.ref))

      underTest.setState(Ignored)
      underTest.stateName shouldBe Ignored
      underTest ! relist
      underTest.stateName shouldBe Created
    }

    "notify current winner that has been bid up" in {
      val firstBuyerProbe = TestProbe()
      val secondBuyerProbe = TestProbe()
      val sellerProbe = TestProbe()
      val notifierProbe = TestProbe()
      val underTest = TestFSMRef(new FSMAuctionActor("Test auction", sellerProbe.ref, notifierProbe.ref))

      underTest.stateData.amount shouldBe 0
      underTest.tell(offerBid(10), firstBuyerProbe.ref)
      underTest.stateData.amount shouldBe 10
      underTest.tell(offerBid(20), secondBuyerProbe.ref)
      underTest.stateData.amount shouldBe 20
      firstBuyerProbe.expectMsgPF() {
        case bidUp(auction, amount) =>
          auction shouldBe underTest
          amount shouldBe 20
      }
    }

  }

}
