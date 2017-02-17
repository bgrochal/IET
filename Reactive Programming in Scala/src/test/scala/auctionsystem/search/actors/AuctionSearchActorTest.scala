package auctionsystem.search.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Register, Search, SearchResult, Unregister}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * @author Bartłomiej Grochal
  */
class AuctionSearchActorTest extends TestKit(ActorSystem("AuctionSearchActorTest")) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  "Auction Search Actor" must {

    "save registered auction in map" in {
      val underTest = TestActorRef[AuctionSearchActor]
      val auctionProbe = TestProbe()
      val sellerProbe = TestProbe()
      val auctionName = "Test name"

      underTest.underlyingActor.registeredAuctions shouldBe empty
      underTest.tell(Register(auctionName, auctionProbe.ref), sellerProbe.ref)
      underTest.underlyingActor.registeredAuctions should contain(auctionName.toLowerCase, (auctionProbe.ref, sellerProbe.ref))
    }

    "unregister saved auction" in {
      val underTest = TestActorRef[AuctionSearchActor]
      val auctionProbe = TestProbe()
      val sellerProbe = TestProbe()
      val auctionName = "Test name"

      underTest.tell(Register(auctionName, auctionProbe.ref), sellerProbe.ref)
      underTest.underlyingActor.registeredAuctions.size should be(1)
      underTest.tell(Unregister(auctionName), auctionProbe.ref)
      underTest.underlyingActor.registeredAuctions.size should be(0)
    }

    "return empty response when no match found" in {
      val underTest = TestActorRef[AuctionSearchActor]
      val auctionProbe = TestProbe()
      val sellerProbe = TestProbe()
      val buyerProbe = TestProbe()
      val auctionName = "Test name"

      underTest.tell(Register(auctionName, auctionProbe.ref), sellerProbe.ref)
      underTest.tell(Search(List("no", "key", "to", "find")), buyerProbe.ref)
      buyerProbe.expectMsgPF() {
        case SearchResult(result) =>
          result shouldBe empty
      }
    }

    "return non-empty response when a match found" in {
      val underTest = TestActorRef[AuctionSearchActor]
      val auctionProbe = TestProbe()
      val sellerProbe = TestProbe()
      val buyerProbe = TestProbe()
      val auctionName = "Test name"

      underTest.tell(Register(auctionName, auctionProbe.ref), sellerProbe.ref)
      underTest.tell(Search(List("TEST")), buyerProbe.ref)
      buyerProbe.expectMsgPF() {
        case SearchResult(result) =>
          result.size should be(1)
          result.head should be(auctionProbe.ref, sellerProbe.ref)
      }
    }

  }

}
