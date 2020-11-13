public class Test {
    public static String recur(String m) {
	if (m != null && m.length() > 0) {
	    System.out.println(m);
	    m = m.substring(0, m.length() - 1);
	    return recur(m);
	}
	return "";
    }

    public static void main(String[] args) {
	System.out.println(recur("win"));
    }
}
