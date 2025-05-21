package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.SubstationDataServer;
import passport.astu.TableFormat;
import passport.astu.TextEngine;

public class ConnectionChannelTable {
    private final XWPFDocument document;
    private final SubstationDataServer substationDataServer;
    private final TextEngine textEngine;

    public ConnectionChannelTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        textEngine = new TextEngine();
    }
    public void createTable() {
        document.createParagraph().createRun().setText("8.\tСведения о каналах связи ТМ в направлении ДЦ «СО ЕЭС» и «ЦУС ПАО «Россети Ленэнерго» ");
        XWPFTable table = document.createTable(4, 7);
        table.getRow(0).getCell(0).setText("Тип канала");
        table.getRow(0).getCell(1).setText("РДУ");
        table.getRow(0).getCell(4).setText("ЦУС ПАО \"Россети Ленэнерго\"");
        table.getRow(1).getCell(1).setText("Наличие");
        table.getRow(1).getCell(2).setText("Тип канала");
        table.getRow(1).getCell(3).setText("Протокол передачи");
        table.getRow(1).getCell(4).setText("Наличие");
        table.getRow(1).getCell(5).setText("Тип канала");
        table.getRow(1).getCell(6).setText("Протокол передачи");
        table.getRow(2).getCell(0).setText("Основной КС");
        table.getRow(2).getCell(1).setText("-");
        table.getRow(2).getCell(2).setText("-");
        table.getRow(2).getCell(3).setText("-");
        table.getRow(2).getCell(4).setText("+");
        table.getRow(2).getCell(5).setText("GSM");
        table.getRow(2).getCell(6).setText("МЭК-60870-5-104");
        table.getRow(3).getCell(0).setText("Резервный КС");
        table.getRow(3).getCell(1).setText("-");
        table.getRow(3).getCell(2).setText("-");
        table.getRow(3).getCell(3).setText("-");
        if (substationDataServer.isReserveChannelExist()) {
            table.getRow(3).getCell(4).setText("+");
            table.getRow(3).getCell(5).setText("GSM");
            table.getRow(3).getCell(6).setText("МЭК-60870-5-104");
        } else {
            table.getRow(3).getCell(4).setText("-");
            table.getRow(3).getCell(5).setText("-");
            table.getRow(3).getCell(6).setText("-");
        }

        TableFormat tableFormat = new TableFormat();
        tableFormat.mergeCellHorizontally(table, 0, 1,3);
        tableFormat.mergeCellHorizontally(table, 0, 4,6);
        tableFormat.mergeCellVertically(table, 0, 0,1);
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
