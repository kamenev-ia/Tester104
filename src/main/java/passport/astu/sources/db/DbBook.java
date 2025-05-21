package passport.astu.sources.db;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DbBook extends XSSFWorkbook {
    private final XSSFWorkbook workbook;

    public DbBook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }
}
