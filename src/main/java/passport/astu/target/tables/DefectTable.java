package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.TextEngine;

public class DefectTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public DefectTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("10.\tСведения о неисправностях КТС");
        XWPFTable table = document.createTable(2, 6);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Дата");
        table.getRow(0).getCell(2).setText("№ заявки в Service Desk (при наличии)");
        table.getRow(0).getCell(3).setText("Краткое описание неисправности");
        table.getRow(0).getCell(4).setText("Отметка об устранении неисправности");
        table.getRow(0).getCell(5).setText("Ф.И.О. производителя работ");
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
