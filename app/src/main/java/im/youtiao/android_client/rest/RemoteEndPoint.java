package im.youtiao.android_client.rest;


import retrofit.Endpoint;

public class RemoteEndPoint implements Endpoint {

    private static final String DEFAULT_ENDPOINT =
            String.format("http://%s:3000", "192.168.241.19");

    private String mEndPoint;

    public void setRemoteEndPoint(String host) {
        mEndPoint = host;
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
