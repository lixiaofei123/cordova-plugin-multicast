package cn.com.geovis;

/**
 * 
 * 数据压缩接口，对编码后的数据进行进一步的压缩/解压，以此减小传输体积
 * @author lixiaofei
 *
 */
public interface ICompress {
	
	public byte[] compress(byte[] unCompressData);
	
	public byte[] unCompress(byte[] compressData);

}
