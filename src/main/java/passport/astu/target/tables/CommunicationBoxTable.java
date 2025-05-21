package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

public class CommunicationBoxTable {
    private final XWPFDocument document;
    private int countCommunicationBoxes;

    public CommunicationBoxTable(XWPFDocument document) {
        this.document = document;
        countCommunicationBoxes = 0;
    }

    public void createTable() {
        countCommunicationBoxes += 1;
        document.createParagraph().createRun().setText("\n2.2." + countCommunicationBoxes + "\tШкаф коммуникационный №" + countCommunicationBoxes + " зав.№");
        XWPFTable table = document.createTable(2, 5);
        table.getRow(0).getCell(0).setText("№");
        table.getRow(0).getCell(1).setText("Обозначение");
        table.getRow(0).getCell(2).setText("Наименование оборудования");
        table.getRow(0).getCell(3).setText("Тип, марка, артикул");
        table.getRow(0).getCell(4).setText("Серийный номер");
    }
}
