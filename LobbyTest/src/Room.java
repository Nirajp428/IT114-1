import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Room {
	public Room(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	private List<ServerThread> clients = new ArrayList<ServerThread>();

	public void addClient(ServerThread client) {
		// TODO check if they exist already
		clients.add(client);
	}

	public void sendMessage(String message) {
		Debug.log(getName() + ": Sending message to " + clients.size() + " clients");
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread client = iter.next();
			boolean messageSent = client.send(message);
			if (!messageSent) {
				// if we got false, due to update of send()
				// we can assume the client lost connection
				// so let's clean it up
				iter.remove();
				Debug.log("Removed client " + client.getId());
			}
		}
	}
}