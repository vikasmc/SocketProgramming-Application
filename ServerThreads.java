import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

class ServerThreads extends Thread {

	Socket client = null;
	Server newserver;
	boolean nameisok = true;
	boolean nameisfree=true;
	boolean logoff ;
	boolean quits = false;
	DataInputStream in = null;
	DataOutputStream out = null;
	String ClientName = null;
	String names = "";
	InetAddress address = null;
	DatagramPacket packet = null;
	DatagramSocket dsocket = null;
	String to;
	String from;
	String message;

	public ServerThreads(Socket socket, Server server) {
		newserver = server;
		client = socket;
	}

	public void run() {
		Start();
		String output;
		do {
			output = WaitOrChat();
			if (output.equals("chat")) {
				Verify();
			}
			else if (output.equals("quit")) {
				quits = true;
				DeleteName();
				break;
			}
			do {
				logoff = RecieveMessage();
			} while (logoff);
		} while (!quits);
	}


	//To register the name of the Client.
public void Start() {
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			do {
				String name = in.readUTF();
				if (Add(name)) {
					ClientName = name;
					out.writeUTF("success");
					nameisok = false;
					System.out.println("the user "+name+" has been added");
				} else {
					out.writeUTF("The name is already taken. Soryy!!!");
					nameisok=true;
				}
			} while (nameisok);

			for (String entry : newserver.names) {
				names += entry + " ";
			}
			out.writeUTF(names);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//To delete the name of hte Client from the ChatServer.
		public void DeleteName() {
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			String n = in.readUTF();
			System.out.println("the user "+n+" has been removed");
			newserver.names.remove(n);
			newserver.getport.remove(n);
			newserver.getinet.remove(n);
			newserver.isfree.remove(n);
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//To check the Client is available or not.
	public void Verify() {
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			do {
				String name = in.readUTF();
				String output = IsFree(name);
				if (output.equals("success")) {
					out.writeUTF("");
					nameisfree = false;
				} else {
					out.writeUTF(output);
				}
			} while (nameisfree);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//operation of adding the name to the arrylist.
	public boolean Add(String n) {
		if (!newserver.names.contains(n)) {
			newserver.names.add(n);
			newserver.isfree.put(n, true);
			return true;
		} else {
			return false;
		}
	}

	//To send and recieve message from the Client
	public boolean RecieveMessage() {
		boolean log=true;
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			String output = in.readUTF();
			String namepass[] = output.split(":");
			to = namepass[0];
			from = namepass[1];
			message = namepass[2];
			newserver.isfree.put(from, false);
			newserver.isfree.put(to, false);
			String data = from + ":" + message;
			if (message.equals("quit")) {
				removefromchat(from);
				removefromchat(to);
				out.writeUTF("you have been logged off");
				System.out.println("the user "+to+" has been removed from the chat room");
				System.out.println("the user "+from+" has been removed from the chat room");
				data = from + ":quit";
				byte[] outbuf = data.getBytes();
				int len = outbuf.length;
				DatagramPacket request = new DatagramPacket(outbuf, len,
						newserver.getinet.get(to), newserver.getport.get(to));
				newserver.socket.send(request);
				log = false;
			} else {
				System.out.println(output);
				out.writeUTF("Message sent from " + from + " to " + to);
				byte[] outbuf = data.getBytes();
				int len = outbuf.length;
				DatagramPacket request = new DatagramPacket(outbuf, len,
						newserver.getinet.get(to), newserver.getport.get(to));
				newserver.socket.send(request);
				log = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return log;
	}

	//to check wheather the Client wants to chat or wait.
	public String WaitOrChat() {
		String data = null;
		try {
			out = new DataOutputStream(client.getOutputStream());
			byte[] inbuf = new byte[256];
			DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);
			newserver.socket.receive(packet);
			newserver.getport.put(ClientName, packet.getPort());
			newserver.getinet.put(ClientName, packet.getAddress());
			data = new String(packet.getData(), 0, packet.getLength());
			out.writeUTF("sucess");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	// operation to check wheather the Client is free or not.
	public String IsFree(String name) {
		if (newserver.isfree.containsKey(name)) {
			if (newserver.isfree.get(name)) {
				newserver.isfree.put(name, false);
				return "success";
			} else {
				return "user is busy";
			}
		} else {
			return "the user name does not exist";
		}
	}
	
	//to remove the Client from one chat and let him talk to another Client.
	public void removefromchat(String name){
		if(!newserver.isfree.get(name)){
		newserver.isfree.put(name, true);
		System.out.println("the user "+name+" is free now");
		}
	}
}
