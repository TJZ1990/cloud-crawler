/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloudwalk.slave.crawler;

import redis.clients.jedis.Jedis;
import edu.uci.ics.crawler4j.crawler.Configurable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller that manages a crawling session. This class creates the
 * crawler threads and monitors their progress.
 *
 * @author Yasser Ganjisaffar Jiezhe Tang
 */
public class CrawlController extends Configurable {

    static final Logger logger = LoggerFactory.getLogger(CrawlController.class);

    /**
     * The 'customData' object can be used for passing custom crawl-related
     * configurations to different components of the crawler.
     */
    protected Object customData;

    /**
     * Once the crawling session finishes the controller collects the local data
     * of the crawler threads and stores them in this List.
     */
    protected List<Object> crawlersLocalData = new ArrayList<>();

    /**
     * Is the crawling of this session finished?
     */
    protected boolean finished;

    /**
     * Is the crawling session set to 'shutdown'. Crawler threads monitor this
     * flag and when it is set they will no longer process new pages.
     */
    protected boolean shuttingDown;
    protected PageFetcher pageFetcher;
    protected RobotstxtServer robotstxtServer;
    protected Scheduler scheduler;
    protected Jedis server;
    protected final Object waitingLock = new Object();

    public CrawlController(CrawlConfig config, PageFetcher pageFetcher,
            RobotstxtServer robotstxtServer, String ip, int port, String pwd)
            throws Exception {
        super(config);

        config.validate();

        server = new Jedis(ip, port);

        if (pwd != null && !pwd.equals("")) {
            server.auth(pwd);
        }
        scheduler = new Scheduler(server, "crawler_queue",
                "crawler_dupefilter", "crawler_counter", config);
        this.pageFetcher = pageFetcher;
        this.robotstxtServer = robotstxtServer;
        finished = false;
        shuttingDown = false;
    }

    /**
     * Start the crawling session and wait for it to finish.
     *
     * @param _c
     *            the class that implements the logic for crawler threads
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T>
     *            Your class extending WebCrawler
     */
    public <T extends WebCrawler> void start(final Class<T> _c,
            final int numberOfCrawlers) {
        this.start(_c, numberOfCrawlers, true);
    }

    /**
     * Start the crawling session and return immediately.
     *
     * @param _c
     *            the class that implements the logic for crawler threads
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T>
     *            Your class extending WebCrawler
     */
    public <T extends WebCrawler> void startNonBlocking(final Class<T> _c,
            final int numberOfCrawlers) {
        this.start(_c, numberOfCrawlers, false);
    }

