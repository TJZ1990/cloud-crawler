package cloudwalk.slave.crawler;

import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;

public class Dupefilter {
    Jedis server;
    String dupefilterKey;
    
    public Dupefilter(Jedis server, String dupefilterKey) {
        this.server = server;
        this.dupefilterKey = dupefilterKey;
    }
    
    public boolean seenURL(WebURL url) {
        String canonicalUrl = URLCanonicalizer.getCanonicalURL(url.getURL());
        Long added = server.sadd(dupefilterKey, canonicalUrl);
        return added == 0;
    }
    
    public void clear() {
        server.del(dupefilterKey);
    }
}