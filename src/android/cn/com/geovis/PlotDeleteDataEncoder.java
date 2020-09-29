package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class PlotDeleteDataEncoder extends AbstractDataEncoder {

	// type: "sync-icon",
	// op: "delete",
	public final static byte MESSAGE_TYPE = 26;
	public final static String JSTYPE = "sync-icon";
	public final static String OP = "delete";

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	// id: iconid,
	// rendermode: "2d",
	// operator: this.me,

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
			short userId = (short) jsonData.getInt("operator");

			int len = uuid.length;

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 4 + len);

			buffer.putLong(createTime);
			buffer.putShort(userId);
			buffer.putInt(len);
			buffer.put(uuid);

			return buffer.array();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	// type: "sync-icon",
	// op: "delete",
	// id: iconid,
	// rendermode: "2d",
	// operator: this.me,

	@Override
	protected String decode0(byte[] data) {

		try {
			ByteBuffer buffer = ByteBuffer.wrap(data);
			long createTime = buffer.getLong();

			int userID = buffer.getShort();
			int len = buffer.getInt();
			byte[] dataByte = new byte[len];
			buffer.get(dataByte);
			String uuid = ByteUtils.byteToUuid(dataByte);

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", JSTYPE);
			jsonData.put("op", OP);
			jsonData.put("create_time", createTime);
			jsonData.put("id", uuid);
			jsonData.put("operator", userID);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		// type: "sync-icon",
		// op: "delete",
		// id: iconid,
		// rendermode: "2d",
		// operator: this.me,

		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',operator:145,type:'sync-icon',op:'delete',create_time:1601204563066}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
