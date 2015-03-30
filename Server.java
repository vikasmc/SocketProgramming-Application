import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
	//To store the port number of the client.
	public static Map<String, Integer> getport = new HashMap<String, Integer>();
	//To check wheather the clients are free.
	public static Map<String, Boolean> isfree = new HashMap<String, Boolean>();
	//To store the Inetaddress of the client.
	public static Map<String, InetAddress> getinet = new HashMap<String, InetAddress>();
	//TO store the names of the Clients.
	public static ArrayList<String> names = new ArrayList<String>();
	ServerSocket serverSocket = null;
	public DatagramSocket socket = null;

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.serverSocket = new ServerSocket(444);
			server.socket = new DatagramSocket(4444);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	//this loops accepts any number of Clients and create new serverThread for each Client.
		while (true) {
			try {
				System.out.println("Waiting for client on port ");
				System.out.println(server.serverSocket.getInetAddress());
				Socket d = server.serverSocket.accept();
				System.out.println("Just connected to "
						+ d.getRemoteSocketAddress());
				System.out.println("the port numbe of Client is " + d.getPort());
				ServerThreads serverthread = new ServerThreads(d,server);
				serverthread.start();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}

