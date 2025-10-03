package app.model;

public class LoginOk extends LoginStatus {

    private final String token;
    protected LoginOk(String token) {
        super(200);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
