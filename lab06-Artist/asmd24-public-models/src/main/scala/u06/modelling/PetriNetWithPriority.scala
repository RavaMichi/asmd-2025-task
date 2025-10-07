package u06.modelling

import PetriNet.*

import scala.annotation.tailrec
object PetriNetWithPriority:

  type PetriNetWithPriority[P] = Seq[PetriNet[P]]
  case class PriorityTrn[P, I](priority: I, transition: Trn[P])

  def apply[P, I](transitions: PriorityTrn[P, I]*)(implicit ordering: Ordering[I]): PetriNetWithPriority[P] =
    transitions
      .groupBy[I](_.priority)
      .toSeq
      .sortWith((a, b) => ordering.gt(a._1, b._1))
      .map((i, trn) => trn.map(_.transition).toSet)

  extension [P](pnp: PetriNetWithPriority[P])
    def toSystem: System[Marking[P]] = m => pnp match
      case Seq() => Set() // nothing
      case h +: tail =>
        val s = h.toSystem.next(m)
        if s.isEmpty then tail.toSystem.next(m) else s


  extension [P, I](i: I)
    def |(trn: Trn[P]): PriorityTrn[P, I] = PriorityTrn(i, trn)
