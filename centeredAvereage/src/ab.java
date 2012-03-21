import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ab {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String s1;
		Process p = Runtime.getRuntime().exec("/home/rohit/Documents/a.out", null);
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getInputStream()));
		s1=stdError.readLine();
		System.out.println("Here is the standard op :\n"+s1);
		
	}

}
