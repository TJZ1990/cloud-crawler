package cloudwalk.master.server.comm;

import cloudwalk.master.server.comm.util.StringFileReader;
import com.sun.net.httpserver.Headers;
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
        if (staticFile.startsWith("/static")) {
            staticFile = staticFile.substring(7);
        }
        LOGGER.info("Get request for " + staticFile);
        String filePath = "../cloud-crawler-front-end/" + staticFile;
        String index = StringFileReader.read(filePath);
        if (staticFile.endsWith(".css")) {
            httpExchange.getResponseHeaders().set("Content-Type", "text/css");
        }
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, index.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(index.getBytes());
        out.flush();
        httpExchange.close();
    }
}
