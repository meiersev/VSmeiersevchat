package ch.ethz.inf.vs.a3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;

import ch.ethz.inf.vs.a3.message.ErrorCodes;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

public class MainActivity extends AppCompatActivity implements NetworkListener {
    private String username;
    private UUID uuid;
    public final static int UDP_TIMEOUT = 2000;
    public final static int numberOfRetries = 5;
    private NetworkManager networkManager;
    private EditText editUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editUsername = (EditText) findViewById(R.id.enter_name);
        networkManager = new NetworkManager();
        networkManager.registerListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        networkManager.unregisterListener(this);
        super.onDestroy();
    }

    // Todo: send 'register' message to server
    public void onClickJoin(View v){
        username = editUsername.getText().toString();
        uuid = UUID.randomUUID();
        Message message = new Message(username, uuid, MessageTypes.REGISTER);
        networkManager.sendMessage(message);
    }

    public void onClickSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onReceiveMessage(String message){
        if(message.equals("-2")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("ERROR");
            alertDialogBuilder.setMessage("Could not connect to Server");
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
            return;
        }else if(message.equals("-1")){
            Log.w("Error", "something went wrong with server response");
            return;
        }
        try {
            JSONObject response = new JSONObject(message);
            JSONObject responseHeader = new JSONObject(response.getString("header"));
            if(responseHeader.getString("type").equals(MessageTypes.ACK_MESSAGE)){
                // got ack message
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("uuid", uuid.toString());
                startActivity(intent);
            }else if(responseHeader.getString("type").equals(MessageTypes.ERROR_MESSAGE)) {
                // todo implement this
                Log.w("got error", "need to implement this");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("ERROR");
                // figure out what went wrong
                JSONObject responseBody = new JSONObject(response.getString("body"));
                int error = responseBody.getInt("content");
                if(error == ErrorCodes.REG_FAIL){
                    alertDialogBuilder.setMessage("User registration failed");
                }else{
                    alertDialogBuilder.setMessage("Server responded with error code: " + error);
                }
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}


