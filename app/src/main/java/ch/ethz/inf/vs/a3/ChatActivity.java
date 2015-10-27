package ch.ethz.inf.vs.a3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.PriorityQueue;
import java.util.UUID;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.message.MessageTypes;

public class ChatActivity extends AppCompatActivity implements NetworkListener{
    private NetworkManager networkManager;
    private String username;
    private UUID uuid;
    private PriorityQueue<Message> messageBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        networkManager = new NetworkManager();
        networkManager.registerListener(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        uuid = UUID.fromString(intent.getStringExtra("uuid"));
        messageBuffer = new PriorityQueue<>(11, new MessageComparator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
        Message message = new Message(username, uuid, MessageTypes.DEREGISTER, new VectorClock(), null);
        networkManager.sendMessage(message);
        networkManager.unregisterListener(this);
        super.onDestroy();
    }

    public void onClickRetrieveLog(View v){
        Message message = new Message(username, uuid, MessageTypes.RETRIEVE_CHAT_LOG, new VectorClock(), null);
        networkManager.sendMessage(message);
    }

    public void onReceiveMessage(String message){
        // todo: part 3
    }
}
