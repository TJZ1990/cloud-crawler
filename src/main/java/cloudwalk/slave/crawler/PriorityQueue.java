package cloudwalk.slave.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class PriorityQueue {
    Jedis server;
    String queueKey;

    public PriorityQueue(Jedis server, String queueKey) {
        this.server = server;
        this.queueKey = queueKey;
    }

    public void push(WebURL url) {
        // URL priority is not used now.
        short score = url.getDepth();
        server.zadd(queueKey.getBytes(), score, serialize(url));
    }

    @SuppressWarnings("unchecked")
    public WebURL pop() {
        Transaction tx = server.multi();
        tx.zrange(queueKey.getBytes(), 0, 0);
        tx.zremrangeByRank(queueKey, 0, 0);
        Set<byte[]> result = (Set<byte[]>) tx.exec().get(0);
        if (result.size() == 0)
            return null;
        return unserialize(result.iterator().next());
    }

    public long getLength() {
        return server.zcard(queueKey);
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
