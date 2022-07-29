package program;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileWorker {
    public static <T> void writeFileTXT(String fileName,
                                        List<T> items,
                                        Separator separator,
                                        WriteMapper<T> mapper) throws IOException {
        fileName = FileWorkerUtil.getFileExtension(fileName, Format.TXT);
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        FileWorkerUtil.createTXT(items,
                separator,
                mapper,
                bufferedWriter);
        bufferedWriter.close();
        fileWriter.close();
    }

    public static <T> void writeFileXlS(String fileName,
                                        List<T> items,
                                        WriteMapper<T> mapper,
                                        String[] columnNames,
                                        String sheetName) throws IOException {
        fileName = FileWorkerUtil.getFileExtension(fileName, Format.XLS);
        FileOutputStream outputStream = new FileOutputStream(fileName);
        Workbook xls = FileWorkerUtil.createXLS(
                sheetName,
                columnNames,
                items.stream()
                        .map(mapper::map)
                        .collect(Collectors.toList()));
        xls.write(outputStream);
        xls.close();
    }

    public static <T> void writeFileCSV(String fileName,
                                        List<T> items,
                                        Separator separator,
                                        WriteMapper<T> mapper,
                                        String[] columnNames) throws IOException {
        fileName = FileWorkerUtil.getFileExtension(fileName, Format.CSV);
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        FileWorkerUtil.createCSV(items, separator, columnNames, mapper, bufferedWriter);
        bufferedWriter.close();
        fileWriter.close();
    }

    public static <T> List<T> readFileTXT(File file, ReadMapper<T> mapper) throws IOException {
        java.io.FileReader fileReader = new java.io.FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<T> itemList = new ArrayList<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            String[] split = line.split(";");
            itemList.add(mapper.map(split));
        }
        bufferedReader.close();
        fileReader.close();
        return itemList;
    }

    public static <T> List<T> readFileCSV(File file, ReadMapper<T> mapper) throws IOException {
        return readFileTXT(file, mapper);
    }

    public static <T> List<T> readFileXLS(String fileLocation,
                                          ReadMapper<T> mapper,
                                          boolean isHeader, Class<T> object) throws IOException {
        FileInputStream file = new FileInputStream(new File(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);
        List<T> itemList = FileWorkerUtil.readXLS(workbook, mapper, isHeader, object);
        workbook.close();
        file.close();
        return itemList;
    }


//    public static <T> List<T> readFile(File file, ReadMapper<T> mapper) throws IOException {
//        java.io.FileReader fileReader = new java.io.FileReader(file);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        List<T> itemList = new ArrayList<>();
//        while (bufferedReader.ready()) {
//            String line = bufferedReader.readLine();
//            String[] split = line.split(";");
//            itemList.add(mapper.map(split));
//        }
//        bufferedReader.close();
//        fileReader.close();
//        return itemList;
//    }
}
