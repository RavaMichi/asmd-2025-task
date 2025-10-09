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
      MSet(P1) ~~> MSet(P2) >> when { case Red(_) => true },
      MSet(P2) ~~> MSet(P3),
    ).toSystem

    val expected = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P2 -> Red(0), P1 -> Blue("")), MSet(P3 -> Red(0), P1 -> Blue("")))

    testCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet should be:
      Set(expected)

  test("CPN should update the color when firing a transition"):

    val testCPN = ColoredPetriNet[Place, Color](
      MSet(P1) ~~> MSet(P2) >> onTransition {
        case Seq(Red(i)) => Red(i + 1)
        case Seq(Blue(s)) => Blue(s + "+")
      },
    ).toSystem

    val expected1 = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P2 -> Red(1), P1 -> Blue("")), MSet(P2 -> Red(1), P2 -> Blue("+")))
    val expected2 = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P1 -> Red(0), P2 -> Blue("+")), MSet(P2 -> Red(1), P2 -> Blue("+")))

    testCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet should be:
      Set(expected1, expected2)


  test("CPN should throw an error if it tries to apply an incomplete function"):

    val incompleteCPN = ColoredPetriNet[Place, Color](
      MSet(P1, P1) ~~> MSet(P2) >> onTransition { case Seq(Red(i)) => Red(i + 1) }, // error: incomplete transformation
    ).toSystem

    an [IllegalArgumentException] should be thrownBy:
      incompleteCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet

  test("CPN should throw an error if it tries to apply too many parameters to the function"):

    val fewParamsCPN = ColoredPetriNet[Place, Color](
      MSet(P1) ~~> MSet(P2) >> onTransition(_(3)), // error: too many params accessed
    ).toSystem

    an [IndexOutOfBoundsException] should be thrownBy :
      fewParamsCPN.paths(MSet(P1 -> Red(0)), 3).toSet