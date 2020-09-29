package cn.com.geovis;

import java.nio.ByteBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

public class GroupUpdateDataEncoder extends AbstractDataEncoder {
  public static final byte MESSAGE_TYPE = 20;

  @Override
  public byte messageType() {
    return MESSAGE_TYPE;
  }

  @Override
  public boolean canHandle(String data) {
    try {
      JSONObject jsonData = new JSONObject(data);
      String type = jsonData.getString("type");
      return type.equals("update-Group");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  //	{
  //        type: 'update-Group',
  //        group: group,
  //        groupId: groupId,
  //        groupAuthor: group.author,
  //        gid: group.gid,
  //        groupCount: group.groupCount,
  //        isShow: group.isShow,
  //        author: this.myInfo.name,
  //      }

  @Override
  protected byte[] encode0(String data) {
    try {
      JSONObject jsonData = new JSONObject(data);
      String groupId = jsonData.getString("groupId");
      long sendTime = 0L;
      if (jsonData.has("sendTime")) {
        sendTime = jsonData.getLong("sendTime");
      }
      short sender = (short) jsonData.getInt("author");

      JSONArray parters = jsonData.getJSONArray("data");
      int nums = parters.length();

      ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + 16 + 4 + 2 * nums);
      buffer.putLong(sendTime);
      buffer.putShort(sender);
      buffer.put(ByteUtils.uuidToByte(groupId));
      buffer.putInt(nums);

      for (int i = 0; i < nums; i++) {
        JSONObject obj = parters.getJSONObject(i);
        short userId = (short) obj.getInt("userId");
        buffer.putShort(userId);
      }

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
      String groupId = ByteUtils.byteToUuid(uuidbytes);

      int num = buffer.getInt();

      JSONObject jsonData = new JSONObject();
      jsonData.put("type", "add-newGroup");
      jsonData.put("op", "add");
      jsonData.put("id", groupId);
      jsonData.put("author", userId);
      jsonData.put("sendTime", sendTime);

      JSONArray array = new JSONArray();
      for (int i = 0; i < num; i++) {
        JSONObject parter = new JSONObject();
        parter.put("userId", buffer.getShort());
        array.put(parter);
      }

      jsonData.put("data", array);

      return jsonData.toString();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    String msg =
      "{type:'add-newGroup',op:'add',id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',sendTime:1601196974815,data:[{userId:1},{userId:2},{userId:3},{userId:4},{userId:5},{userId:6},],author:1234}";
    System.out.println(
      "编码后的数据长度是" + ByteUtils.stringToByte(msg).length
    );

    DataEncoder data = new DataEncoder();
    byte[] datas = data.encode(msg);
    System.out.println("编码后的数据长度是" + datas.length);

    String smsg = data.decode(datas);
    System.out.println("数据还原:" + smsg);
  }
}
