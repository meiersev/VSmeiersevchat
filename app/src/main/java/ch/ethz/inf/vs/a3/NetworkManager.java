package ch.ethz.inf.vs.a3;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.a3.message.Message;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by Severin on 25.10.2015.
 */
public class NetworkManager {
    protected List<NetworkListener> listeners = new ArrayList<>();

    public NetworkManager() {    }

    public void registerListener(NetworkListener n) {
        listeners.add(n);
    }

    public void unregisterListener(NetworkListener n) {
        listeners.remove(n);
    }

    // start AsyncTask for the network interactions
    public void sendMessage(Message message) {
        new NetworkWorker().execute(message);
    }

    class NetworkWorker extends AsyncTask<Message, Void, String> {
        private final int NUMBER_OF_RETRIES = 5;
        private DatagramSocket socket;
        @Override
        protected String doInBackground(Message... message) {
            try {
                // get the message
                JSONObject jsonMessage = message[0].getMessage();
                byte[] buf = jsonMessage.toString().getBytes();
                String type = message[0].getType();
                // create socket
                InetAddress address = InetAddress.getByName(NetworkConsts.SERVER_ADDRESS);
                socket = new DatagramSocket(NetworkConsts.UDP_PORT);
                socket.setSoTimeout(MainActivity.UDP_TIMEOUT);
                // create packet and send it
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, NetworkConsts.UDP_PORT);
                Log.i("sending", new String(packet.getData(), 0, packet.getLength()));
                socket.send(packet);
                // differentiate between message types
                if (type.equals(MessageTypes.RETRIEVE_CHAT_LOG)) {
                    // if it was a message to retrieve the chat log, get the answers and return a code
                    return getChatLog();
                } else if (type.equals(MessageTypes.DEREGISTER)) {
                    // no one waiting for a response after deregister
                    return null;
                } else {
                    // register message -> receive response (try again if no response)
                    buf = new byte[2024];
                    DatagramPacket getAck = new DatagramPacket(buf, buf.length);
                    int i;
                    for (i = 0; i < NUMBER_OF_RETRIES; i++) {
                        try {
                            socket.receive(getAck);
                            break;
                        } catch (SocketTimeoutException t) {
                            socket.send(packet);
                            Log.i("sending", new String(packet.getData(), 0, packet.getLength()));
                        }
                    }
                    if (i == NUMBER_OF_RETRIES) {
                        return "-2";
                }
                socket.disconnect();
                socket.close();
                String received = new String(getAck.getData(), 0, getAck.getLength());
                Log.i("received", received);
                return received;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "-1";
        }

        // wait for messages from the server, until timeout
        protected String getChatLog(){
            try{
                byte[] buf = new byte[2024];
                DatagramPacket getMessage = new DatagramPacket(buf, buf.length);
                String received;
                while (true){
                    try{
                        socket.receive(getMessage);
                        received = new String(getMessage.getData(), 0, getMessage.getLength());
                        // put the messages in the PriorityQueue of the ChatActivity
                        ChatActivity.addToBuffer(Message.fromString(received));
                    }catch (SocketTimeoutException ste){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            socket.disconnect();
            socket.close();
            // let the receiver know that all messages have been added to the queue
            return "-3";
        }

        // give the returned message to all the listeners
        @Override
        protected void onPostExecute(String returnMessage) {
            for(NetworkListener n: listeners){
                n.onReceiveMessage(returnMessage);
            }
        }
    }
}
