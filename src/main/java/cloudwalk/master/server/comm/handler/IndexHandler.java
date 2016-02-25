package cloudwalk.master.server.comm.handler;

import cloudwalk.master.server.comm.util.StringFileReader;
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
        String filePath = "../cloud-crawler-front-end/index.html";
        String index = StringFileReader.read(filePath);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, index.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(index.getBytes());
        out.flush();
        httpExchange.close();
    }
}
