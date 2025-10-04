package view;

import app.model.LoginLogic;
import app.presenter.LoginPresenter;
import app.view.LoginPage;
import mocks.LoginLogicStub;
import org.junit.*;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * SikuliX tests for LoginPage.
 * Requirements:
 *  - images in src/test/resources/images/
 *    username_field.png, password_field.png, login_button.png,
 *    success_msg.png, fail_msg.png, error_msg.png
 */
public class LoginPageSikuliTest {

    private static Screen screen;
    private LoginPage page;
    private Region appRegion;

    private final static String USER_IMG = "images/username_field.png";
    private final static String PASS_IMG = "images/password_field.png";
    private final static String LOGIN_IMG = "images/login_button.png";
    private final static String SUCCESS_IMG = "images/success_msg.png";
    private final static String FAIL_IMG = "images/fail_msg.png";
    private final static String ERROR_IMG = "images/error_msg.png";

    @BeforeClass
    public static void beforeClass() {
        // default for "wait" and "exists" operations (seconds)
        Settings.AutoWaitTimeout = 3.0f;
        // delay for key-press
        Settings.TypeDelay = 0.03;
        screen = new Screen();
    }

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

    @After
    public void tearDown() throws Exception {
        // dispose the window (on EDT)
        SwingUtilities.invokeAndWait(() -> {
            if (page != null) {
                page.dispose();
            }
        });
    }

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
}