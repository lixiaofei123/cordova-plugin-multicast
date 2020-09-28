package cn.com.geovis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        map.put("4", "4");
        map.put("5", "5");
        map.put("6", "6");
        map.put("7", "7");
        
        map.forEachValue(5, new Consumer<String>() {

			@Override
			public void accept(String t) {
				int num = Integer.parseInt(t);
				if(num % 2 == 0){
					map.remove(t);
				}
			}
		});
        
        map.forEachValue(5, new Consumer<String>() {

			@Override
			public void accept(String t) {
				System.out.println(t);
			}
		});
    }
}
