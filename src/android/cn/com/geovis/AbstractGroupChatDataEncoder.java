package cn.com.geovis;

public abstract class AbstractGroupChatDataEncoder extends AbstractChatDataEncoder {

	@Override
	protected String type() {
		return "groupchat";
	}

}
