package cloudwalk.salve.test.crawler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import cloudwalk.slave.crawler.Counter;

public class CounterTest {
    Counter counter;
    Jedis server;
    @Before
    public void setUp() throws Exception {
        server = new Jedis("localhost");
        counter = new Counter(server, "counter_test");
    }

    @After
    public void tearDown() throws Exception {
        counter.clear();
        server.close();
    }

    @Test
    public void test() {
        assertEquals(counter.getNumber(), 0);
        counter.incr();
        assertEquals(counter.getNumber(), 1);
    }

}
