package app.model;

public abstract class LoginStatus {
    private final int status;

    protected LoginStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

