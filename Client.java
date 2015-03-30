import java.net.*;
import java.io.*;

public class Client {

	DataInputStream in = null;
	DataOutputStream out = null;
	static Socket Client = null;
	String serverName = "localhost";
	final int port = 444;
	final int portnumber = 4444;
	DatagramSocket socket = null;
	InetAddress inetaddress = null;

	//To coonect to the Server
	public String connect() {
		try {
			System.out.println("Connecting to " + serverName + " on port "
					+ port);
			Client = new Socket(serverName, port);
			System.out.println("Just connected to "
					+ Client.getRemoteSocketAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Connected to " + Client.getRemoteSocketAddress();
	}

	//To register the name of the client
	public String SendName(String name) {
		String output = null;
		try {
			in = new DataInputStream(Client.getInputStream());
			out = new DataOutputStream(Client.getOutputStream());
			out.writeUTF(name);
			output = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	// To delete the Client from the chat Server.
	public void SendNameToDelete(String name) {
		try {
			in = new DataInputStream(Client.getInputStream());
			out = new DataOutputStream(Client.getOutputStream());
			out.writeUTF(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//To get The list of the users who are online.
	public String ListUsers() {
		String output = null;
		try {
			in = new DataInputStream(Client.getInputStream());
			output = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	//To check wheather the Client is available or not.
	public String GetToName(String name) {
		String output = null;
		try {
			in = new DataInputStream(Client.getInputStream());
			out = new DataOutputStream(Client.getOutputStream());
			out.writeUTF(name);
			output = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	//To send message to the Client.
	public String SendMessage(String to, String from, String message) {
		String messagetosend = to + ":" + from + ":" + message;
		String output = null;
		try {
			in = new DataInputStream(Client.getInputStream());
			out = new DataOutputStream(Client.getOutputStream());
			out.writeUTF(messagetosend);
			output = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	//To know wheather the Client want to wait for other to talk or he wants to talk to other.
	public String WaitOrChat(String name) {

		String output = null;
		byte[] outbuf = name.getBytes();
		int length = outbuf.length;
		try {
			try {
				in = new DataInputStream(Client.getInputStream());
				out = new DataOutputStream(Client.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			inetaddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		DatagramPacket request = new DatagramPacket(outbuf, length,
				inetaddress, portnumber);
		try {
			socket.send(request);
			output=in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;

	}
	
	//To get the message.
	public String GetMessage() {
		String output = null;
		byte[] inbuf = new byte[256];
		DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);
		try {
			socket.receive(packet);
			output = new String(packet.getData(), 0, packet.getLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
