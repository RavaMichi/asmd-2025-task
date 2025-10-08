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
      val mm = m.asList.toMap
      def isInhibited(t: Trn[P]): Boolean = mm.keys.exists(t.inh.asList.contains)
      def guardCheckFailed(guard: C => Boolean): Boolean = !mm.values.exists(guard)
      cpn
        .map { case ColoredTrn(transition, guard, transform) => (transition, guard, transform) }
        .map((tr, g, f) =>
          val cond = mm.filter((p, c) => tr.cond.asList.contains(p))
          if isInhibited(tr) || guardCheckFailed(g) || cond.isEmpty then
            List.empty
          else
            val eff = tr.eff.asList.map(p => (p, f(cond.values.toSeq)))
            mm.removedAll(cond.keys).toList ++ eff
        ).filterNot(_.isEmpty)
        .map(MSet.ofList)