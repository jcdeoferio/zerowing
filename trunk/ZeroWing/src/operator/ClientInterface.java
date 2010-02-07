package operator;

import java.sql.SQLException;

public interface ClientInterface {

	public abstract void startClient() throws SQLException;

	public abstract void refreshPeerList();

	public abstract void addPeer(String ip, int port);

	public abstract void sync();

	public abstract void connectToTracker();

}