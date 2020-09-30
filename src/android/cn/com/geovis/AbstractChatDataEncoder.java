package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public abstract class AbstractChatDataEncoder extends AbstractDataEncoder {
	
	@Override
	public byte messageType() {
		return messageType0();
	}
	
	abstract protected byte messageType0();
	
	abstract protected String dataProperty();
	
	abstract protected String type();
	
	abstract protected String subtype();

	@Override
	protected byte[] encode0(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String msgId = jsonData.getString("id");
			JSONObject dataObj = jsonData.getJSONObject("data");
			long sendTime = jsonData.getLong("sendTime");
			short userId = (short) jsonData.getInt("sender");

			String msgContent = dataObj.getString(dataProperty());
			byte[] msgContentBytes = ByteUtils.stringToByte(msgContent);
			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 4
					+ msgContentBytes.length);
			buffer.putLong(sendTime);
			buffer.putShort(userId);
			buffer.put(ByteUtils.uuidToByte(msgId));
			buffer.putInt(msgContentBytes.length);
			buffer.put(msgContentBytes);

			return buffer.array();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected String decode0(byte[] data) {
		try {
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
			jsonData.put("type", type());
			jsonData.put("subType", subtype());
			jsonData.put("sender", userId +"");
			jsonData.put("sendTime", sendTime);

			JSONObject dataObj = new JSONObject();
			dataObj.put(dataProperty(), content);
			jsonData.put("data", dataObj);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean canHandle(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String type = jsonData.getString("type");
			String subType = jsonData.getString("subType");

			return type.equals(type()) && subType.equals(subtype());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
