import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HelloUi extends Frame implements ActionListener, KeyListener {
	/**
	 * @Vias M C
	 */
	private static final long serialVersionUID = 1L;
	Label meslabel;
	TextField mesg;
	Button send, login, setuser, wait;
	TextArea ta, usrlist;
	Panel top, mid, bot;
	static String out, name, to;
	Client r;

	public HelloUi(String title) {
		super(title);
		initComponents();
		setLayout(new BorderLayout());
		top = new Panel();
		top.setLayout(new GridLayout(1, 2));
		bot = new Panel();
		bot.setLayout(new GridLayout(1, 3, 5, 5));
		setSize(500, 500);
		setBackground(Color.gray);
		setForeground(Color.black);
		meslabel = new Label("Message", Label.RIGHT);
		mesg = new TextField(20);
		ta = new TextArea("", 20, 20, TextArea.SCROLLBARS_BOTH);
		usrlist = new TextArea("user list", 20, 20, TextArea.SCROLLBARS_BOTH);
		send = new Button("Send");
		login = new Button("Login");
		setuser = new Button("SetUser");
		wait = new Button("Wait");
		send.setEnabled(false);
		setuser.setEnabled(false);
		wait.setEnabled(false);
		top.add(meslabel);
		top.add(mesg);
		bot.add(login);
		bot.add(send);
		bot.add(setuser);
		bot.add(wait);
		add(top, BorderLayout.NORTH);
		add(bot, BorderLayout.SOUTH);
		add(ta, BorderLayout.CENTER);
		add(usrlist, BorderLayout.EAST);
		send.addActionListener(this);
		mesg.addKeyListener(this);
		login.addActionListener(this);
		setuser.addActionListener(this);
		wait.addActionListener(this);
		r = new Client();
	}

	public void actionPerformed(ActionEvent ae) {
		Button btn = (Button) ae.getSource();
		if (btn == login) {
			loginDialog ld = new loginDialog(this);
			ld.show();
			name = ld.user;
			String output = r.SendName(name);
			if (output.equals("success")) {
				ta.append("\nThe user " + name + " has been added ");
				login.setEnabled(false);
				wait.setEnabled(true);
				String list = r.ListUsers();
				String namepass[] = list.split(" ");
				for (int i = 0; i < namepass.length; i++) {
					usrlist.append("\n" + namepass[i]);
				}
			} else {
				ta.append("\n" + output);
			}
		}
		if (btn == wait) {
			WaitsDialog Id = new WaitsDialog(this);
			Id.show();
			out = Id.user;
			String output=r.WaitOrChat(out);
			if(output.equals("sucess")){
			if (out.equals("wait")) {
				wait.setEnabled(false);
				new MessageReader().start();
			} else if (out.equals("chat")) {
				setuser.setEnabled(true);
				wait.setEnabled(false);
				new MessageReader().start();
			} else if (out.equals("quit")) {
				r.SendNameToDelete(name);
				dispose();
			}
			}
		}
		if (btn == send) {
			String txt = mesg.getText();
			mesg.setText("");
			String output = r.SendMessage(to, name, txt);
			if (output.equals("Message sent from " + name + " to " + to)) {
				ta.append("\n" + txt);
			} else if (output.equals("the user " + to + " is not online")) {
				ta.append("\n" + txt);
				send.setEnabled(false);
				setuser.setEnabled(true);
			} else {
				ta.append("\n" + output);
				send.setEnabled(false);
				setuser.setEnabled(false);
				wait.setEnabled(true);
			}
		}
		if (btn == setuser) {
			GetDialog Id = new GetDialog(this);
			Id.show();
			to = Id.user;
			String output = r.GetToName(to);
			if (output.equals("")) {
				ta.append("\nEnter the message you want to send to " + to);
				send.setEnabled(true);
				setuser.setEnabled(false);
			} else {
				ta.append("\n" + output);
			}
		}
	}

	public void keyPressed(KeyEvent ke) {
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {

	}

	public void initComponents() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});

	}

	class MessageReader extends Thread {
		String msg;

		public void run() {
			boolean j = true;
			do {
				String output = r.GetMessage();
				if (output != null) {
					setuser.setEnabled(false);
					wait.setEnabled(false);
					send.setEnabled(true);
					String namepass[] = output.split(":");
					to = namepass[0];
					String mess = namepass[1];
					if (mess.equals("quit")) {
						wait.setEnabled(true);
						send.setEnabled(false);
						j = false;
						ta.append("the "+to+"has been logged off");
					}
					else{
					ta.append("\n" + output);
					}
				}
			} while (j);
		}
	}

	public static void main(String s[]) {
		HelloUi mcc = new HelloUi("Client");
		mcc.setVisible(true);
		mcc.show();
		String output = mcc.r.connect();
		mcc.ta.append(output);
	}

}

class loginDialog extends Dialog implements ActionListener {
	Label loginname;
	TextField logintext;
	Button okay;
	Panel top, central;
	String user;

	loginDialog(Frame parent) {
		super(parent, "User Login", true);
		setSize(400, 100);
		setResizable(false);
		setFont(new Font("ComicSans", Font.BOLD | Font.ITALIC, 15));
		loginname = new Label("Enter Name:", Label.RIGHT);
		logintext = new TextField(15);
		okay = new Button("LOGIN");
		okay.setBounds(10, 10, 20, 20);
		top = new Panel();
		central = new Panel();
		central.setLayout(new FlowLayout());
		top.setLayout(new GridLayout(1, 2, 0, 0));
		top.add(loginname);
		top.add(logintext);
		central.add(okay);
		add(central, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		okay.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		user = logintext.getText();
		dispose();
	}
}

class GetDialog extends Dialog implements ActionListener {
	Label loginname;
	TextField logintext;
	Button okay;
	Panel top, central;
	String user;

	GetDialog(Frame parent) {
		super(parent, "Enter the User you want to chat", true);
		setSize(400, 100);
		setResizable(false);
		setFont(new Font("ComicSans", Font.BOLD | Font.ITALIC, 15));
		loginname = new Label("Enter Name:", Label.RIGHT);
		logintext = new TextField(15);
		okay = new Button("Enter");
		okay.setBounds(10, 10, 20, 20);
		top = new Panel();
		central = new Panel();
		central.setLayout(new FlowLayout());
		top.setLayout(new GridLayout(1, 2, 0, 0));
		top.add(loginname);
		top.add(logintext);
		central.add(okay);
		add(central, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		okay.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		user = logintext.getText();
		dispose();
	}
}

class WaitsDialog extends Dialog implements ActionListener {
	Label loginname;
	TextField logintext;
	Button okay;
	Panel top, central;
	String user;

	WaitsDialog(Frame parent) {
		super(parent, "User Login", true);
		setSize(400, 100);
		setResizable(false);
		setFont(new Font("ComicSans", Font.BOLD | Font.ITALIC, 15));
		loginname = new Label("Enter wait/chat/quit", Label.RIGHT);
		logintext = new TextField(15);
		okay = new Button("SUBMIT");
		okay.setBounds(10, 10, 20, 20);
		top = new Panel();
		central = new Panel();
		central.setLayout(new FlowLayout());
		top.setLayout(new GridLayout(1, 2, 0, 0));
		top.add(loginname);
		top.add(logintext);
		central.add(okay);
		add(central, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		okay.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		user = logintext.getText();
		dispose();
	}
}
