package passport.astu.sources;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StmBook {
    public XSSFWorkbook stmWorkbook;
    private int lastRow;

    public StmBook(String mainDirPath) {
        File stmFile = new File(mainDirPath + "\\!!!!!!!Состояние ТМ.xlsm");
        try {
            FileInputStream fis = new FileInputStream(stmFile);
            stmWorkbook = new XSSFWorkbook(fis);
            lastRow = stmWorkbook.getSheet("Общий").getLastRowNum();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StmRow getStmRow(int numRow) {
        return new StmRow(this, numRow);
    }

    public int getLastRow() {
        return lastRow;
    }
}
