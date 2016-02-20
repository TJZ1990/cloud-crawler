package cloudwalk.slave.example;

import cloudwalk.slave.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerTest {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 3;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(-1);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
                pageFetcher);

        
        CrawlController controller;
        if (args.length < 3) {
            controller = new CrawlController(config,
                    pageFetcher, robotstxtServer, "139.129.48.209", 6379,
                    "cloud-03");
        } else {
            controller = new CrawlController(config,
                    pageFetcher, robotstxtServer, args[0], Integer.valueOf(args[1]),
                    args[2]);
        }
        //controller.getScheduler().clear();


        controller.addSeed("http://bbs.hupu.com/bxj");
        for (int i = 2; i <= 10; i++) {
            String url = "http://bbs.hupu.com/bxj-" + i;
            controller.addSeed(url);
        }

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(BXJCrawler.class, numberOfCrawlers);
    }
}
