package cloudwalk.slave.crawler.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 1333907 on 2/18/16.
 * A class to communicate with master server.
 */
public final class Communicator {
    public static final Logger LOGGER = LoggerFactory.getLogger(Communicator.class);

    private static String protocol = "http";
    private static String address = "localhost";
    private static int port = 8080;
    private static String suffix = "post";

    /**
     * In future, static {} will read a configuration file to get protocol, address, port and suffix.
     */
    static {
        protocol = "http";
        address = "localhost";
        port = 8080;
        suffix = "post";
    }

    public static void register(String slaveHostname, String slaveIp, int slavePort, String slaveName) {
        String destination = protocol + "://" + address + ":" + port + "/register/";
        String param = "hostname=" + slaveHostname + "&ip=" + slaveIp + "&port=" + slavePort + "&name=" + slaveName;
        sendPost(destination, param);
    }

    public static void sendMessage(String slaveHostname, String slaveIp, int slavePort, String slaveName, long number) {
        String destination = protocol + "://" + address + ":" + port + "/message/";
        String param = "hostname=" + slaveHostname + "&ip=" + slaveIp + "&port=" + slavePort + "&name=" + slaveName + "&number=" + number;
        sendPost(destination, param);
    }

    private static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // Post request needs following two lines
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // Get output stream
            out = new PrintWriter(conn.getOutputStream());
            // Send param
            out.print(param);
            out.flush();
            // Get Response
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
