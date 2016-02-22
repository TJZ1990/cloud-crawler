package cloudwalk.master.server.comm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * Created by 1333907 on 2/19/16.
 * Handler for static files, e.g., css, js
 */

public class StaticHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        LOGGER.info("Request through StaticHandler");
        String staticFile = httpExchange.getRequestURI().toString();
        LOGGER.info("Get request for " + staticFile);
        File file = new File("./src/main/" + staticFile);
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
