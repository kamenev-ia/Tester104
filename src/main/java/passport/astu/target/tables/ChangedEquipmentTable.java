package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.TextEngine;

public class ChangedEquipmentTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public ChangedEquipmentTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("3.\tВ ходе эксплуатации были выполнены следующие замены оборудования");
        XWPFTable table = document.createTable(2, 8);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Дата");
        table.getRow(0).getCell(2).setText("Марка, наименование демонтированного оборудования");
        table.getRow(0).getCell(3).setText("Заводской номер");
        table.getRow(0).getCell(4).setText("Марка, наименование установленного оборудования");
        table.getRow(0).getCell(5).setText("Заводской номер");
        table.getRow(0).getCell(6).setText("Причина замены");
        table.getRow(0).getCell(7).setText("Ф.И.О. производителя работ");
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
