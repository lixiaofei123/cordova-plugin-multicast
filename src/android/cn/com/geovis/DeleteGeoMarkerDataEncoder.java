package cn.com.geovis;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class DeleteGeoMarkerDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 35;

	public final Map<String, Byte> classplotMap = new HashMap<String, Byte>();

	public DeleteGeoMarkerDataEncoder() {
		classplotMap.put("flag", (byte) 1);
		classplotMap.put("geometric", (byte) 2);
	}

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
			return type.equals("sync-geo") && op.equals("delete");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			short userId = (short) jsonData.getInt("operator");
			long sendTime = 0L;
			if (jsonData.has("sendTime")) {
				sendTime = jsonData.getLong("sendTime");
			}

			String id = jsonData.getString("id");
			String classplot = jsonData.getString("classplot");

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 1);
			buffer.putLong(sendTime);
			buffer.putShort(userId);
			buffer.put(ByteUtils.uuidToByte(id));
			buffer.put(classplotMap.getOrDefault(classplot, (byte) 0)
					.byteValue());

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
			String id = ByteUtils.byteToUuid(uuidbytes);

			byte classplotNum = buffer.get();
			String classplot = "unknown";
			for (String key : classplotMap.keySet()) {
				if (classplotMap.get(key).byteValue() == classplotNum) {
					classplot = key;
					break;
				}
			}

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", "sync-geo");
			jsonData.put("op", "delete");
			jsonData.put("id", id);
			jsonData.put("sendTime", sendTime);
			jsonData.put("classplot", classplot);
			jsonData.put("operator", userId);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		// type: "sync-geo",
		// op: "delete",
		// id: id,
		// classplot: "flag",
		// operator: this.me,

		String msg = "{type:'sync-geo',op:'delete',id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',"
				+ "sendTime:1601196974815,classplot:'geometric',operator:123}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
