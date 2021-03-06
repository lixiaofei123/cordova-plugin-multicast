package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONObject;

public class HeartbeatDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 15;

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			String type = jsonData.getString("type");
			return type.equals("heartbeat");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	protected byte[] encode0(String data) {

		try {
			JSONObject jsonData = new JSONObject(data);
			short userId = (short) jsonData.getInt("userId");
			long sendTime = 0L;
			if (jsonData.has("sendTime")) {
				sendTime = jsonData.getLong("sendTime");
			}
			JSONArray locationArrays = jsonData.getJSONArray("location");
			double lat = locationArrays.getDouble(0);
			double lng = locationArrays.getDouble(1);

			ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16);
			buffer.putLong(sendTime);
			buffer.putShort(userId);
			buffer.putDouble(lat);
			buffer.putDouble(lng);

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
			double lat = buffer.getDouble();
			double lng = buffer.getDouble();

			JSONObject jsonData = new JSONObject();
			jsonData.put("type", "heartbeat");
			jsonData.put("username", userId + "");
			jsonData.put("userId", userId + "");
			jsonData.put("sendTime", sendTime);
			
			JSONArray locations = new JSONArray();
			locations.put(lat);
			locations.put(lng);
			jsonData.put("location", locations);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

}
