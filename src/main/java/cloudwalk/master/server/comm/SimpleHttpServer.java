package cloudwalk.master.server.comm;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by 1333907 on 2/18/16.
 * Master HTTP server starts from command line instead of container.
 */
public final class SimpleHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServer.class);
    private static final int MULTI = 1;

    HttpServerProvider httpServerProvider = null;
    InetSocketAddress inetSocketAddress = null;
    private int port = 8080;

    public SimpleHttpServer() {
        this(8080);
    }

    public SimpleHttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new SimpleHttpServer().init().start();
    }

    public SimpleHttpServer init() {
        httpServerProvider = HttpServerProvider.provider();
        inetSocketAddress = new InetSocketAddress(port);
        return this;
    }

    public SimpleHttpServer start() {
        HttpServer httpServer = null;
        try {
            httpServer = httpServerProvider.createHttpServer(inetSocketAddress, MULTI);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (httpServer == null) {
            LOGGER.error("Cannot start simple HTTP server");
            return null;
        }
        httpServer.createContext("/post/", new SimpleHttpServerHandler());
        httpServer.setExecutor(null);
        httpServer.start();
        LOGGER.info("Simple HTTP server started");
        return this;
    }
}
