package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class AddPlotDataEncoder extends AbstractDataEncoder {

	// type: "sync-icon",
	// op: "add",

	public final static byte MESSAGE_TYPE = 25;
	public final static String JSTYPE = "sync-icon";
	public final static String OP = "add";

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	// features: [newdata], // should be string
	// id: id,
	// rendermode: '3d',
	// author: this.me,
	// isdelete: false,

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

	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);

			long createTime = 0L;
			if (jsonData.has("create_time")) {
				createTime = jsonData.getLong("create_time");
			}

			byte[] uuid = ByteUtils.uuidToByte(jsonData.getString("id"));
			short userId = (short) jsonData.getInt("author");
			byte[] dataByte = ByteUtils.stringToByte(jsonData.getJSONObject(
					"features").toString());

			int uuidLen = uuid.length;
			int dataLen = dataByte.length;

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 4 + uuidLen
					+ dataLen);

			buffer.putLong(createTime);
			buffer.putShort(userId);
			buffer.putInt(uuidLen + dataLen);
			buffer.put(uuid);
			buffer.put(dataByte);

			return buffer.array();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	// features: [newdata],
	// id: id,
	// rendermode: '3d',
	// author: this.me,
	// isdelete: false,

	@Override
	protected String decode0(byte[] data) {

		try {
			ByteBuffer buffer = ByteBuffer.wrap(data);

			long createTime = buffer.getLong();
			int userID = buffer.getShort();
			int len = buffer.getInt();
			byte[] uuidByte = new byte[16];
			buffer.get(uuidByte);
			String uuidStr = ByteUtils.byteToUuid(uuidByte);
			byte[] dataByte = new byte[len - 16];
			buffer.get(dataByte);
			String dataStr = ByteUtils.byteToString(dataByte);

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", JSTYPE);
			jsonData.put("op", OP);
			jsonData.put("create_time", createTime);
			jsonData.put("id", uuidStr);
			jsonData.put("features", new JSONObject(dataStr));
			jsonData.put("author", userID);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		// type: 'sync-icon',
		// op: 'add',
		// rendermode: '2d',
		// features: [item], // TODO: whisperchi
		// id: item.properties.pid,
		// createtime: time,
		// author: this.me,
		// isdelete: false,

		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',features:{name:'lixiaofei'},author:125,type:'sync-icon',op:'add',create_time:1601204563066}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
