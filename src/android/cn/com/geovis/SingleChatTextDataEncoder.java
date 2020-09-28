package cn.com.geovis;

import java.nio.ByteBuffer;
import org.json.JSONObject;

public class SingleChatTextDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 1;
		
	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}
	
	@Override
	protected byte[] encode0(String data) {
		
		JSONObject jsonData = new JSONObject(data);
		String msgId = jsonData.getString("id");
		String msgContent = jsonData.getString("data");
		long sendTime = jsonData.getLong("sendTime");
		short userId = (short) jsonData.getInt("sender");
		
		byte[] msgContentBytes = ByteUtils.stringToByte(msgContent);
		ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 4 + msgContentBytes.length);
		buffer.putLong(sendTime);
		buffer.putShort(userId);
		buffer.put(ByteUtils.uuidToByte(msgId));
		buffer.putInt(msgContentBytes.length);
		buffer.put(msgContentBytes);
		
		return buffer.array();
	}

	@Override
	protected String decode0(byte[] data) {
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		long sendTime = buffer.getLong();
		int userId = buffer.getShort();
		
		byte[] uuidbytes = new byte[16];
		buffer.get(uuidbytes);
		String msgId = ByteUtils.byteToUuid(uuidbytes);
		
		int contentLength = buffer.getInt();
		byte[] contentBytes = new byte[contentLength];
		buffer.get(contentBytes);
		String content = ByteUtils.byteToString(contentBytes);
		
		JSONObject jsonData = new JSONObject();
		jsonData.put("id", msgId);
		jsonData.put("type", "chat");
		jsonData.put("subType", "text");
		jsonData.put("sender", userId);
		jsonData.put("sendTime", sendTime);
		jsonData.put("data", content);
		
		return jsonData.toString();
	}

	@Override
	public boolean canHandle(String data) {
		
		JSONObject jsonData = new JSONObject(data);
		String type = jsonData.getString("type");
		String subType = jsonData.getString("subType");
		
		return type.equals("chat") && subType.equals("text");
	}
	
}
