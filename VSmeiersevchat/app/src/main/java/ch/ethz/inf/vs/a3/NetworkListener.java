package ch.ethz.inf.vs.a3;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Severin on 25.10.2015.
 */
public interface NetworkListener {
    public void onReceiveMessage(String message);
}
