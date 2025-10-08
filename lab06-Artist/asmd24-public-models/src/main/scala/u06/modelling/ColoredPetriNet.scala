package u06.modelling

import u06.utils.MSet

object ColoredPetriNet:
  import PetriNet.*
  case class ColoredTrn[P, C](transition: Trn[P], guard: C => Boolean, transform: Seq[C] => C)
  type ColoredPetriNet[P, C] = Set[ColoredTrn[P, C]]

  given coloredTrnConversion[P, C]: Conversion[Trn[P], ColoredTrn[P, C]] with
    override def apply(x: Trn[P]): ColoredTrn[P, C] = ColoredTrn(x, c => true, _.head)

  def apply[P, C](transitions: ColoredTrn[P, C]*): ColoredPetriNet[P, C] = transitions.toSet

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

  extension [P, C](ctr: ColoredTrn[P, C])
    infix def when(guard: C => Boolean): ColoredTrn[P, C] =
      ColoredTrn(
        ctr.transition,
        c => ctr.guard(c) && guard(c),
        ctr.transform
      )