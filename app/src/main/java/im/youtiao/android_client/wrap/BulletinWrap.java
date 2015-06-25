package im.youtiao.android_client.wrap;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import im.youtiao.android_client.dao.Bulletin;
import im.youtiao.android_client.dao.Group;

public class BulletinWrap {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static im.youtiao.android_client.model.Bulletin wrap(Bulletin arg) {
        im.youtiao.android_client.model.Bulletin ret = new im.youtiao.android_client.model.Bulletin();
        try {
            ret = objectMapper.readValue(arg.getJson(), im.youtiao.android_client.model.Bulletin.class);
        } catch (IOException e) {
            ret = null;
        }
        return ret;
    }

    public static Bulletin validate(im.youtiao.android_client.model.Bulletin arg) {
        Bulletin ret = new Bulletin();
        ret.setServerId(arg.id);
        try {
            ret.setJson(objectMapper.writeValueAsString(arg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
