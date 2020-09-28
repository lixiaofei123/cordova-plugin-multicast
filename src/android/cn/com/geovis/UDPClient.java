package cn.com.geovis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
	
	public static void main(String[] args) throws SocketException {
		
		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',type:'chat',subType:'text',sender:12345,sendTime:1601189882827,data:'今天天气明媚，小雨转晴，温度36度'}";
		DataEncoder dataEncoder = new DataEncoder();
		
		String address  = "192.168.0.5";
		int port = 32630;
		if(args.length != 0){
			address = args[0];
			port = Integer.parseInt(args[1]);
		}
		
		@SuppressWarnings("resource")
		DatagramSocket udpSocket = new DatagramSocket();
		
		while(true){
			
			try {
				 Thread.sleep(5000);
				 byte[] data = dataEncoder.encode(msg);
	             DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(address), port);
	             udpSocket.send(packet);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
