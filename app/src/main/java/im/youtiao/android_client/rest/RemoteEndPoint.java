package im.youtiao.android_client.rest;


import retrofit.Endpoint;

public class RemoteEndPoint implements Endpoint {

    private static final String DEFAULT_ENDPOINT =
            String.format("http://%s:3000", "192.168.241.129");

    private String mEndPoint;

    public void setConnectionSettings(String address, int port) {
        mEndPoint = String.format("http://%s:%d", address, port);
    }

    @Override
    public String getUrl() {
        return mEndPoint != null ? mEndPoint : DEFAULT_ENDPOINT;
    }

    @Override
    public String getName() {
        return "api.youtiao.im";
    }
}
