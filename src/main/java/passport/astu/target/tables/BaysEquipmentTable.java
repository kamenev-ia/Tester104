package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.*;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;
import passport.astu.sources.db.tables.DbTable;
import passport.astu.sources.db.tables.ModulesTable;

public class BaysEquipmentTable {
    private final XWPFDocument document;
    private XWPFTable table;
    private final DbTable modulesTable;
    private final SubstationDataServer substationDataServer;
    private final TextEngine textEngine;

    public BaysEquipmentTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        modulesTable = substationDataServer.getModulesTable();
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("2.3.\tОборудование КТС ячеек " + substationDataServer.getVoltage() + " кВ");
        table = document.createTable(2, 5);
        table.getRow(0).getCell(0).setText("№");
        table.getRow(0).getCell(1).setText("Обозначение");
        table.getRow(0).getCell(2).setText("Наименование оборудования");
        table.getRow(0).getCell(3).setText("Тип, марка, артикул");
        table.getRow(0).getCell(4).setText("Серийный номер");
        fillTable();
        textEngine.createParagraph(document, "", false, false, false, 14);
    }

    private void fillTable() {
        int numRow = 0;
        int newRow = 0;
        for (String s :
                modulesTable.getDataList(ModulesTable.PLACE_COLUMN_NAME)) {
            numRow += 1;
            if (s.startsWith("яч.")) {
                if (newRow > 0) {
                    newRow += 1;
                    table.createRow().getCell(0).setText(numRow + "");
                } else {
                    newRow += 1;
                    table.getRow(newRow).getCell(0).setText(numRow + "");
                }
                table.getRow(newRow).getCell(1).setText("");
                table.getRow(newRow).getCell(2).setText(substationDataServer.getModulesFullNameList().get(numRow - 1));
                table.getRow(newRow).getCell(3).setText(substationDataServer.getModulesNameList().get(numRow - 1));
                table.getRow(newRow).getCell(4).setText(substationDataServer.getModulesSerialNumberList().get(numRow - 1));
            }
        }
    }
}
