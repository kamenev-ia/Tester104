package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.TextEngine;

public class ProgramTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public ProgramTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("6.\tПеречень используемых в КТС программных средств.");
        XWPFTable table = document.createTable(2, 5);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Наименование");
        table.getRow(0).getCell(2).setText("Сведения из реестра российского ПО");
        table.getRow(0).getCell(3).setText("Место установки");
        table.getRow(0).getCell(4).setText("Лицензия/Электронный ключ");
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
