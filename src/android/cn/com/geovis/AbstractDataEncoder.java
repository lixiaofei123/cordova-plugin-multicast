package cn.com.geovis;

import java.nio.ByteBuffer;

public abstract class AbstractDataEncoder implements IDataEncoder {
	
	@Override
	public byte[] encode(String data) {
		
		byte[] payload = encode0(data);
		
		// 头部预留16个字节来放置消息ID和消息序列号等
		ByteBuffer buffer = ByteBuffer.allocate(payload.length + 16 + 1);
		{
			// 暂时随便填充一些内容
			buffer.putLong(1L);
			buffer.putLong(1L);
		}
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
		ByteBuffer buffer = ByteBuffer.wrap(data, 17, data.length - 17);
		byte[] payload = new byte[data.length - 17];
		buffer.get(payload);
		return decode0(payload);
	}
		
	abstract protected byte[] encode0(String data);

	abstract protected String decode0(byte[] data);
	
	

}
