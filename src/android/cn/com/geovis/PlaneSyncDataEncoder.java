package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlaneSyncDataEncoder extends AbstractDataEncoder {

	public final static byte MESSAGE_TYPE = 21;

	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}
	
	@Override
	public boolean canHandle(String data) {

		JSONObject jsonData = new JSONObject(data);
		String type = jsonData.getString("type");
		return type.equals("update-plane");

	}
	
	@Override
	protected byte[] encode0(String data) {

		JSONObject jsonData = new JSONObject(data);
		short userId = (short) jsonData.getInt("author");
		long sendTime = 0L;
		if (jsonData.has("sendTime")) {
			sendTime = jsonData.getLong("sendTime");
		}
		boolean isflying = jsonData.getBoolean("isflying");
		JSONArray locationArrays = jsonData.getJSONArray("position");
		double lat = locationArrays.getDouble(0);
		double lng = locationArrays.getDouble(1);
		
		JSONArray offSetArrays = jsonData.getJSONArray("offset");
		short x = (short) offSetArrays.getInt(0);
		short y = (short) offSetArrays.getInt(1);
		
		//String result = jsonData.getString("result");
		String result = "data";
		byte[] resultBytes = ByteUtils.stringToByte(result);
		int resultLength = resultBytes.length;
		
		ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 1 + 8 + 8 + 2 + 2 + 4 + resultLength);
		buffer.putLong(sendTime);
		buffer.putShort(userId);
		buffer.put(isflying ? (byte)1 : (byte)0);
		buffer.putDouble(lat);
		buffer.putDouble(lng);
		buffer.putShort(x);
		buffer.putShort(y);
		buffer.putInt(resultLength);
		buffer.put(resultBytes);
		
		return buffer.array();
	}

	@Override
	protected String decode0(byte[] data) {

		ByteBuffer buffer = ByteBuffer.wrap(data);
		long sendTime = buffer.getLong();
		int userId = buffer.getShort();
		boolean isflying = buffer.get() == (byte)1;
		double lat = buffer.getDouble();
		double lng = buffer.getDouble();
		short x = buffer.getShort();
		short y = buffer.getShort();
		int resultLength = buffer.getInt();
		
		byte[] resultBytes = new byte[resultLength];
		buffer.get(resultBytes);
		String result = ByteUtils.byteToString(resultBytes);



		JSONObject jsonData = new JSONObject();
		jsonData.put("type", "update-plane");
		jsonData.put("sendTime", sendTime);
		jsonData.put("isflying", isflying);
		jsonData.put("position", new double[]{lat,lng});
		jsonData.put("offSet", new int[]{x,y});
		jsonData.put("result", result);
		jsonData.put("author", userId);

		return jsonData.toString();
	}

	public static void main(String[] args) {

//		type: "update-plane",
	//  isflying: this.isFlying,
	//  position: this.plane_geojson,
	//  offSet: this.marker.getOffset(),
	//  result: this.result,
	//  author: this.myInfo.name,
		
		String msg = "{type:'update-plane',isflying:false,position:[123.145321,45.1234],offset:[15,12],result:'fff',author:14523}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
