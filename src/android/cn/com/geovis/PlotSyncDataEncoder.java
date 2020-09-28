package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class PlotSyncDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 27;
	public final static String JSTYPE = "sync-icon";
	public final static String OP = "detail";

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			String type = jsonData.getString("type");
			String op = jsonData.getString("op");
			return type.equals(JSTYPE) && op.equals(OP);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	// type: "sync-icon",
	// op: "detail",

	// create_time: time,
	// author: this.me,

	// id: id,

	// features: newjson,

	// is_delete: false,
	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);

			long createTime = 0L;
			if (jsonData.has("create_time")) {
				createTime = jsonData.getLong("create_time");
			}
			short userId = (short) jsonData.getInt("author");
			byte[] uuid = ByteUtils.uuidToByte(jsonData.getString("id"));
			byte[] features = ByteUtils.stringToByte(jsonData.getJSONObject(
					("features")).toString());
			int len = features.length;

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 4 + len);

			buffer.putLong(createTime);
			buffer.putShort(userId);
			buffer.put(uuid);
			buffer.putInt(len);
			buffer.put(features);

			return buffer.array();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	// type: "sync-icon",
	// op: "detail",

	// create_time: time,
	// author: this.me,

	// id: id,

	// features: newjson,

	// is_delete: false,
	@Override
	protected String decode0(byte[] data) {

		try {
			ByteBuffer buffer = ByteBuffer.wrap(data);
			long createTime = buffer.getLong();

			int userID = buffer.getShort();

			byte[] uuidByte = new byte[16];
			buffer.get(uuidByte);
			String id = ByteUtils.byteToUuid(uuidByte);

			int len = buffer.getInt();
			byte[] dataByte = new byte[len];
			buffer.get(dataByte);
			String features = ByteUtils.byteToString(dataByte);

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", JSTYPE);
			jsonData.put("op", OP);
			jsonData.put("create_time", createTime);
			jsonData.put("author", userID);
			jsonData.put("id", id);
			jsonData.put("features", new JSONObject(features));

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		// type: "sync-icon",
		// op: "detail",
		// features: newjson,
		// id: id,
		// create_time: time,
		// author: this.me,
		// is_delete: false,
		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',type:'sync-icon',op:'detail',features:{name:'lixiaofei'},author:145,is_delete:false,create_time:1601204563066}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
