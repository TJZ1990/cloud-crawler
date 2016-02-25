package cloudwalk.slave.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cloudwalk.slave.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BXJCrawler extends WebCrawler {
    private String refPattern1 = "^http://bbs.hupu.com/bxj$";
    private String refPattern2 = "^http://bbs.hupu.com/bxj-\\d+$";
    private String curPattern1 = "^http://bbs.hupu.com/[0-9]+\\.html$";

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String cur = url.getURL().toLowerCase();
        String refer = referringPage.getWebURL().getURL().toLowerCase();

        if (refer.matches(refPattern1) || refer.matches(refPattern2)) {
            if (cur.matches(curPattern1)) {
                return true;
            }
        }

        return false;

    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData) {

            if (url.toLowerCase().matches(curPattern1)) {
                HtmlParseData htmlParseData = (HtmlParseData) page
                        .getParseData();
                String text = htmlParseData.getText();
                Pattern p = Pattern.compile("\\d+回复/(\\d+)亮 \\d+浏览");
                Matcher m = p.matcher(text);

                if (m.find()) {
                    int num = Integer.valueOf(m.group(1));

                    if (num >= 5) {
                        System.out.println(num + "亮贴 " + url + " "
                                + htmlParseData.getTitle());
                    }
                }
            }
        }
    }
}
