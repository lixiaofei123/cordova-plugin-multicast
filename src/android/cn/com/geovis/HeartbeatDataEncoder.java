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
		JSONObject jsonData = new JSONObject(data);
		String type = jsonData.getString("type");
		return type.equals("heartbeat");
	}

	@Override
	protected byte[] encode0(String data) {
		
		JSONObject jsonData = new JSONObject(data);
		short userId = (short) jsonData.getInt("userId");
		long sendTime = 0L;
		if(jsonData.has("sendTime")){
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
	}

	@Override
	protected String decode0(byte[] data) {
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		long sendTime = buffer.getLong();
		int userId = buffer.getShort();
		double lat = buffer.getDouble();
		double lng = buffer.getDouble();
		
		JSONObject jsonData = new JSONObject();
		jsonData.put("type", "heartbeat");
		jsonData.put("username", userId);
		jsonData.put("userId", userId);
		jsonData.put("sendTime", sendTime);
		jsonData.put("location", new double[]{lat,lng});
		
		return jsonData.toString();
	}

}
