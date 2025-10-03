package mocks;

import app.model.LoginStatus;

public class LoginUnknown implements LoginStatus {
    @Override
    public int getStatus() {
        return 42;
    }
}
