package cloudwalk.slave.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;

public class Queue {
    Jedis server;
    String queueKey;

    public Queue(Jedis server, String queueKey) {
        this.server = server;
        this.queueKey = queueKey;
    }

    public void push(WebURL url) {
        server.lpush(queueKey.getBytes(), serialize(url));
    }

    public WebURL pop() {
        return unserialize(server.rpop(queueKey.getBytes()));

    }

    public long getLength() {
        return server.llen(queueKey);
    }

    public void clear() {
        server.del(queueKey);
    }

    public byte[] serialize(WebURL url) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

    public WebURL unserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (WebURL) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
