package util;

public interface ConsoleSystem {
	/**
	 * System.out.println.
	 * @param msg
	 */
	void printMessage(String msg);
	/**
	 * Per-application action, parsed on string.
	 * @param action
	 */
	void actionMessage(String action);
}
