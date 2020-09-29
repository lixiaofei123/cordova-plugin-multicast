package cn.com.geovis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DataEncoder implements IDataEncoder {
	
	private static final List<IDataEncoder> encoders = new ArrayList<IDataEncoder>();
	
	private final static ICompress compress = new Compress();
	
	private UnSupportDataEncoder unSupportDataEncoder;

	
	public DataEncoder() {
		
		encoders.add(new HeartbeatDataEncoder());
		
		encoders.add(new SingleChatTextDataEncoder());
		encoders.add(new SingleChatImageDataEncoder());
		encoders.add(new SingleChatAudioDataEncoder());
		encoders.add(new SingleChatVideoDataEncoder());
		
		encoders.add(new GroupChatTextDataEncoder());
		encoders.add(new GroupChatImageDataEncoder());
		encoders.add(new GroupChatAudioDataEncoder());
		encoders.add(new GroupChatVideoDataEncoder());
		
		
		encoders.add(new GroupAddDataEncoder());
		encoders.add(new GroupDeleteDataEncoder());
		
		
		encoders.add(new TaskSyncDataEncoder());
		encoders.add(new TaskTimeSyncDataEncoder());
		encoders.add(new TaskPersonSyncDataEncoder());
		encoders.add(new TaskTeamSyncDataEncoder());
		
		encoders.add(new GeoMarkerAddDataEncoder());
		encoders.add(new GeoMarkerDeleteDataEncoder());
		
		encoders.add(new PlotSyncDataEncoder());
		encoders.add(new PlotAddDataEncoder());
		encoders.add(new PlotDeleteDataEncoder());
		
		encoders.add(new DroneSyncDataEncoder());
		encoders.add(new DroneRequireChangeDataEncoder());
		encoders.add(new DroneAgreeChangeMasterDataEncoder());
		encoders.add(new DroneDisagreeChangeMasterDataEncoder());
	
		unSupportDataEncoder = new UnSupportDataEncoder();
		encoders.add(unSupportDataEncoder);
	}
	

	@Override
	public byte[] encode(String data) {
		byte[] encodeData = null;
		for(IDataEncoder encoder : encoders){
			try{
				if(encoder.canHandle(data)){
					encodeData = encoder.encode(data);
					break;
				}
			}catch(Exception e){
				// nothing to do
			}
		}
		
		System.out.println("未发现有效编码程序!!!!");
		// 不支持的话就直接转换为byte数组
		if(encodeData == null){
			encodeData = unSupportDataEncoder.encode(data);
		}
		
		return compress.compress(encodeData);
	}

	@Override
	public String decode(byte[] data) {
		byte[] unCompressData = compress.unCompress(data);
		for(IDataEncoder encoder : encoders){
			if(unCompressData[16] == encoder.messageType()){
				return encoder.decode(unCompressData);
			}
		}
		return "";
	}

	@Override
	public byte messageType() {
		return 0;
	}

	@Override
	public boolean canHandle(String data) {
		return false;
	}
	

}
