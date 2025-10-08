package u06.modelling

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import u06.utils.MSet
import SystemAnalysis.*

class CPNSpec extends AnyFunSuite:

  enum Place:
    case P1, P2, P3

  enum Color:
    case Red(i: Int)
    case Blue(s: String)
    
  import PetriNet.*
  import ColoredPetriNet.*
  import Place.*
  import Color.*
  
  test("CPN should be able to move token without changing their color"):
    
    val testCPN = ColoredPetriNet[Place, Color](
      MSet(P1) ~~> MSet(P2),
      MSet(P2) ~~> MSet(P3),
    ).toSystem
    
    val expected = List(MSet(P1 -> Red(0)), MSet(P2 -> Red(0)), MSet(P3 -> Red(0)))

    testCPN.paths(MSet(P1 -> Red(0)), 3).toSet should be:
      Set(expected)

  test("CPN should enable transitions when guard check passed"):
    val testCPN = ColoredPetriNet[Place, Color](
      MSet(P1) ~~> MSet(P2) when (c => c.isInstanceOf[Red]),
      MSet(P2) ~~> MSet(P3),
    ).toSystem

    val expected = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P2 -> Red(0), P1 -> Blue("")), MSet(P3 -> Red(0), P1 -> Blue("")))

    testCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet should be:
      Set(expected)