package cloudwalk.slave.crawler;

import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

public class Dupefilter {
    private static final Logger logger = LoggerFactory
            .getLogger(Dupefilter.class);

    Jedis server;
    String dupefilterKey;

    public Dupefilter(Jedis server, String dupefilterKey) {
        this.server = server;
        this.dupefilterKey = dupefilterKey;
    }

    public boolean seenURL(WebURL url) {
        if (url.getURL() == null)
            return true;
        String canonicalUrl = URLCanonicalizer.getCanonicalURL(url.getURL());
        if (canonicalUrl == null)
            return true;
        Long added = server.sadd(dupefilterKey, canonicalUrl);
        if (added == 0) {
            logger.trace("{} is seen before", canonicalUrl);
        }
        return added == 0;
    }

    public void clear() {
        server.del(dupefilterKey);
    }
}