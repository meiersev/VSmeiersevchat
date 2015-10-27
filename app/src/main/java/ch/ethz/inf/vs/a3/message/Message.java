package ch.ethz.inf.vs.a3.message;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import ch.ethz.inf.vs.a3.MainActivity;
import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by Severin on 20.10.2015.
 */
public class Message {
    private final String type;
    private final String username;
    private final UUID uuid;
    private VectorClock timestamp;
    private JSONObject jsonObject;
    private String content;

    public Message(String user, UUID uuid, String type, VectorClock timestamp, String content){
        this.username = user;
        this.uuid = uuid;
        this.type = type;
        this.timestamp = timestamp;
        this.content = content;
        try {
            jsonObject = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("username", username);
            header.put("uuid", uuid.toString());
            header.put("timestamp", timestamp.toString());
            header.put("type", type);
            jsonObject.put("header", header);
            JSONObject body = null;
            if(type == MessageTypes.CHAT_MESSAGE){
                body = new JSONObject();
                body.put("content", content);
            }
            jsonObject.put("body", body);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static Message fromString(String s){
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONObject header = new JSONObject(jsonObject.getString("header"));
            JSONObject body = new JSONObject(jsonObject.getString("body"));
            VectorClock clock = new VectorClock();
            clock.setClockFromString(header.getString("timestamp"));
            return new Message(header.getString("username"), UUID.fromString(header.getString("uuid")),
                    header.getString("type"), clock, body.getString("content"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getType(){
        return type;
    }

    public JSONObject getMessage(){
        return jsonObject;
    }

    public VectorClock getTimestamp(){
        return timestamp;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getContent(){
        return content;
    }
}
