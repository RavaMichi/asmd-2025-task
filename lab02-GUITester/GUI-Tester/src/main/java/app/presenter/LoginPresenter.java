package app.presenter;

import app.model.LoginLogic;
import app.view.LoginPage;

public class LoginPresenter {
    private final LoginLogic model;
    private final LoginPage view;

    public LoginPresenter(LoginLogic model, LoginPage view) {
        this.model = model;
        this.view = view;
    }


}
