package cn.com.geovis;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class AddGeoMarkerDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 34;

	public final Map<String, Byte> classplotMap = new HashMap<String, Byte>();

	public AddGeoMarkerDataEncoder() {
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
			return type.equals("sync-geo") && op.equals("add");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			short userId = (short) jsonData.getInt("author");
			long sendTime = 0L;
			if (jsonData.has("sendTime")) {
				sendTime = jsonData.getLong("sendTime");
			}

			String id = jsonData.getString("id");
			String classplot = jsonData.getString("classplot");

			String features = jsonData.getJSONObject("features").toString();
			byte[] featuresBytes = ByteUtils.stringToByte(features);
			int featuresBytesLength = featuresBytes.length;

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 1 + 4
					+ featuresBytesLength);
			buffer.putLong(sendTime);
			buffer.putShort(userId);
			buffer.put(ByteUtils.uuidToByte(id));
			buffer.put(classplotMap.getOrDefault(classplot, (byte) 0)
					.byteValue());
			buffer.putInt(featuresBytesLength);
			buffer.put(featuresBytes);

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

			int featuresBytesLength = buffer.getInt();
			byte[] dataByte = new byte[featuresBytesLength];
			buffer.get(dataByte);
			String features = ByteUtils.byteToString(dataByte);

			// type: "sync-geo",
			// op: "add",
			// features: flagfeature,
			// id: r,
			// classplot: "flag",
			// author: this.me,

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", "sync-geo");
			jsonData.put("op", "add");
			jsonData.put("id", id);
			jsonData.put("sendTime", sendTime);
			jsonData.put("classplot", classplot);
			jsonData.put("author", userId);
			jsonData.put("features", new JSONObject(features));

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static void main(String[] args) {

		String msg = "{type:'sync-geo',op:'add',id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',"
				+ "sendTime:1601196974815,classplot:'geometric',author:123,features: { name : 'test'}}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
