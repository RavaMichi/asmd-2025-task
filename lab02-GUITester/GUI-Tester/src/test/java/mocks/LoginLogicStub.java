package mocks;

import app.model.LoginError;
import app.model.LoginLogic;
import app.model.LoginOk;
import app.model.LoginStatus;

/**
 * Simple stub  for login logic. Accepts only 'admin' as username and password.
 */
public class LoginLogicStub implements LoginLogic {
    @Override
    public LoginStatus login(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            return new LoginOk(200, "admin-token");
        } else {
            return new LoginError(400);
        }
    }
}
