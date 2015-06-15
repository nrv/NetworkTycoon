package name.herve.networktycoon.text;


public class TextInterface {

	public static void err(String m) {
		System.err.println(m);
		System.err.flush();
	}

	public static void out(String m) {
		System.out.println(m);
		System.out.flush();
	}
}
