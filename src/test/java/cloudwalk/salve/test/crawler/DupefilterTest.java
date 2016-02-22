package cloudwalk.salve.test.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import cloudwalk.slave.crawler.Dupefilter;
import edu.uci.ics.crawler4j.url.WebURL;

public class DupefilterTest {
    private Jedis server;
    private Dupefilter dupe;
    private String dupeKey = "dupe_test";
    
    @Before
    public void setUp() throws Exception {
        server = new Jedis("localhost");
        dupe = new Dupefilter(server, dupeKey);
    }

    @After
    public void tearDown() throws Exception {
        dupe.clear();
        server.close();
    }

    @Test
    public void test() {
        WebURL a = new WebURL();
        a.setURL("http://nba.hupu.com/");
        WebURL b = new WebURL();
        b.setURL("https://www.baidu.com/");
        
        assertEquals(false, dupe.seenURL(a));
        assertEquals(true, dupe.seenURL(a));
        assertEquals(false, dupe.seenURL(b));
        dupe.clear();
        assertEquals(false, dupe.seenURL(a));
        
    }

}
