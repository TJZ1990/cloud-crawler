package cloudwalk.salve.test.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cloudwalk.slave.crawler.Queue;
import edu.uci.ics.crawler4j.url.WebURL;
import redis.clients.jedis.Jedis;

public class NormalQueueTest {
    private Jedis server;
    private Queue queue;
    private String queueKey = "normalqueue_test";
    
    
    @Before
    public void setUp() throws Exception {
        server = new Jedis("localhost");
        queue = new Queue(server, queueKey);
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
        
        WebURL b = new WebURL();
        b.setURL("bbb");

        WebURL c = new WebURL();
        c.setURL("ccc");
        
        WebURL d = new WebURL();
        d.setURL("ddd");
        
        queue.push(a);
        queue.push(b);
        queue.push(c);
        queue.push(d);

        assertEquals(4, queue.getLength());
        assertEquals("aaa", queue.pop().getURL());
        assertEquals(3, queue.getLength());
        assertEquals("bbb", queue.pop().getURL());
        assertEquals("ccc", queue.pop().getURL());
        assertEquals("ddd", queue.pop().getURL());
        assertEquals(0, queue.getLength());
    }

}
