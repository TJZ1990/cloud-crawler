package cloudwalk.startup;

import cloudwalk.master.server.comm.SimpleMasterHttpServer;

/**
 * Created by 1333907 on 2/25/16.
 * Startup for master.
 */
public final class MasterStartup {
    public static void main(String[] args) {
        new MasterStartup().startMaster();
    }

    public void startMaster() {
        new SimpleMasterHttpServer().init().start();
    }
}
