package im.youtiao.android_client.wrap;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import im.youtiao.android_client.dao.Group;

public class GroupWrap {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static im.youtiao.android_client.model.Group wrap(Group arg) {
        im.youtiao.android_client.model.Group ret = new im.youtiao.android_client.model.Group();
        try {
            ret = objectMapper.readValue(arg.getJson(), im.youtiao.android_client.model.Group.class);
        } catch (IOException e) {
            ret = null;
        }
        return ret;
    }

    public static Group validate(im.youtiao.android_client.model.Group arg) {
        Group ret = new Group();
        ret.setServerId(arg.id);
        ret.setRole(arg.membership.role);
        try {
            ret.setJson(objectMapper.writeValueAsString(arg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
