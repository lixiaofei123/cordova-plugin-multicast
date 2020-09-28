package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class TaskSyncDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 23;

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {

		JSONObject jsonData = new JSONObject(data);
		String type = jsonData.getString("type");
		String op = jsonData.getString("op");
		return type.equals("sync-taskinfo") && op.equals("assign");

	}

	@Override
	protected byte[] encode0(String data) {

		JSONObject jsonData = new JSONObject(data);
		short userId = (short) jsonData.getInt("operator");
		long sendTime = 0L;
		if (jsonData.has("sendTime")) {
			sendTime = jsonData.getLong("sendTime");
		}
		String taskinfo = jsonData.getJSONObject("taskinfo").toString();
		byte[] taskinfoBytes = ByteUtils.stringToByte(taskinfo);
		int taskinfoLength = taskinfoBytes.length;

		ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 4 + taskinfoLength);
		buffer.putLong(sendTime);
		buffer.putShort(userId);
		buffer.putInt(taskinfoLength);
		buffer.put(taskinfoBytes);

		return buffer.array();
	}

	@Override
	protected String decode0(byte[] data) {

		ByteBuffer buffer = ByteBuffer.wrap(data);
		long sendTime = buffer.getLong();
		int userId = buffer.getShort();
		int taskinfoLength = buffer.getInt();

		byte[] taskinfoBytes = new byte[taskinfoLength];
		buffer.get(taskinfoBytes);
		String taskinfo = ByteUtils.byteToString(taskinfoBytes);

		JSONObject jsonData = new JSONObject();
		jsonData.put("type", "sync-taskinfo");
		jsonData.put("op", "assign");
		jsonData.put("sendTime", sendTime);
		jsonData.put("taskinfo", new JSONObject(taskinfo));
		jsonData.put("operator", userId);

		return jsonData.toString();
	}

	public static void main(String[] args) {

		// type: "sync-taskinfo",
		// op: "assign",
		// data: msg,
		// operator: this.me,
		String msg = "{type:'sync-taskinfo',op:'assign',taskinfo:{name:'text'},operator:'145'}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
