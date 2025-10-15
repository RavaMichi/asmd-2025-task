package app.presenter;

import app.model.*;
import app.view.LoginPage;

public class LoginPresenter {
    private final LoginLogic model;
    private final LoginPage view;

    public LoginPresenter(LoginLogic model, LoginPage view) {
        this.model = model;
        this.view = view;
    }
    public void login(String username, String password) {
        switch (model.login(username, password)) {
            case LoginOk ok -> setViewMessage(ok.getStatus(), "You are successfully logged in!");
            case LoginError err -> setViewMessage(err.getStatus(), "Incorrect username or password.");
            default -> setViewMessage(500, "An error occurred in login.");
        }
    }
    private void setViewMessage(int status, String msg) {
        view.setMessage(msg + " (Code: " + status + ")");
    }
}
