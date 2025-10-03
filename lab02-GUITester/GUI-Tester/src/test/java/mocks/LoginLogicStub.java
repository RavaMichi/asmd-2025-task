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
            // good
            return new LoginOk(200, "admin-token");
        } else if (username.equals("crash")) {
            // crash: unknown reply
            return new LoginUnknown();
        } else {
            // bad credentials
            return new LoginError(400);
        }
    }
}
