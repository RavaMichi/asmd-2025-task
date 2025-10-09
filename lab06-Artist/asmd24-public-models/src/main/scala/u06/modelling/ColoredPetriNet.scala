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

  def preconditionWithColors[P, C](state: List[(P, C)], cond: List[P]): Set[MSet[(P, C)]] =
    state
      .filter(x => cond.contains(x._1))
      .flatMap(x => cond diff Seq(x._1) match
        case List() => Set(MSet(x))
        case m => preconditionWithColors(state diff Seq(x), m).map(MSet(x).union(_))
      ).toSet
  def effectWithColors[P, C](eff: List[P], cond: List[(P, C)])(function: Seq[C] => C): MSet[(P, C)] =
    MSet.ofList(eff.map(e => (e, function(cond.map(_._2)))))
  
  extension [P, C](cpn: ColoredPetriNet[P, C])
    def toSystem: System[Marking[(P, C)]] = m =>
      def isNotInhibited(t: Trn[P]) = !m.asList.map(_._1).exists(t.inh.asList.contains)
      def passGuard(s: MSet[(P, C)], guard: C => Boolean) = s.asList.map(_._2).forall(guard)
      for 
        ColoredTrn(trn, guard, f) <- cpn
        if isNotInhibited(trn) // check inhibition
        cond <- preconditionWithColors(m.asList, trn.cond.asList)
        if passGuard(cond, guard) // check color guard
        out <- m extract cond
      yield out union effectWithColors(trn.eff.asList, cond.asList)(f)

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
