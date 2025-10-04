package assertj;

import app.model.LoginLogic;
import mocks.LoginLogicStub;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.*;

import app.presenter.LoginPresenter;
import app.view.LoginPage;

/**
 * AssertJ tests for LoginPage
 */
public class LoginPageAssertJTest {
    private FrameFixture window;
    private LoginPage page;

    @BeforeClass
    public static void installRepaintManager() {
        FailOnThreadViolationRepaintManager.install();
    }

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

    @After
    public void tearDown() {
        // important: release resources (keyboard/mouse lock etc.)
        if (window != null) {
            window.cleanUp();
        }
    }

    /**
     * Simulate a successful login
     */
    @Test
    public void testSuccessfulLogin() {
        // simulate user typing and clicking
        window.textBox("username").enterText("admin");
        window.textBox("password").enterText("admin");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("You are successfully logged in! (Code: 200)");
    }

    /**
     * Simulate an unsuccessful login
     */
    @Test
    public void testUnsuccessfulLogin() {
        // simulate user typing and clicking
        window.textBox("username").enterText("wrong_user");
        window.textBox("password").enterText("wrong_pwd");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("Incorrect username or password. (Code: 400)");
    }
    /**
     * Simulate an error during login
     */
    @Test
    public void testLoginError() {
        // simulate user typing and clicking
        window.textBox("username").enterText("crash");
        window.textBox("password").enterText("any");
        window.button("login").click();

        // assert the message label text
        window.label("message").requireText("An error occurred in login. (Code: 500)");
    }
}
