package passport.astu.target.tables;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import passport.astu.SubstationDataServer;
import passport.astu.TextEngine;
import passport.astu.sources.db.tables.DbTable;
import passport.astu.sources.db.tables.InformationSecurityDbTable;

import java.util.List;

public class InformationSecurityTable {
    private final XWPFDocument document;
    private final SubstationDataServer substationDataServer;
    private final DbTable informationSecurityTable;
    private XWPFTable table;
    private final TextEngine textEngine;
    public InformationSecurityTable(XWPFDocument document, SubstationDataServer substationDataServer) {
        this.document = document;
        this.substationDataServer = substationDataServer;
        informationSecurityTable = substationDataServer.getInformationSecurityTable();
        textEngine = new TextEngine();
    }

    public void createTable() {
        if (substationDataServer.isInformationSecurityExist()) {
            document.createParagraph().createRun().setText("5.\tПеречень средств защиты информации, установленных КТС.");
            table = document.createTable(2, 6);
            table.getRow(0).getCell(0).setText("№");
            table.getRow(0).getCell(1).setText("Наименование и тип технического (программного) средства");
            table.getRow(0).getCell(2).setText("Заводской номер");
            table.getRow(0).getCell(3).setText("Сведения о сертификате");
            table.getRow(0).getCell(4).setText("Сведения из реестра российского ПО");
            table.getRow(0).getCell(5).setText("Место и дата установки");
            fillTable();
            textEngine.createParagraph(document, "", false, false, false, 14);
        }
    }

    private void fillTable() {
        List<String> infoSecNameDataList = informationSecurityTable.getDataList(InformationSecurityDbTable.NAME_COLUMN_NAME);
        List<String> infoSecSerialNumberDataList = informationSecurityTable.getDataList(InformationSecurityDbTable.SERIAL_NUMBER_COLUMN_NAME);
        List<String> infoSecTypeDataList = informationSecurityTable.getDataList(InformationSecurityDbTable.TYPE_COLUMN_NAME);
        table.getRow(1).getCell(0).setText("1");
        table.getRow(1).getCell(1).setText(infoSecNameDataList.get(0) + " " + infoSecTypeDataList.get(0));
        table.getRow(1).getCell(2).setText(infoSecSerialNumberDataList.get(0));
        for (int i = 1; i < infoSecNameDataList.size(); i++) {
            table.createRow().getCell(0).setText(i + 1 + "");
            table.getRow(i + 1).getCell(1).setText(infoSecNameDataList.get(i) + " " + infoSecTypeDataList.get(i));
            table.getRow(i + 1).getCell(2).setText(infoSecSerialNumberDataList.get(i));
        }
    }
}
