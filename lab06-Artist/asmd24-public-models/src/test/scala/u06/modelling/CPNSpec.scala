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

  test("testing combinations"):
//    extension [P, C](cpn: ColoredPetriNet[P, C])
//      def asSystem: System[Marking[(P, C)]] = m =>
//        def places: MSet[(P, C)] => MSet[P] = x => MSet.ofList(x.asList.map(_._1))
//        def colors: MSet[(P, C)] => MSet[C] = x => MSet.ofList(x.asList.map(_._2))
//        for
//          ColoredTrn(Trn(cond, eff, inh), guard, f) <- cpn   // get any transition
//          if inh disjoined places(m)
//          token <- cond.asList
//          (_, color) <- m.asList.find(_ == token)
//          if guard(color) // color guard
//
//        yield ???

    def computeColorsOfPrecondition[P, C](state: List[(P, C)], cond: List[P]): Set[MSet[(P, C)]] =
      state
        .flatMap(x => cond.find(_ == x._1).map(c => x))
        .flatMap(x =>
          val newCond = cond diff Seq(x._1)
          if newCond.isEmpty then
            Set(MSet(x))
          else
            val newState = state diff Seq(x)
            computeColorsOfPrecondition(newState, newCond)
              .map(_ union MSet(x))
        ).toSet

    val state = MSet(P1 -> Red(0), P1 -> Blue(""), P2 -> Red(2), P2 -> Red(1), P3 -> Blue(""))
    val cond = MSet(P2, P1, P3)
    val x = computeColorsOfPrecondition(state.asList, cond.asList)
    println(x)

  
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
      MSet(P1) ~~> MSet(P2) | when { case Red(_) => true },
      MSet(P2) ~~> MSet(P3),
    ).toSystem

    val expected = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P2 -> Red(0), P1 -> Blue("")), MSet(P3 -> Red(0), P1 -> Blue("")))

    testCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet should be:
      Set(expected)

  test("CPN should update the color when firing a transition"):

    val testCPN = ColoredPetriNet[Place, Color](
      MSet(P1) ~~> MSet(P2) | onTransition {
        case Seq(Red(i)) => Red(i + 1)
        case Seq(Blue(s)) => Blue(s + "+")
      },
    ).toSystem

    val expected1 = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P2 -> Red(1), P1 -> Blue("")), MSet(P2 -> Red(1), P2 -> Blue("+")))
    val expected2 = List(MSet(P1 -> Red(0), P1 -> Blue("")), MSet(P1 -> Red(0), P2 -> Blue("+")), MSet(P2 -> Red(1), P2 -> Blue("+")))

    testCPN.paths(MSet(P1 -> Red(0), P1 -> Blue("")), 3).toSet should be:
      Set(expected1, expected2)

