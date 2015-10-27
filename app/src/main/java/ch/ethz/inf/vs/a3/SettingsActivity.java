package ch.ethz.inf.vs.a3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "SettingsActivity";
    EditText serverAddrEdit;
    EditText serverPortEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        serverAddrEdit = (EditText) findViewById(R.id.server_addr_edit);
        serverAddrEdit.setText(NetworkConsts.SERVER_ADDRESS);
        serverPortEdit = (EditText) findViewById(R.id.server_port_edit);
        serverPortEdit.setText(""+NetworkConsts.UDP_PORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void onClickSave(View v){
        NetworkConsts.SERVER_ADDRESS = serverAddrEdit.getText().toString();
        Log.i(TAG, "set server address to: " + NetworkConsts.SERVER_ADDRESS);
        try {
            NetworkConsts.UDP_PORT = Integer.parseInt(serverPortEdit.getText().toString());
            this.finish();
            Log.i(TAG, "set server port to: " + NetworkConsts.UDP_PORT);
        }catch (NumberFormatException e){
            // alert user when port has invalid format
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Port must be an integer!");
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }
}
