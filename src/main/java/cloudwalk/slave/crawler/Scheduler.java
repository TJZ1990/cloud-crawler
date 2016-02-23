package cloudwalk.slave.crawler;

import redis.clients.jedis.Jedis;
import edu.uci.ics.crawler4j.crawler.Configurable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.url.WebURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Scheduler extends Configurable {

    protected static final Logger logger = LoggerFactory
            .getLogger(Scheduler.class);

    private PriorityQueue queue;
    private Dupefilter dupefilter;
    private Counter counter;
    private Jedis server;
    protected final Object mutex = new Object();

    protected boolean isFinished = false;

    public Scheduler(Jedis server, String queueKey, String dupefilterKey,
            String counterKey, CrawlConfig config) {
        super(config);
        this.server = server;
        this.queue = new PriorityQueue(server, queueKey);
        this.dupefilter = new Dupefilter(server, dupefilterKey);
        this.counter = new Counter(server, counterKey);
    }

    public void scheduleAll(List<WebURL> urls) {
        synchronized (mutex) {
            for (WebURL url : urls) {
                if (config.getMaxPagesToFetch() > 0
                        && getScheduledNum() >= config.getMaxPagesToFetch()) {
                    logger.info("scheduled pages exceed maximum pages to fetch");
                    return;
                }
                schedule(url);
            }
        }
    }

    public void schedule(WebURL url) {
        synchronized (mutex) {
            if (config.getMaxPagesToFetch() > 0
                    && getScheduledNum() >= config.getMaxPagesToFetch()) {
                logger.info("scheduled pages exceed maximum pages to fetch");
                return;
            }
            if (!dupefilter.seenURL(url)) {
                queue.push(url);
                counter.incr();
            }
        }
    }

    public void getNextURLs(int max, List<WebURL> result) {
        synchronized (mutex) {
            int num = 0;
            WebURL url;
            while (!isFinished && num < max) {
                url = queue.pop();
                if (url == null) {
                    return;
                }
                result.add(url);
                num++;
            }
        }
    }

    public long getQueueLength() {
        return queue.getLength();
    }

    public long getScheduledNum() {
        return counter.getNumber();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void clear() {
        queue.clear();
        dupefilter.clear();
        counter.clear();
    }

    public void close() {

    }

    public void finish() {
        isFinished = true;
        server.close();
        logger.info("close redis client");
    }
}