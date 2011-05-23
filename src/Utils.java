import java.io.UnsupportedEncodingException;


public class Utils {
	private static final String CHARSET = "US-ASCII";
	
	public static String byteArrayToString(byte[] arr){
		try {
			return new String(arr, CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
