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

Il progetto e' basato sul pattern MVP, composto da tre componenti principali: LoginLogic (model), LoginPresenter (presenter) e LoginPage (view). LoginPage riceve i segnali dall'utente, e manda delle query a LoginPresenter, il quale a sua volta chiede l'elaborazione alla LoginLogic. In questo caso la view si occupa della parte puramente grafica, mentre il presenter si occupa di elaborare la risposta della logica e del messaggio da mostrare all'utente. Con il sistema di test si dovra' andare a verificare che View e Presenter diano il risultato a schermo sperato. Per quanto riguarda la logica, questa verr'a rimpiazzata da uno stub LoginLogicStub, che permette il login solo con username e password hardcoded.

### AssertJ

AssertJ permette di scrivere test puliti e organizzati, si integra inoltre molto facilmente dentro JUnit. Per testare Swing, prima si crea una FrameFixture, che abilita il robot che si occupera' di simulare l'interazione utente, poi si decide quali oggetti andare a testare cercandoli nel JFrame tramite il loro nome (questo significa che per poter usare AssertJ e' necessario avere i componenti Swing con i nomi settati).

Si sono fatti tre test per verificare il corretto funzionamento di un login giusto, un login con credenziali sbagliate, e un login con errore sul server.

```java
    @Before
    public void setUp() {
        // create the GUI on the EDT and return the instance
        page = GuiActionRunner.execute(() -> new LoginPage(400, 200));
        LoginLogic stub = new LoginLogicStub();
        LoginPresenter presenter = new LoginPresenter(stub, page);
        GuiActionRunner.execute(() -> page.setPresenter(presenter));

        // creates the Robot that drives the GUI
        window = new FrameFixture(page);
        window.show();
    }

    // ...

    @Test
    public void testSuccessfulLogin() {
        // simulate user typing and clicking
        window.textBox("username").enterText("admin");
        window.textBox("password").enterText("admin");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("You are successfully logged in! (Code: 200)");
    }

    @Test
    public void testUnsuccessfulLogin() {
        // simulate user typing and clicking
        window.textBox("username").enterText("wrong_user");
        window.textBox("password").enterText("wrong_pwd");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("Incorrect username or password. (Code: 400)");
    }

    @Test
    public void testLoginError() {
        // simulate user typing and clicking
        window.textBox("username").enterText("crash");
        window.textBox("password").enterText("any");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("An error occurred in login. (Code: 500)");
    }
```

### SikuliX

