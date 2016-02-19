package cloudwalk.salve.test;

import cloudwalk.slave.crawler.comm.Communicator;
import org.junit.Test;

/**
 * Created by 1333907 on 2/18/16.
 * A class testing Communicator.
 */
public class CommunicatorTest {
    @Test
    public void testSend() {
        Communicator.sendMessage("www.baidu.com");
    }
}
