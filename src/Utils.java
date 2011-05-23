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
	
	public static String boxify(Packet.Says packet){
        String[] lines = packet.message.split("\\\\n");
        int width = packet.nickname.length() + 6;

        for(String line : lines)
            if(line.length() > width)
                width = line.length();

        String bar = repeat("*", width + 4);
        String rtn = bar + "\\n";
        rtn += "* From: " + packet.nickname + repeat(" ", width - packet.nickname.length() - 6) + " *\\n";

        for(String line : lines)
            rtn += "* " + line + repeat(" ", width - line.length()) + " *\\n";

        return rtn + bar;
    }
	
	public static String repeat(String str, int count){
        String rtn = "";
        for(int i = 0; i < count; i++)
            rtn += str;
        return rtn;
    }

}
