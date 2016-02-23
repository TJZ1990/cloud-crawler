package cloudwalk.salve.test;

import cloudwalk.slave.crawler.comm.Communicator;

/**
 * Created by 1333907 on 2/18/16.
 * Test for slave register in master.
 */

public class RegisterTest {
    public static void main(String[] args) {
        Communicator.register("localhost", -1, "0");
    }
}
