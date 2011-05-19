import java.io.*;
import java.net.*;

import Receiver.UDPListenerThread;

public class MulticastListenerThread extends UDPListenerThread{

	private MembershipManager manager;

    public MulticastListenerThread(int port, MembershipManager manager) throws IOException {
		super(port);
		this.manager = manager;
	}

	public void run() {
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // send it
                InetAddress group = InetAddress.getByName(manager.getMulitcastAddr());
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }


}
