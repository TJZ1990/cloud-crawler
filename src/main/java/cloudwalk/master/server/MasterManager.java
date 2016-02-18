package cloudwalk.master.server;

import cloudwalk.master.server.comm.SimpleHttpServer;

/**
 * Created by apple on 2/18/16.
 * Manager to manage master server
 */
public class MasterManager {
    public static void main(String[] args) {
        new MasterManager().startHttpServer();
    }

    public void startHttpServer() {
        new SimpleHttpServer().init().start();
    }
}
