package im.youtiao.android_client.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerError {
    public int status;
    public String errorMessage;
}
