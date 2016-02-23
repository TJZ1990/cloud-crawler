package cloudwalk.slave.crawler;

import redis.clients.jedis.Jedis;

public class Counter {
    private Jedis server;
    private String counterKey;

    public Counter(Jedis server, String counterKey) {
        this.server = server;
        this.counterKey = counterKey;
    }

    public void incr() {
        server.incr(counterKey);
    }

    public long getNumber() {
        String result = server.get(counterKey);
        if (result == null) {
            return 0;
        }
        return Long.parseLong(result);
    }

    public void clear() {
        server.set(counterKey, "0");
    }
}
