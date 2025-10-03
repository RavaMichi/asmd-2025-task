package app.view;

import app.presenter.LoginPresenter;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {

    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JLabel messageField;

    private LoginPresenter presenter;
    public LoginPage(int width, int height) {
        super("Login Page");
        this.setSize(width, height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // layout manager for proper arrangement
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // padding

        int row = 0;

        messageField = new JLabel();
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(messageField, gbc);

        row++;
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField(20);
        usernameField.setName("username");
        panel.add(usernameField, gbc);

        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(20);
        passwordField.setName("password");
        panel.add(passwordField, gbc);

        row++;

        // Login button
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setName("login");
        loginButton.addActionListener(e -> login());
        panel.add(loginButton, gbc);

        this.add(panel);
        this.setLocationRelativeTo(null); // center on screen
    }
    public String getUsername() {
        return usernameField.getText();
    }
    public String getPassword() {
        return passwordField.getText();
    }
    public void login() {
        // error if there is no presenter
        this.presenter.login(getUsername(), getPassword());
    }
    public void setMessage(String message) {
        this.messageField.setText(message);
    }
    public void setPresenter(LoginPresenter presenter) {
        this.presenter = presenter;
    }
}
