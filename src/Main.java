import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {

	public static void main(String[] args) throws IOException{
		String multicastAddr = args[0];
		int port = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(InetAddress.getByName(multicastAddr));

		MembershipManager m = new MembershipManager(multicastAddr, socket, nickname);
		Sender sender = new Sender(m, socket, nickname);
		Receiver receiver = new Receiver(m, socket, sender);
	}
}
