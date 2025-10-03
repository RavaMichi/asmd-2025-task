# Lab 02 - GUI Tester

> Advanced Software Modeling and Design - 2024/25
>
> Author: *Ravaioli Michele*
> E-mail: *michele.ravaioli3@studio.unibo.it*

## Abstract

Nei moderni workflow di sviluppo il testing e' un elemento essenziale e copre diversi livelli (unit, integration e acceptance test) prevalentemente in forma automatizzata e integrata nella CI. L’interfaccia grafica, pero', rimane la parte meno testata, nonostante sia quella direttamente utilizzata dall’utente finale. La difficolta' sta nella complessita' di automatizzare verifiche su aspetti visivi e interattivi. Questo report esplora soluzioni, librerie e framework che rendono possibile l’integrazione di test automatici anche per le GUI.

> NOTA: Per questa analisi si considereranno solamente applicazioni Java, in particolare applicazioni SWING.

## Analisi

La GUI rappresenta l’elemento piu' complesso da testare in un progetto software. Mentre i singoli moduli e la loro integrazione possono essere verificati attraverso metodi e logiche ben definite, l’interfaccia grafica si distingue perché agisce da ponte tra utente e sistema. La logica interna e' testabile in modo indipendente, ma l’interfaccia, per sua natura, richiede l’interazione di un utente. Per automatizzare questa fase e' quindi necessario simulare tali interazioni.

Due sono gli approcci principali:

- **Object-level test**: suddivide l’interfaccia nei suoi componenti e ne testa il comportamento bypassando la parte grafica. Consente di simulare l’interazione con i singoli elementi, risultando efficace per GUI ben strutturate (es. in Swing), ma senza verificare gli aspetti visivi. Un esempio di libreria e' *AssertJ-Swing*.

- **Pixel-based test**: sfrutta tecniche di visione artificiale per analizzare direttamente la resa grafica dell’interfaccia. E' un metodo più complesso, ma simula fedelmente l’esperienza dell’utente e consente di testare anche i dettagli visivi. Una soluzione in questo ambito è *SikuliX*, basata su *OpenCV*.

## Confronto soluzioni

Per verificare l'efficacia di queste soluzioni, si e' definito un piccolo programma da far testare a entrambi: Il programma consiste in una semplice pagina di login, in cui all'utente e' chiesto di inserire username e password, dopodiche' schiacciando il pulsante di login all'utente verra' mostrato un messaggio di successo oppure di errore se le credenziali sono sbagliate. L'obiettivo e' quello di testare non la logica del login (sara' rimpiazzata da un mock) ma che l'interfaccia mostri correttamente all'utente il risultato dell'operazione, sia in caso di successo sia in caso di fallimento. Per testare si useranno entrambe le librerie citate prima: *AssertJ-Swing* e *SikuliX*.

