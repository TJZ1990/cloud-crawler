package cloudwalk.master.server.comm;

import cloudwalk.master.server.comm.handler.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by 1333907 on 2/18/16.
 * Simple master HTTP server starts from command line instead of container.
 */

public final class SimpleMasterHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMasterHttpServer.class);
    private static final int MULTI = 1;

    HttpServerProvider httpServerProvider = null;
    InetSocketAddress inetSocketAddress = null;
    private int port = 8080;

    public SimpleMasterHttpServer() {
        this(8080);
    }

    public SimpleMasterHttpServer(int port) {
        this.port = port;
    }

    public SimpleMasterHttpServer init() {
        httpServerProvider = HttpServerProvider.provider();
        inetSocketAddress = new InetSocketAddress(port);
        return this;
    }

    public SimpleMasterHttpServer start() {
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

        /**
         * Handlers.
         */
        // Handler for index.html
        httpServer.createContext("/index", new IndexHandler());
        // Handler for static files, e.g., css, js
        httpServer.createContext("/static", new StaticHandler());
        // Handler for message containing uri
        httpServer.createContext("/message", new MessageHandler());
        // Handler for slave register
        httpServer.createContext("/register", new RegisterHandler());
        // Handler for slave.json
        httpServer.createContext("/slave.json", new SlaveHandler());
        // Handler for slaves.json
        httpServer.createContext("/slaves.json", new SlaveHandler());
        // Handler for number.json
        httpServer.createContext("/number.json", new SlaveHandler());

        httpServer.setExecutor(null);
        httpServer.start();
        LOGGER.info("Simple HTTP server started");
        return this;
    }
}
