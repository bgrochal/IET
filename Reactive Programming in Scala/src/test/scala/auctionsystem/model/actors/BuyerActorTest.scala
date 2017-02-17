package auctionsystem.model.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.github.bgrochal.auctionsystem.model.actors.AuctionActor.{bidUp, offerBid}
import com.github.bgrochal.auctionsystem.model.actors.BuyerActor
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * @author Bartłomiej Grochal
  */
class BuyerActorTest extends TestKit(ActorSystem("BuyerActorTest")) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  "Buyer Actor" must {

    "React on bid up" in {
      val auctionProbe = TestProbe()
      val underTest = TestActorRef(new BuyerActor(List("Test"), 10))

      underTest.tell(bidUp(auctionProbe.ref, 1), auctionProbe.ref)
      auctionProbe.expectMsgType[offerBid]
      auctionProbe.lastSender shouldBe underTest
    }

  }

}
