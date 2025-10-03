package assertj;

import app.model.LoginLogic;
import app.presenter.LoginPresenter;
import app.view.LoginPage;
import mocks.LoginLogicStub;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        LoginLogic model = new LoginLogicStub();
        LoginPage view = new LoginPage(600, 480);
        LoginPresenter presenter = new LoginPresenter(model, view);
        view.setPresenter(presenter);
        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }
}
