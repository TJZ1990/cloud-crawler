package cloudwalk.slave.example;


/**
 * Created by apple on 2/18/16. A class for test
 */
public class App {
    public static void main(String[] args) throws Exception {
        if (args[0].equals("master")) {

        }
        else if(args[0].equals("slave")) {
            CrawlerTest.main(new String[]{args[1], args[2]});
        }
    }
}
