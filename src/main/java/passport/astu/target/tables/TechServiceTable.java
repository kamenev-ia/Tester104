package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.TextEngine;

public class TechServiceTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public TechServiceTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("9.\tСведения о произведенном техническом обслуживании КТС.");
        XWPFTable table = document.createTable(2, 5);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Дата");
        table.getRow(0).getCell(2).setText("Содержание работ (техкарта)");
        table.getRow(0).getCell(3).setText("Обоснование");
        table.getRow(0).getCell(4).setText("Ф.И.О. производителя работ");
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
