package cn.com.geovis;

import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class AbstractDataEncoder implements IDataEncoder {
	
	private static final int HEADLENGTH = 17;
	
	@Override
	public byte[] encode(String data) {
		
		byte[] payload = encode0(data);
		
		String packageId = UUID.randomUUID().toString();
		
		// 头部预留{HEADLENGTH}个字节来放置消息ID和消息序列号等
		// Header = packageId + msgType
		ByteBuffer buffer = ByteBuffer.allocate(payload.length + HEADLENGTH);
		buffer.put(ByteUtils.uuidToByte(packageId));
		buffer.put((byte)messageType());
		buffer.put(payload);
		//TODO 尾部加入合法校验字符串，例如和指定字符串混合后进行MD5加密
		
		return buffer.array();
	}

	@Override
	public String decode(byte[] data) {
				
		//TODO 根据尾部的校验字符串来校验数据是否合法，例如和指定字符串混合后进行MD5加密后，然后和校验数据字符串相比较

//		byte[] payload = new byte[unCompressData.length - 17];
//		for(int i = 0; i < payload.length; i++){
//			payload[i] = unCompressData[i + 17];
//		}
		ByteBuffer buffer = ByteBuffer.wrap(data, HEADLENGTH, data.length - HEADLENGTH);
		byte[] payload = new byte[data.length - HEADLENGTH];
		buffer.get(payload);
		return decode0(payload);
	}
		
	abstract protected byte[] encode0(String data);

	abstract protected String decode0(byte[] data);
	
	

}
