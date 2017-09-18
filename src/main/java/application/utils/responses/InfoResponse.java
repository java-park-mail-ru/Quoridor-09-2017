package application.utils.responses;

public class InfoResponse {
    private String info;

    public InfoResponse(String info) {
        this.info = info;
    }

    @SuppressWarnings("unused")
    public String getInfo() {
        return info;
    }
}
