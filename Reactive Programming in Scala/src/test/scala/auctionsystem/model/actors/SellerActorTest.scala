package auctionsystem.model.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.github.bgrochal.auctionsystem.model.actors.SellerActor
import com.github.bgrochal.auctionsystem.model.actors.SellerActor.itemSold
import com.github.bgrochal.auctionsystem.search.actors.AuctionSearchActor.{Register, Unregister}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * @author Bartłomiej Grochal
  */
class SellerActorTest extends TestKit(ActorSystem("SellerActorTest")) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  "Seller Actor" must {

    "send registration messages when auction created" in {
      val searchEngineProbe = TestProbe()
      TestActorRef(new SellerActor("test", List("Test auction"), null, searchEngineProbe.ref.path.toString))
      searchEngineProbe.expectMsgType[Register]
    }

    "save created auctions in map" in {
      val underTest = TestActorRef(new SellerActor("test", List("Test auction", "Test auction 2"), null))
      underTest.underlyingActor.auctionsMap.size should be(2)
    }

    "ignore case in auction names" in {
      val underTest = TestActorRef(new SellerActor("test", List("Test auction", "TEST AUCTION"), null))
      underTest.underlyingActor.auctionsMap.size should be(1)
    }

    "unregister finalized auction" in {
      val auctionName = "Test auction"
      val buyerProbe = TestProbe()
      val searchEngineProbe = TestProbe()
      val underTest = TestActorRef(new SellerActor("test", List(auctionName), null, searchEngineProbe.ref.path.toString))
      searchEngineProbe.expectMsgType[Register]

      underTest.tell(itemSold(auctionName, 1, buyerProbe.ref), buyerProbe.ref)
      searchEngineProbe.expectMsgPF() {
        case Unregister(name) =>
          name shouldBe auctionName
      }
    }

  }

}
