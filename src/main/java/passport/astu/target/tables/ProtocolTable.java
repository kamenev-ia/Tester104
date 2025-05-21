package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.*;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;

public class ProtocolTable {
    private final XWPFDocument document;
    private final SubstationDataServer substationDataServer;
    private final TextEngine textEngine;

    public ProtocolTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        textEngine = new TextEngine();
    }

    public void createText() {
        String text = "1.\tОбщие сведения о вводе КТС в работу:\n";
        textEngine.createParagraph(document, text, false, false, false, 10);
        String text1 = "1.1\tНаименование КТС: ";
        String text2 = "Автоматизированная система диспетчерского управления " + substationDataServer.getSubstationType() + " " + substationDataServer.getSubstationNumber();
        XWPFParagraph paragraph = document.createParagraph();
        textEngine.createRun(paragraph, text1, false, false, false, 10);
        textEngine.createRun(paragraph, text2, false, true, true, 10);

        String text3 = "1.2\tРабочая документация: ";
        String text4 = substationDataServer.getProjectName() + ", " + substationDataServer.getProjectOrganization() + ", " + substationDataServer.getProjectDate();
        XWPFParagraph paragraph1 = document.createParagraph();
        textEngine.createRun(paragraph1, text3, false, false, false, 10);
        textEngine.createRun(paragraph1, text4, false, true, true, 10);

//        text = "1.2\tРабочая документация: ";
//        textEngine.createParagraph(document, text, false, false, false, 10);
//        text = substationDataServer.getProjectName() + ", " + substationDataServer.getProjectOrganization() + ", " + substationDataServer.getProjectDate();
//        textEngine.createParagraph(document, text, false, true, true, 10);
        text = "(шифр, разработчик, даты разработки и согласования)";
        textEngine.createParagraph(document, text, false, true, false, 8);
        text = "1.3\tОрганизация, производившая наладку оборудования:";
        textEngine.createParagraph(document, text, false, false, false, 10);
        text = "1.4\tСрок эксплуатации КТС: 7 лет.";
        textEngine.createParagraph(document, text, false, false, false, 10);
        text = "1.5\tДата последней модернизации: ";
        textEngine.createParagraph(document, text, false, false, false, 10);
        textEngine.createParagraph(document, "", false, false, false, 14);
    }

    public void createTable(){
        XWPFTable table = document.createTable(3, 3);
        table.getRow(0).getCell(1).setText("Даты начала/окончания");
        table.getRow(0).getCell(2).setText("№, дата протокола (акта, распоряжения)");
        table.getRow(1).getCell(0).setText("Предварительные испытания (автономные или комплексные)");
        table.getRow(1).getCell(1).setText(substationDataServer.getProtocolKIDate());
        table.getRow(1).getCell(2).setText(substationDataServer.getProtocolKIName());
        table.getRow(2).getCell(0).setText("Ввод КТС в эксплуатацию");
        table.getRow(2).getCell(1).setText(substationDataServer.getOnOrderDate());
        table.getRow(2).getCell(2).setText(substationDataServer.getOnOrderName());
        textEngine.createParagraph(document, "", false, false, false, 14);
    }
}
