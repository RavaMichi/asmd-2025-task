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

Di seguito sono mostrati i test fatti sulla LoginPage ([LoginPageAssertJTest](./GUI-Tester/src/test/java/view/LoginPageAssertJTest.java)):
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

Con SikuliX si possono scrivere test che non richiedono la conoscienza della struttura sottostante, infatti il suo metodo di test si basa puramente sui pixel nello schermo e testa attraverso il pattern matching.

I test son SikuliX risultano semplici da capire e da scrivere, tuttavia richiedono delle immagini di riferimento per poter eseguire il pattern mathcing: per ogni componente con il quale si interagisce, bisogna fornire la sua immagine, per cui il setup del test e' piu' complesso. Una volta ottenute queste immagine lo sviluppo dei test risulta molto semplificato e simula fedelmente l'interazione dell'utente.

Di seguito sono mostrati i test fatti sulla LoginPage ([LoginPageSikuliTest](./GUI-Tester/src/test/java/view/LoginPageSikuliTest.java)): 

```java
 @Before
    public void setUp() throws Exception {
        // Launch the LoginPage on EDT and make it visible at a predictable location (0,0)
        SwingUtilities.invokeAndWait(() -> {
            LoginLogic stub = new LoginLogicStub();
            page = new LoginPage(400, 200);
            LoginPresenter presenter = new LoginPresenter(stub, page);
            page.setPresenter(presenter);

            page.setLocation(0, 0);
            page.setVisible(true);
        });

        // compute the region covering the window so Sikuli searches only inside it
        final Point[] loc = new Point[1];
        final Dimension[] size = new Dimension[1];
        SwingUtilities.invokeAndWait(() -> {
            // getLocationOnScreen requires the component to be visible
            loc[0] = page.getLocationOnScreen();
            size[0] = page.getSize();
        });

        appRegion = new Region(loc[0].x, loc[0].y, size[0].width, size[0].height);
    }

    // used to test any message response from the page
    private void testMessagePattern(String username, String password, URL messageImageURL) {
        try {
            // Patterns: use a high similarity threshold for buttons/fields that are crisp
            Pattern userPattern = new Pattern(ClassLoader.getSystemResource(USER_IMG)).similar(0.92);
            Pattern passPattern = new Pattern(ClassLoader.getSystemResource(PASS_IMG)).similar(0.92);
            Pattern loginPattern = new Pattern(ClassLoader.getSystemResource(LOGIN_IMG)).similar(0.92);
            Pattern messagePattern = new Pattern(messageImageURL).similar(0.95);

            appRegion.click(userPattern);
            screen.type(username); // add username

            appRegion.click(passPattern);
            screen.type(password); // add password

            appRegion.click(loginPattern); // login

            // wait for success message
            appRegion.wait(messagePattern, 5); // throws FindFailed if not found

            assertNotNull("output message should be visible", appRegion.exists(messagePattern));
        } catch (FindFailed ff) {
            fail("could not find message: " + ff.getMessage());
        }
    }

    @Test
    public void testSuccessfulLogin() {
        testMessagePattern(
                "admin",
                "admin",
                ClassLoader.getSystemResource(SUCCESS_IMG)
        );
    }

    @Test
    public void testUnsuccessfulLogin() {
        testMessagePattern(
                "wrong",
                "wrong",
                ClassLoader.getSystemResource(FAIL_IMG)
        );
    }

    @Test
    public void testLoginError() {
        testMessagePattern(
                "crash",
                "any_pwd",
                ClassLoader.getSystemResource(ERROR_IMG)
        );
    }
```

### CI workflow

Con queste librerie e' possibile impostare dei test automatici per la continuos integration. Il setup risulta pero' molto complesso, in quanto richiede di impostare display virtuali in un ambiete privo di UI per eseguire il test.

Il file [ci.yaml](./GUI-Tester/.github/workflows/ci.yaml) contiene un job per eseguire i test delle due classi di test precedentemente mostrate.

## Conclusioni

Il confronto mette in luce un trade-off chiaro: *AssertJ-Swing* offre test puliti, veloci e facilmente integrabili in JUnit/CI, ma non verifica l’aspetto visivo; *SikuliX* riproduce fedelmente l’interazione utente e cattura dettagli grafici, ma richiede immagini di riferimento, ambiente di esecuzione più complesso e risulta più fragile a cambi di risoluzione, tema, font etc.

Tenendo a mente queste considerazione, il workflow consigliato e':

- Usare AssertJ-Swing come base: e' ottimo per testare i componenti della UI, e' affidabile e si integra facilmente con la CI. Richiede solamente di progettare la GUI per la testabilita' (ha bisogno del name sui vari componenti da testare).

- Integrare SikuliX in modo selettivo solo per test end-to-end visivi critici oppure dove non e' possibile analizzare i componenti della UI (per esempio: un canvas di un simulatore).

- Impostare la CI con display virtuali.