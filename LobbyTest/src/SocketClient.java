import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClient {
	Socket server;

	public void connect(String address, int port) {
		try {
			server = new Socket(address, port);
			Debug.log("Client connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		if (server == null) {
			return;
		}
		Debug.log("Client Started");
		// listen to console, server in, and write to server out
		try (Scanner si = new Scanner(System.in);
				ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());) {
			// Thread to listen for keyboard input so main thread isn't blocked
			Thread inputThread = new Thread() {
				@Override
				public void run() {
					try {
						while (!server.isClosed()) {
							Debug.log("Waiting for input");
							String line = si.nextLine();
							if (!"quit".equalsIgnoreCase(line) && line != null) {
								// grab line and write it to the stream
								out.writeObject(line);
							} else {
								Debug.log("Stopping input thread");
								// we're quitting so tell server we disconnected so it can broadcast
								out.writeObject("bye");
								break;
							}
						}
					} catch (Exception e) {
						Debug.log("Client shutdown");
					} finally {
						close();
					}
				}
			};
			inputThread.start();// start the thread

			// Thread to listen for responses from server so it doesn't block main thread
			Thread fromServerThread = new Thread() {
				@Override
				public void run() {
					try {
						String fromServer;
						// while we're connected, listen for strings from server
						while (!server.isClosed() && (fromServer = (String) in.readObject()) != null) {
							Debug.log(fromServer);
						}
						Debug.log("Stopping server listen thread");
					} catch (Exception e) {
						if (!server.isClosed()) {
							e.printStackTrace();
							Debug.log("Server closed connection");
						} else {
							Debug.log("Connection closed");
						}
					} finally {
						close();
					}
				}
			};
			fromServerThread.start();// start the thread

			// Keep main thread alive until the socket is closed
			// initialize/do everything before this line
			while (!server.isClosed()) {
				Thread.sleep(50);
			}
			Debug.log("Exited loop");
			System.exit(0);// force close
			// TODO implement cleaner closure when server stops
			// without this, it still waits for input before terminating
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private void close() {
		if (server != null) {
			try {
				server.close();
				Debug.log("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		SocketClient client = new SocketClient();
		client.connect("127.0.0.1", 3005);// uploaded server port via eclipse Run Configurations
		try {
			// if start is private, it's valid here since this main is part of the class
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
