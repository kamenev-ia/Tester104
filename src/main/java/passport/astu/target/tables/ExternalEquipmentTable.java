package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;
import passport.astu.sources.db.tables.DbTable;
import passport.astu.sources.db.tables.ModulesTable;

public class ExternalEquipmentTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;
    private final SubstationDataServer substationDataServer;
    private final DbTable modulesTable;
    private XWPFTable table;

    public ExternalEquipmentTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        modulesTable = substationDataServer.getModulesTable();
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("2.5.\tДополнительное оборудование");
        table = document.createTable(2, 6);
        table.getRow(0).getCell(0).setText("№");
        table.getRow(0).getCell(1).setText("Обозначение");
        table.getRow(0).getCell(2).setText("Наименование оборудования");
        table.getRow(0).getCell(3).setText("Тип, марка, артикул");
        table.getRow(0).getCell(4).setText("Серийный номер");
        table.getRow(0).getCell(5).setText("Место размещения");
        fillTable();
        textEngine.createParagraph(document, "", false, false, false, 14);
    }

    private void fillTable() {
        int numRow = 0;
        int newRow = 0;
        for (String s :
                modulesTable.getDataList(ModulesTable.PLACE_COLUMN_NAME)) {
            numRow += 1;
            if (!s.startsWith("яч.") && !s.startsWith("ШТМ") && !s.startsWith("ШКТМ")) {
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
                table.getRow(newRow).getCell(5).setText(modulesTable.getDataList(ModulesTable.PLACE_COLUMN_NAME).get(numRow - 1));
            }
        }
    }
}
