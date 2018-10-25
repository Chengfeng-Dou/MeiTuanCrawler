package util;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ExcelHelper {


    private static class ExcelHelperHolder {
        static ExcelHelper instance = new ExcelHelper();
    }

    public static ExcelHelper getInstance() {
        return ExcelHelperHolder.instance;
    }

    private final HSSFWorkbook workbook;
    private final HSSFSheet sheet;
    private int rowNum = 0;

    private ExcelHelper() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet();
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 8000);

    }


    public void insertHotelNameAndCityToExcel(String hotelName, String city) {
        System.out.println("save: " + hotelName + " " + city);
        synchronized (ExcelHelper.class) {
            HSSFRow row = sheet.createRow(rowNum);
            row.setHeight((short) 900);
            row.createCell(0).setCellValue(new HSSFRichTextString(city));
            row.createCell(1).setCellValue(new HSSFRichTextString(hotelName));
            rowNum++;
            if (rowNum % 50 == 0) {
                writeDataToDisk();
            }
        }
    }

    public void writeDataToDisk() {
        File file = new File("D://meituan_data.xls");
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                }
            }
            FileOutputStream stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