    protected <T extends WebCrawler> void start(final Class<T> _c,
            final int numberOfCrawlers, boolean isBlocking) {
        try {
            finished = false;
            crawlersLocalData.clear();
            final List<Thread> threads = new ArrayList<>();
            final List<T> crawlers = new ArrayList<>();
            for (int i = 1; i <= numberOfCrawlers; i++) {
                T crawler = _c.newInstance();
                Thread thread = new Thread(crawler, "Crawler " + i);
                crawler.setThread(thread);
                crawler.init(i, this);
                thread.start();
                crawlers.add(crawler);
                threads.add(thread);
                logger.info("Crawler {} started", i);
            }

            final CrawlController controller = this;

            Thread monitorThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        synchronized (waitingLock) {

                            while (true) {
                                sleep(10);
                                boolean someoneIsWorking = false;
                                for (int i = 0; i < threads.size(); i++) {
                                    Thread thread = threads.get(i);
                                    if (!thread.isAlive()) {
                                        if (!shuttingDown) {
                                            logger.info(
                                                    "Thread {} was dead, I'll recreate it",
                                                    i);
                                            T crawler = _c.newInstance();
                                            thread = new Thread(crawler,
                                                    "Crawler " + (i + 1));
                                            threads.remove(i);
                                            threads.add(i, thread);
                                            crawler.setThread(thread);
                                            crawler.init(i + 1, controller);
                                            thread.start();
                                            crawlers.remove(i);
                                            crawlers.add(i, crawler);
                                        }
                                    } else if (crawlers.get(i)
                                            .isNotWaitingForNewURLs()) {
                                        someoneIsWorking = true;
                                    }
                                }
                                if (!someoneIsWorking) {
                                    // Make sure again that none of the threads
                                    // are alive.
                                    logger.info("It looks like no thread is working, waiting for 10 seconds to make sure...");
                                    sleep(10);

                                    someoneIsWorking = false;
                                    for (int i = 0; i < threads.size(); i++) {
                                        Thread thread = threads.get(i);
                                        if (thread.isAlive()
                                                && crawlers
                                                        .get(i)
                                                        .isNotWaitingForNewURLs()) {
                                            someoneIsWorking = true;
                                        }
                                    }
                                    if (!someoneIsWorking) {
                                        if (!shuttingDown) {
                                            long queueLength = scheduler
                                                    .getQueueLength();
                                            if (queueLength > 0) {
                                                continue;
                                            }
                                            logger.info("No thread is working and no more URLs are in queue waiting for another 10 seconds to make sure...");
                                            sleep(10);
                                            queueLength = scheduler
                                                    .getQueueLength();
                                            if (queueLength > 0) {
                                                continue;
                                            }
                                        }

                                        logger.info("All of the crawlers are stopped. Finishing the process...");
                                        // At this step, frontier notifies the
                                        // threads that were waiting for new
                                        // URLs and they should stop
                                        scheduler.finish();
                                        for (T crawler : crawlers) {
                                            crawler.onBeforeExit();
                                            crawlersLocalData.add(crawler
                                                    .getMyLocalData());
                                        }

                                        logger.info("Waiting for 10 seconds before final clean up...");
                                        sleep(10);

                                        scheduler.close();
                                        pageFetcher.shutDown();

                                        finished = true;
                                        waitingLock.notifyAll();

                                        return;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected Error", e);
                    }
                }
            });
            monitorThread.start();
            if (isBlocking) {
                waitUntilFinish();
            }

        } catch (Exception e) {
            logger.error("Error happened", e);
        }
    }

    /**
     * Wait until this crawling session finishes.
     */

    public void waitUntilFinish() {
        while (!finished) {
            synchronized (waitingLock) {
                if (finished) {
                    return;
                }
                try {
                    waitingLock.wait();
                } catch (InterruptedException e) {
                    logger.error("Error occurred", e);
                }
            }
        }
    }

    /**
     * Once the crawling session finishes the controller collects the local data
     * of the crawler threads and stores them in a List. This function returns
     * the reference to this list.
     *
     * @return List of Objects which are your local data
     */

    public List<Object> getCrawlersLocalData() {
        return crawlersLocalData;
    }

    protected static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception ignored) {
            // Do nothing
        }
    }

    /**
     * Adds a new seed URL. A seed URL is a URL that is fetched by the crawler
     * to extract new URLs in it and follow them for crawling.
     *
     * @param pageUrl
     *            the URL of the seed
     */

    public void addSeed(String pageUrl) {
        String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
        if (canonicalUrl == null) {
            logger.error("Invalid seed URL: {}", pageUrl);
        }

        WebURL webUrl = new WebURL();
        webUrl.setURL(canonicalUrl);
        webUrl.setDocid(-1);
        webUrl.setDepth((short) 0);
        if (!robotstxtServer.allows(webUrl)) {
            logger.warn("Robots.txt does not allow this seed: {}", pageUrl); // using
                                                                             // the
                                                                             // WARN
                                                                             // level
                                                                             // here,
                                                                             // as
                                                                             // the
                                                                             // user
                                                                             // specifically
                                                                             // asked
                                                                             // to
                                                                             // add
                                                                             // this
                                                                             // seed
        } else {
            scheduler.schedule(webUrl);
        }
    }

    public PageFetcher getPageFetcher() {
        return pageFetcher;
    }

    public void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public RobotstxtServer getRobotstxtServer() {
        return robotstxtServer;
    }

    public void setRobotstxtServer(RobotstxtServer robotstxtServer) {
        this.robotstxtServer = robotstxtServer;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     * Set the current crawling session set to 'shutdown'. Crawler threads
     * monitor the shutdown flag and when it is set to true, they will no longer
     * process new pages.
     */

    public void shutdown() {
        logger.info("Shutting down...");
        this.shuttingDown = true;
        getPageFetcher().shutDown();
        scheduler.finish();
    }
}