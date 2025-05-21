package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.*;
import passport.astu.TextEngine;

public class PowerSupplyTable {
    private final XWPFDocument document;
    private final TextEngine textEngine;

    public PowerSupplyTable(XWPFDocument document) {
        this.document = document;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("2.4.\tШкаф гарантированного питания");
        String text = "Бесперебойное питание организовано в составе ШТМ";
        textEngine.createParagraph(document, text, false, true, true, 10);
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
