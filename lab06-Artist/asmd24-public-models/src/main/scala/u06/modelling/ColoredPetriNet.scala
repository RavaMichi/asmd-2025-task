package u06.modelling

import u06.utils.MSet

object ColoredPetriNet:
  import PetriNet.*
  case class ColoredTrn[P, C](transition: Trn[P], guard: C => Boolean, transform: Seq[C] => C)
  type ColoredPetriNet[P, C] = Set[ColoredTrn[P, C]]

  def apply[P, C](transitions: ColoredTrn[P, C]*): ColoredPetriNet[P, C] = transitions.toSet

  // helper function
  def combinations[A](list: Set[A], size: Int): Set[Set[A]] = size match
    case 0 => Set()
    case 1 => list.map(Set(_))
    case s =>
      for
        x <- list
        comb <- combinations(list.filter(x != _), s - 1)
      yield comb + x

  def computeColorsOfPrecondition[P, C](state: List[(P, C)], cond: List[P]): Set[MSet[(P, C)]] =
    state
      .filter(x => cond.contains(x._1))
      .flatMap(x => cond diff Seq(x._1) match
        case List() => Set(MSet(x))
        case m => computeColorsOfPrecondition(state diff Seq(x), m).map(MSet(x).union(_))
      ).toSet

  extension [P, C](cpn: ColoredPetriNet[P, C])
    def toSystem: System[Marking[(P, C)]] = m =>
      def isInhibited(t: Trn[P]): Boolean = m.asList.map(_._1).exists(t.inh.asList.contains)
      cpn
        .map { case ColoredTrn(transition, guard, transform) => (transition, guard, transform) }
        .map((tr, g, f) =>
          val cond = m.asList.filter((p, c) => tr.cond.asList.contains(p) && g(c))
          println(cond)
          if isInhibited(tr) || cond.isEmpty then
            List.empty
          else
            val eff = tr.eff.asList.map(p => (p, f(cond.map(_._2))))
            println(eff)
            m.asList.filterNot(cond.contains) ++ eff
        ).filterNot(_.isEmpty)
        .map(MSet.ofList)

  given coloredTrnConversion[P, C]: Conversion[Trn[P], ColoredTrn[P, C]] with
    override def apply(x: Trn[P]): ColoredTrn[P, C] = ColoredTrn(x, c => true, _.head)



  enum ColorAttribute[C]:
    case Guard(guard: C => Boolean)
    case OnTransition(tr: Seq[C] => C)

  import ColorAttribute.*
  def when[C](guard: PartialFunction[C, Boolean]): Guard[C] = Guard(c => guard.applyOrElse(c, _ => false))
  infix def onTransition[C](tr: Seq[C] => C): OnTransition[C] = OnTransition(tr)

  extension [P](tr: Trn[P])
    infix def |[C](cc: ColorAttribute[C]*): ColoredTrn[P, C] =
      ColoredTrn(
        tr,
        c => cc.collect { case x: Guard[C] => x }.forall(_.guard(c)),
        cc.collectFirst { case x: OnTransition[C] => x }.map(_.tr).getOrElse(_.head)
      )
