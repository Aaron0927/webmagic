package us.codecraft.webmagic.processor.example;

import com.alibaba.fastjson.annotation.JSONField;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.3.2
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(5000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        //page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        //page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+)").all());
        //page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        // 记录这个用户这首歌听过的次数
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        // 每个页面有５０条记录，但最后一页可能小于５０
        // 在这里面做一次combiner, reduce在FilePipe中
        for (int i = 1; i <= 50; ++i) {
            String songAuthor = "";
            String user = page.getHtml().xpath("//div[@class='infos']/h1/text()").toString().split("最近")[0];
            String song = page.getHtml().xpath("//table/tbody/tr[" + String.valueOf(i) +"]/td[@class='song_name']/a[1]/@title").toString();
            String author = page.getHtml().xpath("//table/tbody/tr[" + String.valueOf(i) + "]/td[@class='song_name']/a[2]/text()").toString();
            // 将有特殊字符的歌曲过滤掉
            if ( !songAuthor.contains("\"") && !songAuthor.contains("/")
                    && !songAuthor.contains(":") && !songAuthor.contains("（")
                    && !songAuthor.contains("(") && !user.contains(".")
                    && !song.contains(".") && !song.contains("-")) {
                songAuthor = user + "," + song + "," + author;
                if (map.containsKey(songAuthor)) {
                    map.put(songAuthor, map.get(songAuthor) + 1);
                } else {
                    map.put(songAuthor, 1);
                }
            }
        }
        System.out.println(page.getHtml().xpath("//div[@class='infos']/h1/text()").toString().split("最近")[0]);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            page.putField(entry.getKey(), entry.getValue().toString());
        }
        // 发现新链接
        page.addTargetRequest(page.getHtml()
        .xpath("//div[@class='all_page']/a[@class='p_redirect_l']/@href").get());
        //for (int i = 1287020; i < 2000000; ++i) {
          //  page.addTargetRequest("http://www.xiami.com/space/charts-recent/u/" + String.valueOf(i) + "/page/1");
        //}
    }

    @Override
    public Site getSite() {
        site.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12");
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor())
                .addUrl("http://www.xiami.com/space/charts-recent/u/1287027/page/1?spm=a1z1s.6626017.0.0.AJbApy")
                .addPipeline(new FilePipeline("/home/zhangchengfei/temp/"))
                .thread(1)
                .run();
    }
}
