package passport.astu;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import passport.astu.sources.StmBook;
import passport.astu.sources.StmRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
//        String stmDirPath = "D:\\ПаспортАСТУ";
//        String mainDirPath = "D:\\ПаспортАСТУ";
        String logFilePath = "D:\\ПаспортАСТУ";
        String stmDirPath = "\\\\Len-file-ks\\КС_Документы\\ОАСУ\\УКК_ТМ";
        String mainDirPath = "\\\\Len-file-ks\\КС_Документы\\ОАСУ\\УКК_ТМ";
//        String logFilePath = mainDirPath + "\\Западный район\\29570 ул. Гаккелевская, д.18, к.2, лит.А";

        boolean isBDFileExist = false;

        StmBook stmBook = new StmBook(stmDirPath);
        for (int i = 1; i <= stmBook.getLastRow(); i++) {
            StmRow stmRow = stmBook.getStmRow(i);
            if (stmRow.getPassport().equals("+")) {
                LogFile.resetErrorCounter();
                LogFile.addRow("Объект " + stmRow.getNumber());
                String substationPath = mainDirPath + "\\" + stmRow.getRegion() + " район" + "\\" + stmRow.getNumber() + " " + stmRow.getAddress();
                File substationFile = new File(substationPath);
                if (substationFile.isDirectory()) {
                    for (File file :
                            Objects.requireNonNull(substationFile.listFiles())) {
                        if (file.getName().equals("БД " + stmRow.getNumber() + ".xlsm") || file.getName().equals("БД " + stmRow.getNumber() + ".xlsx")){
                            isBDFileExist = true;
                            try {
                                FileInputStream fis = new FileInputStream(file);
                                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                                new Passport(workbook, file, stmRow);
                            } catch (IOException e) {
                                LogFile.addErrorRow("Ошибка открытия файла БД " + stmRow.getNumber());
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!isBDFileExist) {
                        LogFile.addErrorRow("Отсутствует или переименован файл БД " + stmRow.getNumber() + ".xlsm");
                    }
                } else LogFile.addErrorRow("Не найдена директория объекта " + stmRow.getNumber());
            }
        }
        LogFile.createLogFile(logFilePath);
    }
}
