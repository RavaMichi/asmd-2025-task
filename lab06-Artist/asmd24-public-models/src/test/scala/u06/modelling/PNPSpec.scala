package u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import scala.runtime.RichInt
class PNPSpec extends AnyFunSuite:

  import PetriNet.*
  import PetriNetWithPriority.*
  import u06.utils.*
  import SystemAnalysis.*

  enum TestPlace:
    case P1, P2, P3

  import TestPlace.*

  test("PNP should first fire transitions with higher priority"):

    val testPNP = PetriNetWithPriority[TestPlace, Int](
      0 | MSet(P1) ~~> MSet(P2),
      1 | MSet(P2) ~~> MSet(P3),
    ).toSystem

    val expected = List(MSet(P1, P1, P1), MSet(P2, P1, P1), MSet(P3, P1, P1), MSet(P2, P3, P1), MSet(P3, P3, P1), MSet(P2, P3, P3), MSet(P3, P3, P3))

    testPNP.paths(MSet(P1, P1, P1),7).toSet should be:
      Set(expected)
