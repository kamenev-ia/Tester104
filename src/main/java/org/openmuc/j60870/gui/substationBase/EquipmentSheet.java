package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EquipmentSheet extends XSSFSheet {
    private static final String SHEET_NAME = "Оборудование";
    private static final String OBJECT_TABLE_NAME = "Объект";
    private static final String CONTROLLER_TABLE_NAME = "УСПД";
    private static final String CONNECTION_TABLE_NAME = "КаналыСвязи";
    private static final String INFORMATION_SECURITY_TABLE_NAME = "ИнформационнаяБезопасность";
    private static final String MODULES_TABLE_NAME = "МодулиТМ";
    private static final String OUTER_SYSTEMS_TABLE_NAME = "СторонниеСистемы";
    private static final String MEASURING_TRANSFORMERS_TABLE_NAME = "ИзмерительныеТрансформаторы";
    private static final String BAYS_TABLE_NAME = "Ячейки";
    private static final String UNITS_TABLE_NAME = "Шкафы";
    private static final String BATTERIES_TABLE_NAME = "АКБ";
    private static final String METERS_TABLE_NAME = "АИИСКУЭ";
    private static final String SWITCHES_TABLE_NAME = "Коммутаторы";
    private final XSSFTable objectTable, controllerTable, connectionTable, informationSecurityTable, modulesTable;
    private final XSSFTable outerSystemsTable, measuringTransformersTable, baysTable, unitsTable, batteriesTable;
    private final XSSFTable metersTable, switchesTable;
    private final XSSFSheet sheet;
    private final XSSFWorkbook workbook;

    public EquipmentSheet(XSSFSheet sheet) {
        this.sheet = sheet;
        this.workbook = sheet.getWorkbook();
        this.objectTable = workbook.getTable(OBJECT_TABLE_NAME);
        this.controllerTable = workbook.getTable(CONTROLLER_TABLE_NAME);
        this.connectionTable = workbook.getTable(CONNECTION_TABLE_NAME);
        this.informationSecurityTable = workbook.getTable(INFORMATION_SECURITY_TABLE_NAME);
        this.modulesTable = workbook.getTable(MODULES_TABLE_NAME);
        this.outerSystemsTable = workbook.getTable(OUTER_SYSTEMS_TABLE_NAME);
        this.measuringTransformersTable = workbook.getTable(MEASURING_TRANSFORMERS_TABLE_NAME);
        this.baysTable = workbook.getTable(BAYS_TABLE_NAME);
        this.unitsTable = workbook.getTable(UNITS_TABLE_NAME);
        this.batteriesTable = workbook.getTable(BATTERIES_TABLE_NAME);
        this.metersTable = workbook.getTable(METERS_TABLE_NAME);
        this.switchesTable = workbook.getTable(SWITCHES_TABLE_NAME);
    }

    public void getRowFromTable() {
        int startRow = objectTable.getStartRowIndex();
        int lastRow = objectTable.getEndRowIndex();

        for (int i = startRow + 1; i <= lastRow; i++){
            System.out.println(sheet.getRow(i).getCell(objectTable.findColumnIndex("РЭС")).getRichStringCellValue());
        }
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public String getDBSheetName() {
        return SHEET_NAME;
    }
}
