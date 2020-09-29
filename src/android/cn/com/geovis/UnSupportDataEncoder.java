package cn.com.geovis;

public class UnSupportDataEncoder extends AbstractDataEncoder {

	public static final byte MESSAGE_TYPE = 127;
	
	@Override
	public byte messageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public boolean canHandle(String data) {
		return false;
	}

	@Override
	protected byte[] encode0(String data) {
		return ByteUtils.stringToByte(data);
	}

	@Override
	protected String decode0(byte[] data) {
		return ByteUtils.byteToString(data);
	}
	

}
