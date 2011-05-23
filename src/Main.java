import java.io.IOException;
import java.net.MulticastSocket;

public class Main {

	public static void main(String[] args) throws IOException{
		String multicastAddr = args[0];
		int port = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		MulticastSocket socket = new MulticastSocket(port);
		
		System.err.println("allocated Port!");
		MembershipManager m = new MembershipManager(multicastAddr, socket, nickname);
		System.err.println("allocated Manager!");
		Sender sender = new Sender(m, socket, nickname);
		System.err.println("allocated Sender!");
		Receiver receiver = new Receiver(m, socket, sender);
		System.err.println("allocated Receiver!");
	}
}
