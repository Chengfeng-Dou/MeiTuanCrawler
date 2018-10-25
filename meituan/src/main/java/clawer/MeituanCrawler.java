package clawer;

import controller.MeituanController;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ExcelHelper;
import util.WebHelper;
import util.PinyinUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MeituanCrawler implements Runnable {
    private static final Pattern patternFilter = Pattern.compile("https://hotel\\.meituan\\.com/[a-z]+/(xj23|xj01)/.*");
    private static final Pattern changeCity = Pattern.compile("https://[a-z]+\\.meituan\\.com/");
    private Queue<String> urlQueue = new LinkedList<String>();
    private static SimpleBloomFilter reVisitFilter = new SimpleBloomFilter();
    private WebDriver driver = WebHelper.getDriver();
    private static SimpleBloomFilter cityFilter = new SimpleBloomFilter();

    private MeituanController meituanController;
    private String currentCity = null;
    private int loopTimes = 0;

    public MeituanCrawler(MeituanController meituanController) {
        this.meituanController = meituanController;
    }

    public void addUrl(String url) {
        urlQueue.add(url);
        reVisitFilter.add(String.format("%spn1/", url));
    }


    private void visit(String url) {
        WebElement body = getWebElement(url);
        if (body == null) {
            return;
        }

        List<WebElement> titles = body.findElements(By.className("poi-title"));

        for (WebElement element : titles) {
            String hotelName = element.getText().replaceAll("\\d+", "");
            ExcelHelper.getInstance().insertHotelNameAndCityToExcel(hotelName, currentCity);
        }

        List<WebElement> urls = body.findElements(By.className("page-link"));
        for (WebElement element : urls) {
            WebElement a = element.findElement(By.tagName("a"));
            if (a != null) {
                String extract = a.getAttribute("href");
                if (!urlQueue.contains(extract) && !reVisitFilter.contains(extract) && patternFilter.matcher(url).matches()) {
                    System.out.printf("add url: %s%n", extract);
                    urlQueue.add(extract);
                }
            }
        }


    }

    private WebElement getWebElement(String url) {
        driver.manage().deleteAllCookies();
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

        System.out.println(driver.getCurrentUrl());
        System.out.println(url);

        WebElement body = driver.findElement(By.tagName("body"));
        System.out.println(body.getText());
        return body;
    }

    private void finishRunning(){
        loopTimes = 40;
    }

    public void run() {
        loopTimes = 0;
        currentCity = null;
        while (!urlQueue.isEmpty() && loopTimes < 40) {
            String url = urlQueue.poll();
            loopTimes ++;

            if (!reVisitFilter.contains(url) && patternFilter.matcher(url).matches()) {
                System.out.printf("visit: %s%n", url);
                visit(url);
            } else if(changeCity.matcher(url).matches()){
                if(!setCity(url)){
                    return;
                }
            }

            reVisitFilter.add(url);

            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        meituanController.reuse(this);
    }

    private boolean setCity(String url){
        WebElement body = getWebElement(url);
        if (currentCity == null) {
            WebElement city = body.findElement(By.className("current-city"));

            currentCity = city.getText();
            String pinyin = PinyinUtils.getPinYin(currentCity).toLowerCase();
            System.out.println("change city: " + pinyin);
            if(cityFilter.contains(pinyin)){
                finishRunning();
                System.out.println("ignore: " + pinyin);
                return false;
            }
            cityFilter.add(pinyin);
        }
        return true;
    }
}
