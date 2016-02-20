package edu.zju.test.dummy;

import redis.clients.jedis.*;

/**
 * This is a dummy project to test shell script
 * @author Ryan Z
 */
public class App {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.auth("cloud-03");

        if (args[0].equals("master")) {
            jedis.set("status", "master-service");
        } else if (args[0].equals("slave")) {
            jedis.set("status", "slave-service");
        }
        System.out.println("Current node service: " + jedis.get("status") + ".");
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                System.out.println("Argument " + i + " = " + args[i]);
            }
        }

        jedis.close();
    }
}
