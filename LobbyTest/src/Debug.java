public class Debug {
	static boolean isDebug = true;

	public static void log(String message) {
		if (!isDebug) {
			return;
		}
		System.out.println(message);
	}
}