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

    public void sendMessage(Message message) {
        new NetworkWorker().execute(message);
    }

    public String receiveMessage(){
        return "";
    }

    class NetworkWorker extends AsyncTask<Message, Void, String> {
        private final int NUMBER_OF_RETRIES = 5;
        private String uuid;
        private DatagramSocket socket;
        @Override
        protected String doInBackground(Message... message) {
            try {
                uuid = message[0].getUUID().toString();
                // get the message
                JSONObject jsonMessage = message[0].getMessage();
                byte[] buf = jsonMessage.toString().getBytes();
                // create socket
                InetAddress address = InetAddress.getByName(NetworkConsts.SERVER_ADDRESS);
                socket = new DatagramSocket(NetworkConsts.UDP_PORT);
//                socket.setReuseAddress(true);
                socket.setSoTimeout(MainActivity.UDP_TIMEOUT);
                // create packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, NetworkConsts.UDP_PORT);
                Log.i("sending", new String(packet.getData(), 0, packet.getLength()));
                socket.send(packet);
                if(message[0].getType().equals(MessageTypes.RETRIEVE_CHAT_LOG)){
                    return getChatLog();
                }
                // receive response (try again if no response)
                buf = new byte[2024];
                DatagramPacket getAck = new DatagramPacket(buf, buf.length);
                int i;
                for (i = 0; i < NUMBER_OF_RETRIES; i ++){
                    try{
                        socket.receive(getAck);
                        break;
                    }catch (SocketTimeoutException t){
                        socket.send(packet);
                        Log.i("sending", new String(packet.getData(), 0, packet.getLength()));
                    }
                }
                if(i == NUMBER_OF_RETRIES){
                    return "-2";
                }
                socket.disconnect();
                socket.close();
                String received = new String(getAck.getData(), 0, getAck.getLength());
                Log.i("received", received);
                return received;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "-1";
        }

        protected String getChatLog(){
            try{
                byte[] buf = new byte[2024];
                DatagramPacket getMessage = new DatagramPacket(buf, buf.length);
                String received;
                while (true){
                    try{
                        socket.receive(getMessage);
                        received = new String(getMessage.getData(), 0, getMessage.getLength());
                        ChatActivity.addToBuffer(Message.fromString(received));
                    }catch (SocketTimeoutException ste){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return "-3";
        }

        @Override
        protected void onPostExecute(String returnMessage) {
            for(NetworkListener n: listeners){
                n.onReceiveMessage(returnMessage);
            }
        }
    }
}
