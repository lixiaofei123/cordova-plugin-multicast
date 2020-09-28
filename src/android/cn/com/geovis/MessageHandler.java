package cn.com.geovis;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MessageHandler {

	private boolean start = false;
	private final static int MAX_RETRY_TIMES = 3;
	private final static int PARALLELISM_THRESHOLD = 6;
	private final static long RETRY_TIMEOUT = 1 * 1000;
	
	private final static long CLEAR_TIMEOUT = 30 * 1000;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(2); // 1个用来检测发送的消息，一个用来检测接收的消息
	private ExecutorService sendExecutorService = Executors.newFixedThreadPool(5); //重新发送或者确认
	private ConcurrentHashMap<String, WrapMessage> toBeConfirmMessages = new ConcurrentHashMap<String, WrapMessage>(
			20);
	private ConcurrentHashMap<String, WrapConsumeMessage> confirmMessages = new ConcurrentHashMap<String, WrapConsumeMessage>(
			20);

	private DatagramSocket socket;

	public MessageHandler(DatagramSocket socket) {
		this.socket = socket;
	}
	
	private static class WrapConsumeMessage{
		
		public WrapConsumeMessage(String packageId){
			this.packageId = packageId;
			this.consumeTime = System.currentTimeMillis();
		}
		
		public String packageId;
		public long consumeTime;
	}
	
	private static class WrapMessage {
		public WrapMessage(String packageId, byte[] message, String address,
				int port) {
			this.packageId = packageId;
			this.message = message;
			this.sendTime = System.currentTimeMillis();
			this.address = address;
			this.port = port;
		}

		public String packageId;
		public String address;
		public int port;
		public byte[] message;
		public long sendTime;
		public int retryTimes = 0;
	}

	public void ProductMessage(byte[] message, String destAddress, int destPort) {
		String packageId = packageId(message);
		WrapMessage wrapMessage = new WrapMessage(packageId, message,
				destAddress, destPort);
		toBeConfirmMessages.put(packageId, wrapMessage);

	}
	
	public boolean ConsumeMessage(byte[] message){
		String packageId = packageId(message);
		if(confirmMessages.containsKey(packageId)){
			return false;
		}else{
			WrapConsumeMessage wrapConsumeMessage = new WrapConsumeMessage(packageId);
			confirmMessages.put(packageId, wrapConsumeMessage);
			sendExecutorService.execute(new Runnable() {
				
				@Override
				public void run() {
					
					
				}
			});
			return true;
		}
		
	}

	public void ackMessage(String packageId) {
		toBeConfirmMessages.remove(packageId);
	}

	public String packageId(byte[] message) {
		ByteBuffer buffer = ByteBuffer.wrap(message, 0, 16);
		byte[] uuidBytes = new byte[16];
		buffer.get(uuidBytes);
		return ByteUtils.byteToUuid(uuidBytes);
	}

	public void startHandle() {
		start = true;
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				while (start) {
					try {
						final long currentTime = System.currentTimeMillis();
						toBeConfirmMessages.forEachValue(PARALLELISM_THRESHOLD,
								new Consumer<WrapMessage>() {
									@Override
									public void accept(WrapMessage t) {
										if (t.retryTimes > MAX_RETRY_TIMES) {
											// 超过最大尝试次数，删除此包
											toBeConfirmMessages
													.remove(t.packageId);
										}

										if (currentTime - t.sendTime >= RETRY_TIMEOUT) {
											t.sendTime = currentTime;
											t.retryTimes++;
											// 重新发送
											final byte[] data = t.message;
											final String address = t.address;
											final int port = t.port;
											
											sendExecutorService.execute(new Runnable() {
												
												@Override
												public void run() {
													
													try {
														DatagramPacket packet = new DatagramPacket(
																data,
																data.length,
																InetAddress
																		.getByName(address),
																port);
														socket.send(packet);
													} catch (IOException e) {
														e.printStackTrace();
													}

												}
											});
											
											
										}
									}
								});
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				while(start){
					try {
						final long currentTime = System.currentTimeMillis();
						confirmMessages.forEachValue(PARALLELISM_THRESHOLD, new Consumer<WrapConsumeMessage>() {
							@Override
							public void accept(WrapConsumeMessage t) {
								if (currentTime - t.consumeTime >= CLEAR_TIMEOUT){
									confirmMessages.remove(t.packageId);
								}
							}
						});
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void stopHandle() {
		start = false;
		executorService.shutdown();
	}

}
