
package cloudwalk.crawler;

import redis.clients.jedis.Jedis;
import edu.uci.ics.crawler4j.crawler.Configurable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.url.WebURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Frontier extends Configurable {

  protected static final Logger logger = LoggerFactory.getLogger(Frontier.class);

  private Queue queue;
  private Dupefilter dupefilter;
  protected final Object mutex = new Object();

  protected boolean isFinished = false;

  public Frontier(Jedis server, String queueKey, String dupefilterKey, CrawlConfig config) {
    super(config);
    this.queue = new Queue(server, queueKey);
    this.dupefilter = new Dupefilter(server, dupefilterKey);
    if (!config.isResumableCrawling()) {
        queue.clear();
        dupefilter.clear();
    }

  }

  public void scheduleAll(List<WebURL> urls) {
    synchronized (mutex) {
      for (WebURL url : urls) {
          schedule(url);
      }
    }
  }

  public void schedule(WebURL url) {
    synchronized (mutex) {
        if(!dupefilter.seenURL(url)) {
            queue.push(url);
        }
    }
  }

  public void getNextURLs(int max, List<WebURL> result) {
    while (true) {
      synchronized (mutex) {
        if (isFinished) {
          return;
        }

        int num = 0;
        WebURL url;
        while(queue.getLength() > 0 && num < max) {
            url = queue.pop();
            result.add(url);
            num++;
        }

        if (result.size() > 0) {
          return;
        }
      }

      //TODO：If no URL is got, block the thread.
      
    }
  }


  public long getQueueLength() {
    return queue.getLength();
  }


  public boolean isFinished() {
    return isFinished;
  }

  public void close() {

  }

  public void finish() {
    isFinished = true;

  }
}