package org.jarvus.cordova.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Inet4Address;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.util.SparseArray;

import cn.com.geovis.DataEncoder;
import cn.com.geovis.IDataEncoder;

public class Multicast extends CordovaPlugin {
    private static final String TAG = Multicast.class.getSimpleName();

    SparseArray<DatagramSocket> m_sockets;
    SparseArray<SocketListener> m_listeners;

    LocationListener m_locListener;

    private IDataEncoder dataEncoder;

    public Multicast() {
        m_sockets = new SparseArray<DatagramSocket>();
        m_listeners = new SparseArray<SocketListener>();
        dataEncoder = new DataEncoder();
    }


    private class LocationListener extends Thread{

        private double[] locations = new double[2];

        public void run(){
            

            try {
                 // 启动udpserder来监听位置信息
                DatagramSocket socket = new DatagramSocket(8080);
                byte[] data = new byte[10240];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                Log.d(TAG, "Receive location thread Starting loop!");
                while(true){
                    try{
                        Log.d(TAG, "Waiting for location packet!");
                        socket.receive(packet);
                        String rawData = new String(data, 0, packet.getLength());
                        Log.d(TAG, "rawData is " + rawData);
                        if(rawData.contains("$BDRMC")){
                            Log.d(TAG, "have $BDRMC");
                            setLocation(rawData);
                        }else{
                            Log.d(TAG, "no have $BDRMC");
                        }
                    }catch(Exception e){
                        Log.d(TAG, "Receive exception:" + e.toString());
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Receive exception:" + e.toString());
                return;
            }


           
        }

        synchronized public void setLocation(String info) {

            Log.d(TAG, "execute setLocation");

            String lonStr = degreeModify(info.split(",")[5]);
            String latStr = degreeModify(info.split(",")[3]);


            Log.d(TAG, "lonStr is " + lonStr + "  latStr is " + latStr);

            double lon = Double.parseDouble(lonStr);
            double lat = Double.parseDouble(latStr);

            Log.d(TAG, "lon is " + lon + "  lat is " + lat);


            locations[0] = lon;
            locations[1] = lat;

            Log.d(TAG, "locations[0] is " + locations[0] + "   locations[1] is " +  locations[1]);

         
          }

        synchronized public double[] getLocation(){
            return locations;
        }

        public String degreeModify(String rawData) {

            Log.d(TAG, "degreeModify rawData is " + rawData);

            String head = rawData.split("\\.")[0];
            String end = rawData.split("\\.")[1];
            double res = Double.parseDouble(head);
            res = res / 100;
            String r = String.valueOf(res);

            Log.d(TAG, "degreeModify result is " + r + end);

            return r + end;
          }

    }

    private class SocketListener extends Thread {
        int m_socketId;
        DatagramSocket m_socket;

        public SocketListener(int id, DatagramSocket socket) {
            this.m_socketId = id;
            this.m_socket = socket;
        }

        public void run() {
            byte[] data = new byte[20480]; // investigate MSG_PEEK and MSG_TRUNC in java
            DatagramPacket packet = new DatagramPacket(data, data.length);
            Log.d(TAG, "Starting loop!");
            while (true) {
                try {
                    Log.d(TAG, "Waitin for packet!");
                    this.m_socket.receive(packet);
                    // String msg = new String(data, 0, packet.getLength(), "UTF-8")
                    //                 .replace("'", "\'")
                    //                 .replace("\r", "\\r")
                    //                 .replace("\n", "\\n");
                    String msg = dataEncoder.decode(data);
                    msg = msg.replace("'", "\'")
                                     .replace("\r", "\\r")
                                     .replace("\n", "\\n");
                    Log.d(TAG, "Receive msg :" + msg);
                    String address = packet.getAddress().getHostAddress();
                    int port = packet.getPort();

                    Multicast.this.webView.sendJavascript(
                        "cordova.require('org.jarvus.cordova.multicast.multicast')._onMessage("
                            + this.m_socketId + ","
                            + "'" + msg + "',"
                            + "'" + address + "',"
                            + port + ")");
                } catch (Exception e) {
                    Log.d(TAG, "Receive exception:" + e.toString());
                    return;
                }
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        final int id = data.getInt(0);
        DatagramSocket socket = m_sockets.get(id);
        Log.d(TAG, "action is " + action);
        if (action.equals("initLocation")){
            Log.d(TAG, "will initLocation");
            m_locListener = new LocationListener();
            m_locListener.start();
            callbackContext.success();
        }else if(action.equals("getLocation")){
            Log.d(TAG, "will  getLocation");
            if(m_locListener != null){
                double[] locations = m_locListener.getLocation();
                Log.d(TAG, "m_locListener is not  null,location is " + locations[0] + " , " + locations[1] );
                Log.d(TAG, "["+locations[0] +","+ locations[1] +"]");
                callbackContext.success("["+locations[0] +","+ locations[1] +"]");
            }else{
                Log.d(TAG, "m_locListener is null");
                callbackContext.error("Please call initLocation() to init Location Thread first");
            }
        }else if(action.equals("create")) {
            assert socket == null;
            final boolean isMulticast = data.getBoolean(1);
            try {
                socket = isMulticast ? new MulticastSocket(null) : new DatagramSocket(null);
                m_sockets.put(id, socket);
                callbackContext.success();
            } catch (Exception e) {
                Log.d(TAG, "Create exception:" + e.toString());
                callbackContext.error(e.toString());
            }
        } else if (action.equals("bind")) {
            final int port = data.getInt(1);
            try {
                String interfaceName = data.getString(2);
                Log.d(TAG, "the interfaceName is " + interfaceName);
                String hostAddress = findNetworkInterfaceByName(interfaceName);
                if(hostAddress != null){
                    Log.d(TAG, "will bind " + hostAddress);
                    socket.bind(new InetSocketAddress(hostAddress,port));
                }else{
                    Log.d(TAG, "not query valid ip address");
                    socket.bind(new InetSocketAddress(port));
                }
                SocketListener listener = new SocketListener(id, socket);
                m_listeners.put(id, listener);
                listener.start();

                callbackContext.success();
            } catch (Exception e) {
                Log.d(TAG, "Bind exception:" + e.toString());
                callbackContext.error(e.toString());
            }
        } else if (action.equals("joinGroup")) {
            final String address = data.getString(1);
            MulticastSocket msocket = (MulticastSocket) socket;
            try {
                msocket.joinGroup(InetAddress.getByName(address));
                callbackContext.success();
            } catch (Exception e) {
                Log.d(TAG, "joinGroup exception:" + e.toString());
                callbackContext.error(e.toString());
            }
        } else if (action.equals("leaveGroup")) {
            final String address = data.getString(1);
            MulticastSocket msocket = (MulticastSocket) socket;
            try {
                msocket.leaveGroup(InetAddress.getByName(address));
                callbackContext.success();
            } catch (Exception e) {
                Log.d(TAG, "leaveGroup exception:" + e.toString());
                callbackContext.error(e.toString());
            }
        } else if (action.equals("send")) {
            String message = data.getString(1);
            String address = data.getString(2);
            int port = data.getInt(3);

            try {
                byte[] encodeData = dataEncoder.encode(message);
                Log.d(TAG, "send msg :" + message);
                DatagramPacket packet = new DatagramPacket(encodeData, encodeData.length, InetAddress.getByName(address), port);
                socket.send(packet);
                callbackContext.success(message);
            } catch (IOException ioe) {
                Log.d(TAG, "send exception:" + ioe.toString());
                callbackContext.error("IOException: " + ioe.toString());
            }
        } else if (action.equals("close")) {
            if (socket != null) {
                socket.close();
                m_sockets.remove(id);
                SocketListener listener = m_listeners.get(id);
                if (listener != null) {
                    listener.interrupt();
                    m_listeners.remove(id);
                }
            }
            callbackContext.success();
        } else {
            return false; // 'MethodNotFound'
        }

        return true;
    }


    
	public String findNetworkInterfaceByName(String name) throws SocketException{
		for (Enumeration<NetworkInterface> en =
	              NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

	            NetworkInterface intf = en.nextElement();
	            String networkName =  intf.getName();
	            if(networkName.equals(name))
	            {
	            	for (Enumeration<InetAddress> enumIpAddr =
		                     intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
	            		
	            		InetAddress inetAddress = enumIpAddr.nextElement();
	            		 if (inetAddress instanceof Inet4Address) {
	            		     return inetAddress.getHostAddress();
	            		 }
		            }
	            	break;
	            }
	        }
		return null;
	}
}

