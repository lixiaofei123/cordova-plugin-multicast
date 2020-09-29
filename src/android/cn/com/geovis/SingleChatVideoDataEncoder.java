package cn.com.geovis;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class SingleChatVideoDataEncoder extends AbstractDataEncoder {
	
  public static final byte MESSAGE_TYPE = 4;

  @Override
  public byte messageType() {
    return MESSAGE_TYPE;
  }

  @Override
  protected byte[] encode0(String data) {
    try {
      JSONObject jsonData = new JSONObject(data);
      String msgId = jsonData.getString("id");
      JSONObject  dataObj =  jsonData.getJSONObject("data");
      long sendTime = jsonData.getLong("sendTime");
      short userId = (short) jsonData.getInt("sender");

      String msgContent = dataObj.getString("data");
      byte[] msgContentBytes = ByteUtils.stringToByte(msgContent);
      ByteBuffer buffer = ByteBuffer.allocate(
        8 + 2 + 16 + 4 + msgContentBytes.length
      );
      buffer.putLong(sendTime);
      buffer.putShort(userId);
      buffer.put(ByteUtils.uuidToByte(msgId));
      buffer.putInt(msgContentBytes.length);
      buffer.put(msgContentBytes);

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
      String msgId = ByteUtils.byteToUuid(uuidbytes);

      int contentLength = buffer.getInt();
      byte[] contentBytes = new byte[contentLength];
      buffer.get(contentBytes);
      String content = ByteUtils.byteToString(contentBytes);

      JSONObject jsonData = new JSONObject();
      jsonData.put("id", msgId);
      jsonData.put("type", "chat");
      jsonData.put("subType", "video");
      jsonData.put("sender", userId);
      jsonData.put("sendTime", sendTime);
      
      JSONObject  dataObj = new JSONObject();
      dataObj.put("data", content);
      jsonData.put("data", dataObj);

      return jsonData.toString();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public boolean canHandle(String data) {
    try {
      JSONObject jsonData = new JSONObject(data);
      String type = jsonData.getString("type");
      String subType = jsonData.getString("subType");

      return type.equals("chat") && subType.equals("video");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  public static void main(String[] args) {

		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',type:'chat',subType:'video',receiver:123,sender:456,sendTime:1601347381497,data:{data:'data:image/png;base64,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'}}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}

}
