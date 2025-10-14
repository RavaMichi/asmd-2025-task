# Lab 06 - Artist

> Advanced Software Modeling and Design - 2024/25
>
> Author: *Ravaioli Michele*
> E-mail: *michele.ravaioli3@studio.unibo.it*

## Abstract

Lo scopo di questo task e' implementare in *Scala* delle estensioni della rete di Petri. Le estensioni da aggiungere sono:

1. **Rete di Petri con priorita'**: alle transizioni della rete viene assegnato un valore di priorita' che consente di scegliere prima le transizioni con priorita' maggiore nel caso siano eseguibili più transizioni contemporaneamente.

2. **Rete di Petri colorata**: i token della rete possiedono un colore che, durante le transizioni, può essere manipolato, modificato e usato per inibire o abilitare alcune transizioni. I colori rendono la rete più espressiva.


## Rete di Petri con priorita'

Nella rete di Petri classica le transizioni attivabili hanno uguale probabilita'/possibilita' di essere eseguite e l'ordine di attivazione e' indifferente. L'insieme delle transizioni possibili e' quindi rappresentabile con un `Set[Trn]`, non ordinato. Per modellare priorita' e' necessario ordinare le transizioni in base al loro livello di priorita'; le transizioni con la stessa priorita' mantengono tra loro pari probailita' di essere scelte. Il modo più naturale per descrivere l'insieme delle transizioni attivabili e' quindi un `Seq[Set[Trn]]`.

Il tipo `PriorityPetriNet` e' rappresentato come una `Seq[PetriNet]`: ogni elemento della sequenza e' una sotto-rete che contiene le transizioni con la stessa priorita'. Per calcolare il nuovo stato si itera la sequenza ordinata dall'alto verso il basso, si verifica se esistono transizioni attivabili nel livello corrente; se si', se ne esegue una (tra quelle del medesimo livello), altrimenti si passa al livello successivo.

```scala
def toSystem: System[Marking[P]] = m => pnp match
  case Seq() => Set() // nothing
  case h +: tail =>
    val s = h.toSystem.next(m)
    if s.isEmpty then tail.toSystem.next(m) else s
```

Per facilitare la costruzione di reti con priorita' e' stato definito un DSL semplice che permette di associare un livello di priorita' alle transizioni.

```scala
val testPPN = PriorityPetriNet[TestPlace, Int](
  0 | MSet(P1) ~~> MSet(P2),
  1 | MSet(P2) ~~> MSet(P3), // this transition has higher priority
).toSystem
```

I test sono presenti nella classe [PPNSpec](asmd24-public-models/src/test/scala/u06/modelling/PPNSpec.scala).


## Rete di Petri colorata

Questa estensione e' più articolata rispetto a quella con priorita'. Nella rete classica i token sono indistinguibili; nella rete colorata ogni token possiede un colore che lo identifica e che può cambiare durante le transizioni.

### Design

La rete colorata deve permettere di associare a ogni token un colore che lo segua durante lo spostamento. Il colore può essere usato per due scopi principali: i *guard check* e gli *aggiornamenti*.

* **Guard check**: e' un controllo eseguito prima dell'attivazione di una transizione; la guardia può inibire la transizione in base ai colori dei token coinvolti. Le guardie sono definite sulle transizioni e sono opzionali: se assenti la transizione non effettua il controllo di guardia.

* **Aggiornamento**: e' la funzione che calcola il nuovo colore dei token prodotti quando una transizione viene eseguita. L'aggiornamento prende in input i colori dei token consumati e restituisce il colore del token generato; questa funzione e' definita sulla transizione e, nel nostro modello, deve essere sempre presente.

### Implementazione

Il tipo `ColoredPetriNet` modella una rete colorata ed e' definito come `Set[ColoredTrn]`. `ColoredTrn` contiene i dati della transizione, la guardia e la funzione di aggiornamento. La guardia e' modellata come `Option[C => Boolean]`, dove `C` e' il tipo dei colori; la funzione di aggiornamento e' modellata come `Seq[C] => C` (prende i colori dei token consumati e restituisce il nuovo colore).

Il comportamento della rete e' implementato seguendo la logica descritta: per ogni transizione attivabile si valuta la guardia (se presente) e, se superata, si applica la funzione di aggiornamento per ottenere il colore dei token prodotti. L'implementazione sfrutta costrutti idiomatici di Scala per mantenere il codice conciso ed efficiente.

```scala
def toSystem: System[Marking[(P, C)]] = m =>
  for
    ColoredTrn(trn, guard, f) <- cpn
    if isNotInhibited(trn) // check inhibition
    cond <- preconditionWithColors(m.asList, trn.cond.asList)
    if passGuard(cond, guard) // check color guard
    out <- m extract cond
  yield out union effectWithColors(trn.eff.asList, cond.asList)(f)
```

Anche per la rete colorata e' stato definito un DSL che consente di dichiarare la rete in modo chiaro e compatto.

```scala
val testCPN = ColoredPetriNet[Place, Color](
  MSet(P2) ~~> MSet(P3),
  MSet(P1) ~~> MSet(P2) >> when { case Red(_) => true }, // transition with guard check
  MSet(P1) ~~> MSet(P2) >> onTransition(_(3)),           // transition with update
  MSet(P1) ~~> MSet(P2) >> onTransition(_(3)) >> when { case Red(_) => true }, // both
).toSystem
```

Le funzionalita' sono testate nella classe [CPNSpec](asmd24-public-models/src/test/scala/u06/modelling/CPNSpec.scala).

