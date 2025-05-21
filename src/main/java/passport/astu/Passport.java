package passport.astu;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import passport.astu.sources.StmRow;
import passport.astu.target.tables.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Passport {
    private final SubstationDataServer substationDataServer;
    private final String filePath;

    public Passport(XSSFWorkbook workbook, File file, StmRow stmRow) {
        filePath = file.getParentFile().getPath();
        substationDataServer = new SubstationDataServer(workbook, filePath, stmRow);
        createPassport();
    }

    private void createPassport() {
        XWPFDocument doc = null;
        try {
            File wordFile = new File("C:/Users/kamen/OneDrive/Рабочий стол/PASSPORT ASTU/Тех Паспорт АСТУ Шаблон.docx");
            FileInputStream fis = new FileInputStream(wordFile);
            doc = new XWPFDocument(fis);

            String object = substationDataServer.getSubstationType() + " " + substationDataServer.getSubstationNumber();

            ProtocolTable pt = new ProtocolTable(doc, substationDataServer);
            pt.createText();
            pt.createTable();
            TMBoxTable tmbt = new TMBoxTable(doc, substationDataServer);
            tmbt.createTable();
            BaysEquipmentTable bet = new BaysEquipmentTable(doc, substationDataServer);
            bet.createTable();
            PowerSupplyTable pst = new PowerSupplyTable(doc);
            pst.createTable();
            ExternalEquipmentTable eet = new ExternalEquipmentTable(doc, substationDataServer);
            eet.createTable();
            ChangedEquipmentTable cet = new ChangedEquipmentTable(doc);
            cet.createTable();
            doc.createParagraph().createRun().setText("4.\tСведения об электропитании приведены в Инструкции по эксплуатации и обслуживанию комплекса телемеханики и связи " + substationDataServer.getUserInstruction());
            doc.createParagraph().createRun().setText("");
            InformationSecurityTable ist = new InformationSecurityTable(doc, substationDataServer);
            ist.createTable();
            ProgramTable prt = new ProgramTable(doc);
            prt.createTable();
            doc.createParagraph().createRun().setText("7.\tСведения о ТИ, ТС и ТУ передаваемых в диспетчерские центры «СО ЕЭС», «ЦУС ПАО «Россети Ленэнерго» указаны в формулярах согласования приема/передачи данных.");
            doc.createParagraph().createRun().setText("");
            ConnectionChannelTable cct = new ConnectionChannelTable(doc, substationDataServer);
            cct.createTable();
            TechServiceTable tst = new TechServiceTable(doc);
            tst.createTable();
            DefectTable dt = new DefectTable(doc);
            dt.createTable();
            DocumentationTable docTable = new DocumentationTable(doc, substationDataServer);
            docTable.createTable();
            ChangeTable ct = new ChangeTable(doc);
            ct.createTable();

            for (XWPFTable t : doc.getTables()) {
                for (XWPFTableRow r : t.getRows()) {
                    for (XWPFTableCell cell : r.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun run : p.getRuns()) {
                                run.setFontSize(11);
                                run.setFontFamily("TimesNewRoman");
                                String text = run.getText(0);
                                if (text != null && text.contains("year")) {
                                    text = "2024";
                                    run.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }

            for (XWPFParagraph p : doc.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        String text = run.getText(0);
                        if (text != null){
                            if (text.contains("year")) {
                                text = "2024";
                                run.setText(text, 0);
                            }
                            if (text.contains("object")) {
                                text = object;
                                run.setText(text, 0);
                            }
                            if (text.contains("namekts")) {
                                text = "Автоматизированная система управления " + object;
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (doc != null) {
                doc.write(Files.newOutputStream(new File(filePath + "/ТехПаспорт АСТУ " + substationDataServer.getSubstationNumber() + ".docx").toPath()));
                LogFile.addFinalRow(substationDataServer.getSubstationNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
