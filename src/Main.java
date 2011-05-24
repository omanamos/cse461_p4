import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {

	public static void main(String[] args) throws IOException{
		String addr = args[0];
		int port = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(InetAddress.getByName(addr));

		MembershipManager m = new MembershipManager(socket, addr, port, nickname);
		Sender sender = new Sender(m, socket, nickname);
		new Receiver(m, socket, sender);
	}
}
