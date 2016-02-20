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
        
        if (args[0].compareTo("master") == 0) {
            jedis.set("status", "master-service");
        } else if (args[0].compareTo("slave") == 0) {
            jedis.set("status", "slave-service");
        }
        System.out.println("Current node service: " + jedis.get("status") + ".");  
        
        jedis.close();
    }
}
