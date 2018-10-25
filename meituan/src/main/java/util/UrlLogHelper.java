package util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class UrlLogHelper {
    private static File urlMemory = new File("url.txt");

    public static void writeUrlToDisk(List<String> urls) throws IOException {
        if (!urlMemory.exists()) {
            if (!urlMemory.createNewFile()) {
                return;
            }
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(urlMemory));
        for (String url : urls) {
            out.write(String.format("%s\r\n", url));
        }

        out.flush();
        out.close();
    }

    public static LinkedList<String> getUrlFromDisk() throws IOException {
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(urlMemory)); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        LinkedList<String> urls = new LinkedList<String>();
        String line;
        while ((line = br.readLine()) != null) {
            urls.add(line);
        }
        reader.close();
        return urls;
    }


    public static boolean doNotHasLogFile() {
        return !urlMemory.exists();
    }
}
