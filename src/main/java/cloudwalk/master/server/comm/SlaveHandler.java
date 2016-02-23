package cloudwalk.master.server.comm;

import cloudwalk.master.server.comm.util.StringFileReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by 1333907 on 2/23/16.
 * Handler about slaves.
 */
public class SlaveHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String info = httpExchange.getRequestURI().toString();
        String filePath = "../cloud-crawler-front-end/server/json" + info;
        String index = StringFileReader.read(filePath);
        if ("/number.json".equals(info)) {
            handleNumber(httpExchange, index);
        } else {
            handleSlave(httpExchange, index);
        }
        httpExchange.close();
    }

    public void handleSlave(HttpExchange httpExchange, String index) throws IOException {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, index.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(index.getBytes());
        out.flush();
    }

    public void handleNumber(HttpExchange httpExchange, String index) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        httpExchange.getResponseHeaders().set("Cache-Control", "no-cache");
        httpExchange.getResponseHeaders().set("Connection", "keep-alive");
        index = "data:" + index.replaceAll("\n", "") + "\n\n";
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, index.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        while (true) {
            out.write(index.getBytes());
            out.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
