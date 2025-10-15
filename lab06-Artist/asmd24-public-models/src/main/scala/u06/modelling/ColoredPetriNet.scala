package u06.modelling

import u06.utils.MSet

object ColoredPetriNet:
  import PetriNet.*
  case class ColoredTrn[P, C](transition: Trn[P], guard: Option[C => Boolean], transform: Seq[C] => C)
  type ColoredPetriNet[P, C] = Set[ColoredTrn[P, C]]

  def apply[P, C](transitions: ColoredTrn[P, C]*): ColoredPetriNet[P, C] = transitions.toSet

  // helper function
  private def preconditionWithColors[P, C](state: List[(P, C)], cond: List[P]): Set[MSet[(P, C)]] =
    // compute all possible combinations of places and colors \in state and \in cond
    state
      .filter(x => cond.contains(x._1))
      .flatMap(x => cond diff Seq(x._1) match
        case List() => Set(MSet(x))
        case m => preconditionWithColors(state diff Seq(x), m).map(MSet(x).union(_))
      ).toSet
  private def effectWithColors[P, C](eff: List[P], cond: List[(P, C)])(function: Seq[C] => C): MSet[(P, C)] =
    // compute the color of the effects
    MSet.ofList(eff.map(e => (e, function(cond.map(_._2)))))

  extension [P, C](cpn: ColoredPetriNet[P, C])
    def toSystem: System[Marking[(P, C)]] = m =>
      def isNotInhibited(t: Trn[P]) = !m.asList.map(_._1).exists(t.inh.asList.contains)
      def passGuard(s: MSet[(P, C)], guard: Option[C => Boolean]) = s.asList.map(_._2).forall(guard.getOrElse(_ => true))
      for
        ColoredTrn(trn, guard, f) <- cpn
        if isNotInhibited(trn) // check inhibition
        cond <- preconditionWithColors(m.asList, trn.cond.asList)
        if passGuard(cond, guard) // check color guard
        out <- m extract cond
      yield out union effectWithColors(trn.eff.asList, cond.asList)(f)

  given coloredTrnConversion[P, C]: Conversion[Trn[P], ColoredTrn[P, C]] with
    override def apply(x: Trn[P]): ColoredTrn[P, C] = ColoredTrn(x, Option.empty, _.head)

  enum ColorAttribute[C]:
    case Guard(guard: C => Boolean)
    case OnTransition(tr: Seq[C] => C)

  import ColorAttribute.*
  def when[C](guard: PartialFunction[C, Boolean]): Guard[C] = 
    Guard(c => guard.applyOrElse(c, _ => false))
  def onTransition[C](tr: PartialFunction[Seq[C], C]): OnTransition[C] = 
    OnTransition(t => tr.applyOrElse(t, _ => throw IllegalArgumentException("The transition color update function is incomplete")))

  extension [P, C](ctr: ColoredTrn[P, C])
    infix def >>(cc: ColorAttribute[C]): ColoredTrn[P, C] = cc match
      case Guard(g)         => ctr.copy(guard = Option(c => g(c) || ctr.guard.getOrElse(_ => false)(c) || g(c)))
      case OnTransition(tr) => ctr.copy(transform = tr)
