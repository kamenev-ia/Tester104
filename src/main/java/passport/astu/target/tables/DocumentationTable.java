package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;

public class DocumentationTable {
    private final XWPFDocument document;
    private final SubstationDataServer substationDataServer;
    private final TextEngine textEngine;

    public DocumentationTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        textEngine = new TextEngine();
    }

    public void createTable() {
        document.createParagraph().createRun().setText("11.\tПеречень эксплуатационной документации КТС");
        XWPFTable table = document.createTable(6, 2);
        table.getRow(0).getCell(0).setText("№ п/п");
        table.getRow(0).getCell(1).setText("Наименование");
        table.getRow(1).getCell(0).setText("1");
        table.getRow(1).getCell(1).setText("Инструкция по эксплуатации и обслуживанию КТС " + substationDataServer.getUserInstruction());
        table.getRow(2).getCell(0).setText("2");
        table.getRow(2).getCell(1).setText("Протокол наладки КТС № " + substationDataServer.getProtocolKINumber() + " от " + substationDataServer.getProtocolKIDate());
        table.getRow(3).getCell(0).setText("3");
        table.getRow(3).getCell(1).setText("Протокол предварительных испытаний КТС № " + substationDataServer.getProtocolKINumber() + " от " + substationDataServer.getProtocolKIDate());
        table.getRow(4).getCell(0).setText("4");
        table.getRow(4).getCell(1).setText("Формуляры приема/передачи данных в ДП");
        table.getRow(5).getCell(0).setText("5");
        table.getRow(5).getCell(1).setText("Технологическая карта обслуживания оборудования КТС " + substationDataServer.getTechnicalCard());
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
