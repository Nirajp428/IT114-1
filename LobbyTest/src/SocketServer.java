import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {
	int port = 3005;
	public static boolean isRunning = true;
	// private List<ServerThread> clients = new ArrayList<ServerThread>();
	private List<Room> rooms = new ArrayList<Room>();
	private Room lobby;// here for convenience

	private void start(int port) {
		this.port = port;
		Debug.log("Waiting for client");
		try (ServerSocket serverSocket = new ServerSocket(port);) {
			lobby = new Room("Lobby");
			rooms.add(lobby);
			while (SocketServer.isRunning) {
				try {
					Socket client = serverSocket.accept();
					Debug.log("Client connecting...");
					// Server thread is the server's representation of the client
					ServerThread thread = new ServerThread(client, this);
					thread.start();
					// add client thread to list of clients
					// clients.add(thread);
					lobby.addClient(thread);
					Debug.log("Client added to clients pool");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isRunning = false;
				Thread.sleep(50);
				Debug.log("closing server socket");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Broadcast given message to everyone connected
	public synchronized void broadcast(String message, long id) {
		/*
		 * 09/14/2020: even though this version uses a "rooms" concept we still want the
		 * server to be able to preprocess messages and route them to the appropriate
		 * Room, that's why this function still exists
		 */

		// let's temporarily use the thread id as the client identifier to
		// show in all client's chat. This isn't good practice since it's subject to
		// change
		// as clients connect/disconnect
		// int from = getClientIndexByThreadId(id);
		message = String.format("User[%d]: %s", id, message);
		// end temp identifier
		lobby.sendMessage(message);
	}

	public static void main(String[] args) {
		// let's allow port to be passed as a command line arg
		// in eclipse you can set this via "Run Configurations"
		// -> "Arguments" -> type the port in the text box -> Apply
		int port = 3005;// make some default
		if (args.length >= 1) {
			String arg = args[0];
			try {
				port = Integer.parseInt(arg);
			} catch (Exception e) {
				// ignore this, we know it was a parsing issue
			}
		}
		Debug.log("Starting Server");
		SocketServer server = new SocketServer();
		Debug.log("Listening on port " + port);
		server.start(port);
		Debug.log("Server Stopped");
	}
}