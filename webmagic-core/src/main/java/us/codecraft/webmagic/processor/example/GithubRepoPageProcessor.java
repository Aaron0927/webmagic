package us.codecraft.webmagic.processor.example;

import com.alibaba.fastjson.annotation.JSONField;
import com.sun.org.apache.xerces.internal.xs.StringList;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.3.2
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setTimeOut(20000).setRetryTimes(2).setSleepTime(5100);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        ////////////////////////////////////////////////////////////////////////////////////////////////
        //                                                                                            //
        //                                   爬取用户行为信息                                           //
        //                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 记录这个用户这首歌听过的次数
//        HashMap<String, Integer> map = new HashMap<String, Integer>();
//
//        // 每个页面有５０条记录，但最后一页可能小于５０
//        // 在这里面做一次combiner, reduce在FilePipe中
//        for (int i = 1; i <= 50; ++i) {
//            String songAuthor = "";
//            String user = page.getHtml().xpath("//div[@class='infos']/h1/text()").toString().split("最近")[0];
//            String song = page.getHtml().xpath("//table/tbody/tr[" + String.valueOf(i) +"]/td[@class='song_name']/a[1]/@title").toString();
//            String author = page.getHtml().xpath("//table/tbody/tr[" + String.valueOf(i) + "]/td[@class='song_name']/a[2]/text()").toString();
//            // 将有特殊字符的歌曲过滤掉
//            if (user != null && song != null && author != null) {
//                if (!songAuthor.contains("\"") && !songAuthor.contains("/")
//                        && !songAuthor.contains(":") && !songAuthor.contains("（")
//                        && !songAuthor.contains("(") && !user.contains(".")
//                        && !song.contains(".") && !song.contains("-")
//                        ) {
//                    songAuthor = user + "," + song + "," + author;
//                    if (map.containsKey(songAuthor)) {
//                        map.put(songAuthor, map.get(songAuthor) + 1);
//                    } else {
//                        map.put(songAuthor, 1);
//                    }
//                }
//            }
//        }
//        System.out.println(page.getHtml().xpath("//div[@class='infos']/h1/text()").toString().split("最近")[0]);
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            page.putField(entry.getKey(), entry.getValue().toString());
//        }
//
//        // 发现新链接
//        page.addTargetRequest(page.getHtml()
//        .xpath("//div[@class='all_page']/a[@class='p_redirect_l']/@href").get());

        //for (int i = 1287020; i < 2000000; ++i) {
          //  page.addTargetRequest("http://www.xiami.com/space/charts-recent/u/" + String.valueOf(i) + "/page/1");
        //}


        ////////////////////////////////////////////////////////////////////////////////////////////////
        //                                                                                            //
        //                                   爬取用户信息                                               //
        //                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////
        for (int i = 1; i <= 12; ++i) { // i不同的用户设置的值是不一样的，有的设置为20，有的为12
            page.putField("http://www.xiami.com/space/charts-recent/u/"
                    + page.getHtml().xpath("//ul[@class='clearfix user_list']/li[" + String.valueOf(i) + "]/div/p[@class='name']/a/@href").toString().split("/u/")[1]
                    + "/page/1", "");
            System.out.println("http://www.xiami.com/space/charts-recent/u/"
                    + page.getHtml().xpath("//ul[@class='clearfix user_list']/li[" + String.valueOf(i) + "]/div/p[@class='name']/a/@href").toString().split("/u/")[1]
                    + "/page/1");
        }

        page.addTargetRequest(page.getHtml().xpath("//div[@class='all_page']/a[@class='p_redirect_l']/@href").get());

    }

    @Override
    public Site getSite() {
        site.setUserAgent("Googlebot/2.1 (+http://www.googlebot.com/bot.html)");
        return site;
    }

    public static void main(String[] args) {
//        List<String> list = new ArrayList<String>();
//        list.add("http://www.xiami.com/space/charts-recent/u/1287027/page/1?spm=a1z1s.6626017.0.0.AJbApy");
//        try {
//            String filepath = "/home/zhangchengfei/temp/userInfo.csv";
//            File filename = new File(filepath);
//            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
//            BufferedReader br = new BufferedReader(reader);
//            String line = "";
//            line = br.readLine();
//            while (line != null) {
//                line = br.readLine(); // 一次读入一行数据
//                if (line != null) {
//                    list.add(line);
//                    //System.out.println(line);
//                }
//
//            }
//        } catch (IOException ie) {
//            ie.printStackTrace();
//        }
        Spider.create(new GithubRepoPageProcessor())
                //.addUrl("http://www.xiami.com/space/charts-recent/u/1287027/page/1?spm=a1z1s.6626017.0.0.AJbApy") //音乐种子
                //.addUrl("http://i.xiami.com/spice/fans?spm=a1z1s.6632057.350708681.1.VEjx4u") //用户信息种子页
                //.addUrl("http://www.xiami.com/space/fans/u/9?spm=a1z1s.6626009.229054169.3.hospZN") //用户信息种子页

                .addUrl("http://i.xiami.com/mayeroleg/fans?spm=a1z1s.6632057.350708681.1.bYfDHS") //new new
                //.addUrl("http://www.xiami.com/space/fans/u/9/page/27")
                .addPipeline(new FilePipeline("/home/zhangchengfei/temp/"))//.setScheduler(new FileCacheQueueScheduler())
                .thread(1)//.startUrls(list)
                .run();
    }
}
