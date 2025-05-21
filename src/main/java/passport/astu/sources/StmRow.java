package passport.astu.sources;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import passport.astu.LogFile;

public class StmRow {
    public static final int REGION_COLUMN = 1;
    public static final int NUMBER_COLUMN =  3;
    public static final int ADDRESS_COLUMN = 4;
    public static final int ORGANIZATION_COLUMN = 7;
    public static final int PROJECT_COLUMN = 9;
    public static final int PROJECT_DATE_COLUMN = 10;
    public static final int PASSPORT_COLUMN = 51;
    public static final String SHEET_NAME = "Общий";

    private String region, number, address, organization, project, projectDate;
    private final String passport;

    public StmRow(StmBook stmBook, int rowNumber) {

        XSSFRow row = stmBook.stmWorkbook.getSheet(SHEET_NAME).getRow(rowNumber);

        if (row.getCell(PASSPORT_COLUMN) != null) {
            passport = row.getCell(PASSPORT_COLUMN).getStringCellValue();
        } else {
            passport = "";
        }
        if (passport.equals("+")) {
            region = row.getCell(REGION_COLUMN).getStringCellValue();
            double numberDouble = row.getCell(NUMBER_COLUMN).getNumericCellValue();
            number = (int)numberDouble + "";
            address = row.getCell(ADDRESS_COLUMN).getStringCellValue();
            organization = row.getCell(ORGANIZATION_COLUMN).getStringCellValue();
            project = row.getCell(PROJECT_COLUMN).getStringCellValue();

            if (row.getCell(PROJECT_DATE_COLUMN).getCellType() == CellType.STRING) {
                projectDate = row.getCell(PROJECT_DATE_COLUMN).getStringCellValue();
            } else {
                projectDate = "";
            }
        }
    }

    public String getRegion() {
        return region;
    }

    public String getNumber() {
        return number.replaceFirst("[.][^.]+$", "");
    }

    public String getAddress() {
        return address;
    }

    public String getOrganization() {
        if (organization.equals("")) {
            LogFile.addErrorRow("Отсутствует наименование организации в таблице Состояние ТМ");
        }
        return organization;
    }

    public String getProject() {
        if (project.equals("")) {
            LogFile.addErrorRow("Отсутствует шифр проекта в таблице Состояние ТМ");
        }
        return project;
    }

    public String getProjectDate() {
        if (projectDate.equals("")) {
            LogFile.addErrorRow("Отсутствует дата согласования проекта в таблице Состояние ТМ");
        }
        return projectDate;
    }

    public String getPassport() {
        return passport;
    }
}
