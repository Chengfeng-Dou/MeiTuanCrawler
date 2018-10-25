package controller;

import clawer.CityExtractor;
import clawer.MeituanCrawler;
import util.ExcelHelper;
import util.UrlLogHelper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MeituanController {
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,
            5, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
    private final ArrayBlockingQueue<MeituanCrawler> avlCrawlers = new ArrayBlockingQueue<MeituanCrawler>(4);

    public static void main(String[] args) throws Exception {
        MeituanController controller = new MeituanController();
        controller.run();
    }

    public void run() throws Exception {
        CityExtractor cityExtractor = new CityExtractor();
        LinkedList<String> cityInfo;
        if (UrlLogHelper.doNotHasLogFile()) {
            cityInfo = cityExtractor.getCityInfo();
            UrlLogHelper.writeUrlToDisk(cityInfo);
        } else {
            cityInfo = UrlLogHelper.getUrlFromDisk();
        }

        System.out.println(cityInfo.toString());
        UrlLogHelper.writeUrlToDisk(cityInfo);

        avlCrawlers.add(new MeituanCrawler(this));
        avlCrawlers.add(new MeituanCrawler(this));

        while (!((Queue<String>) cityInfo).isEmpty()) {
            String cityName = ((Queue<String>) cityInfo).poll();
            MeituanCrawler crawler = avlCrawlers.take();
            crawler.addUrl(String.format("https://%s.meituan.com/", cityName));
            crawler.addUrl(String.format("https://hotel.meituan.com/%s/xj23/", cityName));
            crawler.addUrl(String.format("https://hotel.meituan.com/%s/xj01/", cityName));

            threadPool.execute(crawler);
            TimeUnit.MILLISECONDS.sleep(1000);
            UrlLogHelper.writeUrlToDisk(cityInfo);
        }


        ExcelHelper.getInstance().writeDataToDisk();
    }

    public void reuse(MeituanCrawler crawler) {
        this.avlCrawlers.add(crawler);
    }
}
