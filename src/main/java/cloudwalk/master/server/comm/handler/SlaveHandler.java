package cloudwalk.master.server.comm.handler;

import cloudwalk.master.server.SlaveManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sf.json.JSONObject;

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
        if ("/slaves.json".equals(info)) {
            handleSlaves(httpExchange);
        }
        if ("/number.json".equals(info)) {
            handleNumber(httpExchange);
        }
        httpExchange.close();
    }

    public void handleSlaves(HttpExchange httpExchange) throws IOException {
        JSONObject slaves = SlaveManager.getSlaves();
        String slavesString = slaves.toString();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, slavesString.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(slavesString.getBytes());
        out.flush();
    }

    public void handleNumber(HttpExchange httpExchange) throws IOException {
        JSONObject number = SlaveManager.getNumber();
        String numberString = number.toString();
        httpExchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        httpExchange.getResponseHeaders().set("Cache-Control", "no-cache");
        httpExchange.getResponseHeaders().set("Connection", "keep-alive");
        numberString = "data:" + numberString.replaceAll("\n", "") + "\n\n";
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, numberString.getBytes().length);
        OutputStream out = httpExchange.getResponseBody();
        while (true) {
            out.write(numberString.getBytes());
            out.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
