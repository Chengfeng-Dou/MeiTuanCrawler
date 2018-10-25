package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Level;
import java.util.logging.Logger;


public class WebHelper {
    private static DesiredCapabilities dcaps;

    static {

        dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持（第二参数表明的是你的phantomjs引擎所在的路径）
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "phantomjs.exe");
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.INFO);
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, "--webdriver-loglevel=NONE");

    }

    public static WebDriver getDriver() {
        return new PhantomJSDriver(dcaps);
    }


}
