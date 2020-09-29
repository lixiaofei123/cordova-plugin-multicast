package cn.com.geovis;

import java.nio.ByteBuffer;
import org.json.JSONObject;

public class DroneRequireChangeDataEncoder extends AbstractDataEncoder {

	
	public static final byte MESSAGE_TYPE = 36;
	
	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {
		 try {
		      JSONObject jsonData = new JSONObject(data);
		      String type = jsonData.getString("type");

		      return type.equals("requireChange");
		    } catch (Exception e) {
		      throw new RuntimeException(e.getMessage());
		    }
	}

	@Override
	protected byte[] encode0(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			short newMaster = (short) jsonData.getInt("newMaster");
			
			ByteBuffer buffer = ByteBuffer.allocate(2);
			buffer.putShort(newMaster);

			return buffer.array();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected String decode0(byte[] data) {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(data);

			int newMaster = buffer.getShort();
			JSONObject jsonData = new JSONObject();
			jsonData.put("type", "requireChange");
			jsonData.put("newMaster", newMaster);

			return jsonData.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static void main(String[] args) {

		String msg = "{type:'requireChange',newMaster:123}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}


}
