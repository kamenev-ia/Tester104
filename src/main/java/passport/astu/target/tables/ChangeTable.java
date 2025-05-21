package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.TextEngine;

public class ChangeTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public ChangeTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("12.\tЛист регистрации изменений");
        XWPFTable table = document.createTable(2, 6);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Дата, страница");
        table.getRow(0).getCell(2).setText("Содержание изменения (дополнения)");
        table.getRow(0).getCell(3).setText("Обоснование");
        table.getRow(0).getCell(4).setText("Подпись лица, внесшего изменения (дополнения)");
        table.getRow(0).getCell(5).setText("Подпись начальника ОКиТ АСУ");
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
