package ch.ethz.inf.vs.a3.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import ch.ethz.inf.vs.a3.MainActivity;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by Severin on 20.10.2015.
 */
public class Message {
    private final String type;
    private final String username;
    private final UUID uuid;
    private JSONObject jsonObject;

    public Message(String user, UUID uuid, String type){
        this.username = user;
        this.uuid = uuid;
        this.type = type;
        try {
            jsonObject = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("username", username);
            header.put("uuid", uuid.toString());
            header.put("timestamp", "{}");
            header.put("type",type);
            jsonObject.put("header", header);
            jsonObject.put("body", null);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public JSONObject getMessage(){
        return jsonObject;
    }

    public UUID getUUID(){
        return uuid;
    }
}
