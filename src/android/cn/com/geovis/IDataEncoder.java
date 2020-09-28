package cn.com.geovis;

/**
 * 根据协议将数据进行转换
 * @author lixiaofei
 *
 */
public interface IDataEncoder {
	
	public byte[] encode(String data);
	
	public String decode(byte[] data);
	
	public byte messageType();
	
	public boolean canHandle(String data);
	
}
