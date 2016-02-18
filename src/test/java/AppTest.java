/**
 * Created by apple on 2/18/16.
 * A class for test
 */
public class AppTest {
    public static void main(String[] args) {
        String s = "adf&adf";
        String[] sa = s.split("&");
        for (String a : sa) {
            System.out.println(a);
        }
    }
}
