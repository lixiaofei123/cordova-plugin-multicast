package cn.com.geovis;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils {
	
	private ByteUtils(){
		
	}
	
	public static byte[] longToByte(long num){
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(num);
	    return buffer.array();
	}
	
	public static long byteToLong(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getLong();
	}
	
	public static byte[] intToByte(int num){
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.putInt(num);
	    return buffer.array();
	}
	
	public static int byteToInt(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getInt();
	}
	
	public static byte[] shortToByte(short num){
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
	    buffer.putInt(num);
	    return buffer.array();
	}
	
	public static short byteToShort(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getShort();
	}
	
    public static byte[] uuidToByte(String uuidString) {
    	UUID uuid = UUID.fromString(uuidString);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static String byteToUuid(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();
        return (new UUID(high, low)).toString();
    }
    
    public static byte[] stringToByte(String str){
    	try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return new byte[]{};
    }
    
    public static String byteToString(byte[] bytes){
    	try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return "";
    }
}
