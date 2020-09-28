package cn.com.geovis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * 数据压缩，
 * 
 * @author lixiaofei
 *
 */
public class Compress implements ICompress {

	@Override
	public byte[] compress(byte[] data) {

		Deflater deflater = new Deflater();
		deflater.setInput(data);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length)) {
			deflater.finish();
			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer); // returns the generated
														// code... index
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
			byte[] output = outputStream.toByteArray();
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[] {};

	}

	@Override
	public byte[] unCompress(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length)) {
			byte[] buffer = new byte[1024];
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
			byte[] output = outputStream.toByteArray();
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		}

		return new byte[] {};
	}

}
