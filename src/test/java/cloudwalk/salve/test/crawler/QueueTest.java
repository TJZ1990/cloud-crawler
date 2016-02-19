package cloudwalk.salve.test.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import cloudwalk.slave.crawler.PriorityQueue;
import edu.uci.ics.crawler4j.url.WebURL;

public class QueueTest {
    private Jedis server;
    private PriorityQueue queue;
    private String queueKey = "queue_test";
    
    @Before
    public void setUp() throws Exception {
        server = new Jedis("localhost");
        queue = new PriorityQueue(server, queueKey);
    }

    @After
    public void tearDown() throws Exception {
        queue.clear();
        server.close();
    }

    @Test
    public void test() {
        WebURL a = new WebURL();
        a.setURL("aaa");
        a.setDepth((short)3);
        
        WebURL b = new WebURL();
        b.setURL("bbb");
        b.setDepth((short)3);

        WebURL c = new WebURL();
        c.setURL("ccc");
        c.setDepth((short)2);
        
        WebURL d = new WebURL();
        d.setURL("ddd");
        d.setDepth((short)1);
        
        queue.push(a);
        queue.push(b);
        queue.push(c);
        queue.push(d);

        assertEquals(4, queue.getLength());
        assertEquals("ddd", queue.pop().getURL());
        assertEquals(3, queue.getLength());
        assertEquals("ccc", queue.pop().getURL());
        assertEquals("aaa", queue.pop().getURL());
        assertEquals("bbb", queue.pop().getURL());
        assertEquals(0, queue.getLength());

    }

}
