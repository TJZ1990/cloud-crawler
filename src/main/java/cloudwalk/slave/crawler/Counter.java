package cloudwalk.slave.crawler;

import redis.clients.jedis.Jedis;

public class Counter {
    private final String counterKey = "scheduledPages";
    private Jedis server;

    public Counter(Jedis server) {
        this.server = server;
    }
    
    public void incr() {
        server.incr(counterKey);
    }
    
    public long getNumber() {
        String result = server.get(counterKey);
        if(result == null){
            return 0;
        }
        return Long.parseLong(result);
    }
    
    public void clear() {
        server.set(counterKey, "0");
    }
}
