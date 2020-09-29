package cn.com.geovis;

public abstract class AbstractSingleChatDataEncoder extends AbstractChatDataEncoder {

	@Override
	protected String type() {
		return "chat";
	}

}
