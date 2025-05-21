package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;
import passport.astu.sources.db.tables.BoxesTable;
import passport.astu.sources.db.tables.DbTable;
import passport.astu.sources.db.tables.ModulesTable;

import java.util.List;


public class TMBoxTable {
    private final XWPFDocument document;
    private int countTMBoxes;
    private final TextEngine textEngine;
    private final SubstationDataServer substationDataServer;
    private XWPFTable table;
    private final DbTable modulesTable, boxesTable, controllerTable;

    public TMBoxTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        document.createParagraph().createRun().setText("Состав оборудования:");
        countTMBoxes = 0;
        modulesTable = substationDataServer.getModulesTable();
        boxesTable = substationDataServer.getBoxesTable();
        controllerTable = substationDataServer.getControllerTable();
        textEngine = new TextEngine();
    }

    public void createTable() {
        countTMBoxes += 1;
        document.createParagraph().createRun().setText("2.1." + countTMBoxes + "\tШкаф телемеханики №" + countTMBoxes + " зав. №" + getBoxSerialNumber());
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
        int newRow = 1;
        table.getRow(1).getCell(0).setText(numRow + 1 + "");
        table.getRow(1).getCell(2).setText(substationDataServer.getControllerFullName());
        table.getRow(1).getCell(3).setText(substationDataServer.getControllerName());
        table.getRow(1).getCell(4).setText(substationDataServer.getControllerSerialNumber());
        for (String s :
                modulesTable.getDataList(ModulesTable.PLACE_COLUMN_NAME)) {
            numRow += 1;
            if (s.equals("ШТМ") || s.equals("ШКТМ")) {
                if (newRow > 0) {
                    newRow += 1;
                    table.createRow().getCell(0).setText(numRow + 1 + "");
                } else {
                    newRow += 1;
                    table.getRow(newRow).getCell(0).setText(numRow + 1 + "");
                }
                table.getRow(newRow).getCell(1).setText("");
                table.getRow(newRow).getCell(2).setText(substationDataServer.getModulesFullNameList().get(numRow - 1));
                table.getRow(newRow).getCell(3).setText(substationDataServer.getModulesNameList().get(numRow - 1));
                table.getRow(newRow).getCell(4).setText(substationDataServer.getModulesSerialNumberList().get(numRow - 1));
            }
        }
    }

    private String getBoxSerialNumber() {
        List<String> boxesNameList = boxesTable.getDataList(BoxesTable.NAME_COLUMN_NAME);
        List<String> boxesSerialNumberList = boxesTable.getDataList(BoxesTable.SERIAL_NUMBER_COLUMN);
        for (int i = 0; i < boxesNameList.size(); i++) {
            if (boxesNameList.get(i).equals("ШТМ") || boxesNameList.get(i).equals("ШКТМ")) {
                return boxesSerialNumberList.get(i);
            }
        }
        return "";
    }
}
