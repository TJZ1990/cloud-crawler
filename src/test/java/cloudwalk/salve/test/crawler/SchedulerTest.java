package cloudwalk.salve.test.crawler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cloudwalk.slave.crawler.Scheduler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;

public class SchedulerTest {
    Jedis server;
    Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        server = new Jedis("localhost");
        CrawlConfig config = new CrawlConfig();
        scheduler = new Scheduler(server, "queue_test", "dupe_test",
                "counter_test", config);
    }

    @After
    public void tearDown() throws Exception {
        scheduler.clear();
        scheduler.close();
    }

    @Test
    public void test() {
        String url1 = "http://nba.hupu.com/";
        String url2 = "http://www.google.com/";

        WebURL a = new WebURL();
        a.setURL(url1);
        a.setDepth((short) 3);

        WebURL b = new WebURL();
        b.setURL(url2);
        b.setDepth((short) 2);

        scheduler.schedule(a);
        assertEquals(1, scheduler.getQueueLength());

        scheduler.schedule(a);
        assertEquals(1, scheduler.getQueueLength());

        scheduler.schedule(b);
        assertEquals(2, scheduler.getQueueLength());

        List<WebURL> l = new ArrayList<WebURL>();
        scheduler.getNextURLs(1, l);
        assertEquals(url2, l.get(0).getURL());

        assertEquals(1, scheduler.getQueueLength());
        assertEquals(2, scheduler.getScheduledNum());

    }

}
