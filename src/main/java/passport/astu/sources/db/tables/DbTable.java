package passport.astu.sources.db.tables;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import passport.astu.LogFile;

import java.util.ArrayList;
import java.util.List;

public class DbTable {

    private static final String SHEET_NAME = "Оборудование";
    private XSSFTable table;
    private XSSFSheet sheet;
    private int firstRow, lastRow;
    private final XSSFWorkbook workbook;
    private final String tableName;

    public DbTable(XSSFWorkbook workbook, String tableName) {
        this.workbook = workbook;
        this.tableName = tableName;
        if (isTableExist()) {
            table = workbook.getTable(tableName);
            this.sheet = workbook.getSheet(SHEET_NAME);
            firstRow = table.getStartRowIndex();
            lastRow = table.getEndRowIndex();
        } else {
           LogFile.addErrorRow("Таблица " + tableName + " не найдена");
        }
    }

    private List<String> createDataList(String columnName) {
        List<String> list = new ArrayList<>();
        for (int i = firstRow + 1; i <= lastRow; i++){
            CellType cellType = sheet.getRow(i).getCell(table.findColumnIndex(columnName)).getCellType();
            switch (cellType) {
                case NUMERIC:
                    list.add(String.valueOf((long)sheet.getRow(i).getCell(table.findColumnIndex(columnName)).getNumericCellValue()));
                    break;
                case STRING:
                case FORMULA:
                    list.add(sheet.getRow(i).getCell(table.findColumnIndex(columnName)).getRichStringCellValue().getString());
                    break;
                default:
                    list.add("");
            }
        }
        return list;
    }

    public List<String> getDataList(String columnName) {
        return createDataList(columnName);
    }

    public boolean isTableExist() {
        return workbook.getTable(tableName) != null;
    }
}
