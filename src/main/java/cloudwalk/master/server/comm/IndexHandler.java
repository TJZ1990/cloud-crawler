package cloudwalk.master.server.comm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * Created by 1333907 on 2/19/16.
 * Handler for index.html.
 */

public class IndexHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Request through IndexHandler");
        File file = new File("./src/main/static/index.html");
        if (!file.exists()) {
            LOGGER.error("File does not exist");
        }
        BufferedReader input = new BufferedReader(new FileReader(file));
        StringBuilder index = new StringBuilder();
        String string;
        while ((string = input.readLine()) != null) {
            index.append(string);
        }
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, index.toString().getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(index.toString().getBytes());
        out.flush();
        httpExchange.close();
    }
}
