package u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

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

  test("PNP should work as a normal Petri Net if transitions have the same priority"):
    val testPNP = PetriNetWithPriority[TestPlace, Int](
      0 | MSet(P1) ~~> MSet(P2),
      0 | MSet(P1) ~~> MSet(P3),
      0 | MSet(P2) ~~> MSet(P1),
      0 | MSet(P2) ~~> MSet(P3),
      0 | MSet(P3) ~~> MSet(P1),
      0 | MSet(P3) ~~> MSet(P2),
    ).toSystem

    val expected1 = List(MSet(P1), MSet(P2), MSet(P3))
    val expected2 = List(MSet(P1), MSet(P3), MSet(P1))
    val expected3 = List(MSet(P1), MSet(P2), MSet(P1))
    val expected4 = List(MSet(P1), MSet(P3), MSet(P2))

    testPNP.paths(MSet(P1), 3).toSet should be:
      Set(expected1, expected2, expected3, expected4)

  test("PNP should work with inhibitor arcs"):
    val testPNP = PetriNetWithPriority[TestPlace, Int](
      0 | MSet(P3) ~~> MSet(),
      1 | MSet(P1) ~~> MSet(P2, P3) ^^^ MSet(P3),
    ).toSystem

    val expected = List(MSet(P1, P1), MSet(P1, P2, P3), MSet(P1, P2), MSet(P2, P2, P3), MSet(P2, P2))

    testPNP.paths(MSet(P1, P1), 5).toSet should be:
      Set(expected)
