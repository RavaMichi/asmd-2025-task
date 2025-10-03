package app.view;

import app.presenter.LoginPresenter;

import javax.swing.*;

public class LoginPage extends JFrame {

    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JLabel messageField;

    private final LoginPresenter presenter;
    public LoginPage(int width, int height, LoginPresenter presenter) {
        super("Login Page");
        this.setSize(width, height);
        this.presenter = presenter;

        messageField = new JLabel();
        this.add(messageField);

        this.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        usernameField.setName("username");
        this.add(usernameField);

        this.add(new JLabel("Password:"));
        passwordField = new JTextField(20);
        passwordField.setName("password");
        this.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setName("login");
        loginButton.addActionListener(e -> login());
        this.add(loginButton);

    }
    public String getUsername() {
        return usernameField.getText();
    }
    public String getPassword() {
        return passwordField.getText();
    }
    public void login() {
        this.presenter.login(getUsername(), getPassword());
    }
    public void setMessage(String message) {
        this.messageField.setText(message);
    }
}
