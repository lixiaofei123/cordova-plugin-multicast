package cn.com.geovis;

import java.nio.ByteBuffer;
import org.json.JSONObject;

public class TaskTimeSyncDataEncoder extends AbstractDataEncoder {
  public static final byte MESSAGE_TYPE = 31;

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
      return type.equals("sync-free_taskCompletion") && op.equals("time");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  protected byte[] encode0(String data) {
    try {
      JSONObject jsonData = new JSONObject(data);
      long sendTime = 0L;
      if (jsonData.has("sendTime")) {
        sendTime = jsonData.getLong("sendTime");
      }
      String taskinfo = jsonData.getString("taskinfo");
      byte[] taskinfoBytes = ByteUtils.stringToByte(taskinfo);
      int taskInfoLength = taskinfoBytes.length;

      ByteBuffer buffer = ByteBuffer.allocate(8 + 4 + taskInfoLength);
      buffer.putLong(sendTime);
      buffer.putInt(taskInfoLength);
      buffer.put(taskinfoBytes);

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
      int taskInfoLength = buffer.getInt();

      byte[] taskinfoBytes = new byte[taskInfoLength];
      buffer.get(taskinfoBytes);
      String taskinfo = ByteUtils.byteToString(taskinfoBytes);

      JSONObject jsonData = new JSONObject();
      jsonData.put("type", "sync-free_taskCompletion");
      jsonData.put("op", "time");
      jsonData.put("sendTime", sendTime);
      jsonData.put("taskinfo", taskinfo);

      return jsonData.toString();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    //		 type: "sync-free_taskCompletion",
    //      op: "time",
    //      taskinfo: JSON.stringify(titem),

    String msg =
      "{type:'sync-free_taskCompletion',op:'time',taskinfo:'今天天气明媚，小雨转晴，温度36度'}";
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
