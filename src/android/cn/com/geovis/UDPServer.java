package cn.com.geovis;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {

	public static void main(String[] args) throws SocketException {
		
		int port = 32630;
		if(args.length != 0){
			port = Integer.parseInt(args[0]);
		}
		
		DataEncoder dataEncoder = new DataEncoder();
		
		// socket
		@SuppressWarnings("resource")
		DatagramSocket udpSocket = new DatagramSocket(port);
		//udpSocket.bind(new InetSocketAddress(8888));
		System.out.println("bind port successful");
		byte[] data = new byte[40960];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		System.out.println("start listening");
		while (true) {
			try {
				udpSocket.receive(packet);
				String address = packet.getAddress().getHostAddress();
				String message = dataEncoder.decode(data);
				System.out
						.println("receive from " + address + ", msg is " + message);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
