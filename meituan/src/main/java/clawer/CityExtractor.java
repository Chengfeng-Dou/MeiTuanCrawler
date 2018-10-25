package clawer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class CityExtractor {
    private final LinkedList<String> cityInfo = new LinkedList<String>();


    public LinkedList<String> getCityInfo() throws Exception {
        Document doc = Jsoup.connect("https://www.meituan.com/changecity/").timeout(3000).get();
        System.out.println("reading city!");
        Element area = doc.selectFirst("div[class=alphabet-city-area]");
        Elements alphabetCity = area.select("a[class=link city]");
        for (Element element : alphabetCity) {
            putCityNamePinYinAndName(cityInfo, element);
        }
        System.out.println("finish reading!");

        return cityInfo;
    }

    private void putCityNamePinYinAndName(List<String> list, Element element) {
        String key = element.attr("href")
                .replace("//", "").replace(".meituan.com", "");
        list.add(key);
    }

}
