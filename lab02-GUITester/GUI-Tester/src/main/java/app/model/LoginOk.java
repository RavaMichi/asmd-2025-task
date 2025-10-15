package app.model;

public record LoginOk(int status, String token) implements LoginStatus {

    public String getToken() {
        return token;
    }
    @Override
    public int getStatus() {
        return status;
    }
}
