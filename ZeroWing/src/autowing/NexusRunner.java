package autowing;

import java.sql.SQLException;

public class NexusRunner implements Runnable {
	Site[] sites;
	long delay;
	int max = -1;

	public NexusRunner(Site[] sites, long delay) {
		this.sites = sites;
		this.delay = delay;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void run() {
		int time = 0;
		// for(Site s: sites){
		// try {
		// s.as.startClient();
		// } catch (SQLException e) {
		// System.out.println("Error on "+s.toString());
		// e.printStackTrace();
		// }
		// }
		//		
		while (time != max) {
			try {
				iterate(time);
				time++;
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("NexusRunnable has failed");
				e.printStackTrace();
			}
		}
		if (time == max) {
			System.out.println("Nexus has successfully finished due to max time.");
		}
	}

	public void iterate(int time) throws InterruptedException {

		for (Site s : sites) {
			s.tickTimer(time);
		}

	}

	public void startClients() {
		for (Site s : sites) {
			try {
				s.as.startClient();
			} catch (SQLException e) {
				System.out.println("Error on " + s.toString());
				e.printStackTrace();
			}
		}
	}

}
