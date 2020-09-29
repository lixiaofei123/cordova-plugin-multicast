package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class SingleChatTextDataEncoder extends AbstractDataEncoder {
	public static final byte MESSAGE_TYPE = 1;

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	protected byte[] encode0(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String msgId = jsonData.getString("id");
			JSONObject dataObj = jsonData.getJSONObject("data");
			long sendTime = jsonData.getLong("sendTime");
			short userId = (short) jsonData.getInt("sender");

			String msgContent = dataObj.getString("text");
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
			jsonData.put("type", "chat");
			jsonData.put("subType", "text");
			jsonData.put("sender", userId);
			jsonData.put("sendTime", sendTime);

			JSONObject dataObj = new JSONObject();
			dataObj.put("text", content);
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

			return type.equals("chat") && subType.equals("text");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void main(String[] args) {

		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',type:'chat',subType:'text',receiver:123,sender:456,sendTime:1601347381497,data:{text:'hello'}}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}
}
