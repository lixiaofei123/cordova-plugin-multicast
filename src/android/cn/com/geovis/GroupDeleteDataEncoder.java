package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class GroupDeleteDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 19;

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			String type = jsonData.getString("type");
			return type.equals("delete-Group");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			String groupId = jsonData.getString("id");
			long sendTime = 0L;
			if (jsonData.has("sendTime")) {
				sendTime = jsonData.getLong("sendTime");
			}
			short sender = (short) jsonData.getInt("author");
			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16);
			buffer.putLong(sendTime);
			buffer.putShort(sender);
			buffer.put(ByteUtils.uuidToByte(groupId));

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
			String groupId = ByteUtils.byteToUuid(uuidbytes);

			// {
			// type: "delete-Group",
			// id: gid,
			// author: this.myInfo.name,
			// }

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", "delete-Group");
			jsonData.put("id", groupId);
			jsonData.put("author", userId);
			jsonData.put("sendTime", sendTime);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		String msg = "{type:'delete-Group',id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',author:5421,sendTime:1601196974815}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
