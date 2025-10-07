package u06.modelling

import u06.utils.MSet

object ColoredPetriNet:
  import PetriNet.*
  case class ColoredTrn[P, C](transition: Trn[P], guard: C => Boolean, transform: Iterable[C] => C)
  type ColoredPetriNet[P, C] = Set[ColoredTrn[P, C]]

  given coloredTrnConversion[P, C]: Conversion[Trn[P], ColoredTrn[P, C]] with
    override def apply(x: Trn[P]): ColoredTrn[P, C] = ColoredTrn(x, c => true, _.head)

  def apply[P, C](transitions: ColoredTrn[P, C]*): ColoredPetriNet[P, C] = transitions.toSet

  extension [P, C](cpn: ColoredPetriNet[P, C])
    def toSystem: System[Marking[(P, C)]] = m =>
      val mm = m.asList.toMap
      cpn
        .map { case ColoredTrn(transition, guard, transform) => (transition, guard, transform) }
        .map:
          (tr, g, f) =>
            if mm.keys.exists(tr.inh.asList.contains) then // inhibition
              MSet()
            else if mm.values.exists(g) then // color guard
              MSet()
            else
              lazy val cond = mm.filter((p, c) => tr.cond.asList.contains(p))
              lazy val out = tr.eff.asList.map(p => (p, f(cond.values)))
              MSet.ofList(mm.removedAll(cond.keys).toList ++ out)