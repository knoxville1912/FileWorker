package program;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public final class FileWorkerUtil {

    private FileWorkerUtil() {
    }

    public static String getFileExtension(String fileName, Format format) {
        switch (format) {
            case CSV:
                fileName += ".csv";
                break;
            case DOC:
                fileName += ".doc";
                break;
            case PDF:
                fileName += ".pdf";
                break;
            case RTF:
                fileName += ".rtf";
                break;
            case TXT:
                fileName += ".txt";
                break;
            case XLS:
                fileName += ".xls";
                break;
            case DOCX:
                fileName += ".docx";
                break;
            case HTML:
                fileName += ".html";
                break;
        }
        return fileName;
    }

    public static void getColumn(Format format,
                                 String[] columnNames,
                                 Separator columnSeparator,
                                 BufferedWriter bufferedWriter) throws IOException {
        switch (format) {
            case DOC:
            case PDF:
            case XLS:
            case RTF:
            case HTML:
            case DOCX:
                break;
            case CSV:
            case TXT:
                for (int i = 0; i < columnNames.length - 1; i++) {
                    bufferedWriter.write(columnNames[i] + columnSeparator.getSeparator());
                }
                bufferedWriter.write(columnNames[columnNames.length - 1] + '\n');
                break;
        }
    }

    public static <T> void createCSV(List<T> items,
                                     Separator separator,
                                     String[] columnNames,
                                     WriteMapper<T> mapper,
                                     BufferedWriter bufferedWriter) throws IOException {
        for (int i = 0; i < columnNames.length - 1; i++) {
            bufferedWriter.write(columnNames[i] + separator.getSeparator());
        }
        bufferedWriter.write(columnNames[columnNames.length - 1] + '\n');
        createTXT(items, separator, mapper, bufferedWriter);
    }

    public static <T> void createTXT(List<T> items,
                                     Separator separator,
                                     WriteMapper<T> mapper,
                                     BufferedWriter bufferedWriter) throws IOException {
        List<String[]> strings = items.stream().map(mapper::map).collect(Collectors.toList());
        for (int i = 0; i < strings.size(); i++) {
            if (i == strings.size() - 1) {
                for (int j = 0; j < strings.get(i).length - 1; j++) {
                    bufferedWriter.write(strings.get(i)[j] + separator.getSeparator());
                }
                bufferedWriter.write(strings.get(i)[strings.get(i).length - 1]);
            } else {
                for (int j = 0; j < strings.get(i).length - 1; j++) {
                    bufferedWriter.write(strings.get(i)[j] + separator.getSeparator());
                }
                bufferedWriter.write(strings.get(i)[strings.get(i).length - 1] + '\n');
            }
        }
    }

    public static Workbook createXLS(String sheetName,
                                     String[] fieldNames,
                                     List<String[]> data) {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet(sheetName);

        int k = 0;
        if (fieldNames != null) {
            Row header = sheet.createRow(k++);
            for (int j = 0; j < fieldNames.length; j++) {
                Cell headerCell = header.createCell(j);
                headerCell.setCellValue(fieldNames[j]);
            }
        }

        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(k++);
            for (int j = 0; j < data.get(i).length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data.get(i)[j]);
            }
        }

        return workbook;
    }

    public static <T> List<T> readXLS(Workbook workbook,
                                      ReadMapper<T> mapper,
                                      boolean isHeader,
                                      Class<T> object) {
        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(i).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getLocalDateTimeCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BOOLEAN:
                        data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA:
                        data.get(i).add(cell.getCellFormula() + "");
                        break;
                    default:
                        data.get(i).add(" ");
                }
            }
            i++;
        }

//        Field[] fields = (Class<T>)
//                ((ParameterizedType) FileWorkerUtil.class.getGenericSuperclass())
//                        .getActualTypeArguments()[0].getClass().getFields();

        Field[] fields = object.getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            if (Integer.class.equals(fields[j].getType()) || Long.class.equals(fields[j].getType())) {
                for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
                    String changedString = entry.getValue().get(j).replace(".0", "");
                    entry.getValue().set(j, changedString);
                }
            }
        }

        System.out.println(Arrays.stream(FileWorkerUtil.class.getMethods())
                        .filter(f -> f.getName().equals("readXLS"))
                .map(m -> Pair.create(m.getName(), m.getGenericReturnType()))
                .collect(Collectors.toList()));



        System.out.println(mapper.getClass());

        List<T> itemList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
            itemList.add(mapper.map(entry.getValue().toArray(new String[0])));
        }

        return itemList;
    }
}
