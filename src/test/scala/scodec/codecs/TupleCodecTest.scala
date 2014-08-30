package scodec
package codecs

import scalaz.\/-
import scodec.bits.{ BitVector, ByteVector }

class TupleCodecTest extends CodecSuite {

  "tuple codec support" should {
    "generate left nested tuples" in {
      (uint8 ~ uint8 ~ uint8).encode(1 ~ 2 ~ 3) shouldBe \/-(BitVector(1, 2, 3))
    }

    "provide a type alias for left nested tuples" in {
      (uint8 ~ uint8 ~ ascii): Codec[Int ~ Int ~ String]
    }

    "roundtrip" in {
      roundtripAll(uint16 ~ uint16, Seq((0, 0), (0, 1), (65535, 42)))
      roundtripAll(uint16 ~ uint16 ~ uint32, Seq(((0, 0), 1L << 32 - 1), ((0, 1), 20L), ((65535, 42), 5L)))
      roundtripAll(uint(2) ~ uint4 ~ uint(2), Seq(((0, 15), 0)))
    }

    "allow extraction via ~ operator" in {
      (uint8 ~ uint8 ~ uint8).decode(BitVector(24, 255, 14)) match {
        case \/-((rest, a ~ b ~ c)) =>
          rest should be ('empty)
          a should be (24)
          b should be (255)
          c should be (14)
      }
      (uint8 ~ uint8 ~ uint8).decodeValue(BitVector(1, 2, 3)) map { case a ~ b ~ c => a + b + c } should be (\/-(6))
    }

    "allow function application of N args to nested tuples generated by ~" in {
      val bytes = BitVector(ByteVector.fill(20)(1))

      val add2 = (_: Int) + (_: Int)
      (uint8 ~ uint8).decodeValue(bytes) map add2 shouldBe \/-(2)

      val add3 = (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add3 shouldBe \/-(3)

      val add4 = (_: Int) + (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add4 shouldBe \/-(4)

      val add5 = (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add5 shouldBe \/-(5)

      val add6 = (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add6 shouldBe \/-(6)

      val add7 = (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add7 shouldBe \/-(7)

      val add8 = (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int) + (_: Int)
      (uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8 ~ uint8).decodeValue(bytes) map add8 shouldBe \/-(8)
    }
  }
}
