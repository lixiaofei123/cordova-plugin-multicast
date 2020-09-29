package cn.com.geovis;

public class GroupChatTextDataEncoder extends AbstractGroupChatDataEncoder {
	
	public static final byte MESSAGE_TYPE = 8;

	@Override
	protected byte messageType0() {
		return MESSAGE_TYPE;
	}

	@Override
	protected String dataProperty() {
		return "text";
	}

	@Override
	protected String subtype() {
		return "text";
	}
	

	public static void main(String[] args) {

		String msg = "{id:'dad44267-91b7-4c4d-a640-5cf5a48c0924',type:'groupchat',subType:'text',receiver:123,sender:456,sendTime:1601347381497,data:{text:'hello'}}";
		System.out.println("编码后的数据长度是" + ByteUtils.stringToByte(msg).length);

		DataEncoder data = new DataEncoder();
		byte[] datas = data.encode(msg);
		System.out.println("编码后的数据长度是" + datas.length);

		String smsg = data.decode(datas);
		System.out.println("数据还原:" + smsg);
	}



	
}
