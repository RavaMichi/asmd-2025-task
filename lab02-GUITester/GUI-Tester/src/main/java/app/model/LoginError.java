package app.model;

public record LoginError(int status) implements LoginStatus {
    @Override
    public int getStatus() {
        return status;
    }
}
