package passport.astu;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import passport.astu.sources.StmRow;
import passport.astu.sources.db.tables.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubstationDataServer {
    private final String substationFilePath;
    private final DbTable batteriesTable, baysTable, boxesTable, connectionTable, controllerTable;
    private final DbTable informationSecurityTable, measuringTransformersTable, metersTable, modulesTable;
    private final DbTable objectTable, outerSystemsTable, switchesTable;
    private final InstructionMap instructionMap;
    private String controllerName, substationNumber, substationType, voltage, controllerSerialNumber;
    private final String projectName, projectDate, projectOrganization;
    private List<String> modulesNameList, modulesFullNameList, modulesSerialNumberList;
    private final ModulesMap modulesMap;
    private final ControllersMap controllersMap;
    private final boolean isReserveChannelExist, isInformationSecurityExist;

    public SubstationDataServer(XSSFWorkbook workbook, String substationFilePath, StmRow stmRow) {

        this.substationFilePath = substationFilePath;

        instructionMap = new InstructionMap();
        modulesMap = new ModulesMap();
        controllersMap = new ControllersMap();


        batteriesTable = new DbTable(workbook, BatteriesTable.TABLE_NAME);
        baysTable = new DbTable(workbook, BaysTable.TABLE_NAME);
        boxesTable = new DbTable(workbook, BoxesTable.TABLE_NAME);
        connectionTable = new DbTable(workbook, ConnectionTable.TABLE_NAME);
        controllerTable = new DbTable(workbook, ControllerTable.TABLE_NAME);
        informationSecurityTable = new DbTable(workbook, InformationSecurityDbTable.TABLE_NAME);
        measuringTransformersTable = new DbTable(workbook, MeasuringTransformersTable.TABLE_NAME);
        metersTable = new DbTable(workbook, MetersTable.TABLE_NAME);
        modulesTable = new DbTable(workbook, ModulesTable.TABLE_NAME);
        objectTable = new DbTable(workbook, ObjectTable.TABLE_NAME);
        outerSystemsTable = new DbTable(workbook, OuterSystemsTable.TABLE_NAME);
        switchesTable = new DbTable(workbook, SwitchesTable.TABLE_NAME);

        if (controllerTable.isTableExist()) {
            List<String> dataList = controllerTable.getDataList(ControllerTable.CONTROLLER_NAME_COLUMN_NAME);
            if (!dataList.isEmpty()) {
                controllerName = dataList.get(0);
                controllerSerialNumber = controllerTable.getDataList(ControllerTable.SERIAL_NUMBER_COLUMN_NAME).get(0);
            } else {
                LogFile.addErrorRow("Не заполнено наименование контроллера в БД");
            }
        }
        if (objectTable.isTableExist()) {
            substationNumber = objectTable.getDataList(ObjectTable.NUMBER_COLUMN_NAME).get(0);
            substationType = objectTable.getDataList(ObjectTable.TYPE_COLUMN_NAME).get(0);
            voltage = objectTable.getDataList(ObjectTable.VOLTAGE_COLUMN_NAME).get(0);
        }

        if (modulesTable.isTableExist()) {
            modulesNameList = modulesTable.getDataList(ModulesTable.TYPE_COLUMN_NAME);
            modulesSerialNumberList = modulesTable.getDataList(ModulesTable.SERIAL_NUMBER_COLUMN_NAME);
            modulesFullNameList = createModulesFullNameList(modulesNameList);
        }

        projectName = stmRow.getProject();
        projectDate = stmRow.getProjectDate();
        projectOrganization = stmRow.getOrganization();

        isReserveChannelExist = isReserveChannel();
        isInformationSecurityExist = isInformationSecurity();
    }

    public String getVoltage() {
        return getStringBeforeLastPoint(voltage);
    }
    public String getSubstationNumber() {
        return getStringBeforeLastPoint(substationNumber);
    }
    public String getSubstationType() {
        return substationType;
    }
    public DbTable getModulesTable() {
        return modulesTable;
    }
    public DbTable getBoxesTable() {
        return boxesTable;
    }
    public String getProtocolKIName() {
        return getFileNameByKeyWord("Протокол КИ");
    }
    public String getProtocolKIDate() {
        return getDataInFileNameWithoutExt(getFileNameByKeyWord("Протокол КИ"));
    }
    public String getOnOrderName() {
        return getFileNameByKeyWord("Распоряжение о вводе");
    }
    public String getOnOrderDate() {
        return getDataInFileNameWithoutExt(getFileNameByKeyWord("Распоряжение о вводе"));
    }
    public String getProtocolKINumber() {
        return getSubstationNumber();
    }
    public DbTable getControllerTable() {
        return controllerTable;
    }
    public String getControllerName() {
        return controllerName;
    }
    public String getControllerFullName() {
        return controllersMap.getControllerFullName(controllerName);
    }
    public DbTable getInformationSecurityTable() {
        return informationSecurityTable;
    }
    public String getControllerSerialNumber() {
        return controllerSerialNumber;
    }
    public String getUserInstruction() {
        if (!(controllerName == null)) {
            return instructionMap.getInstruction(controllerName);
        }
        return "";
    }
    public String getTechnicalCard() {
        if (!(controllerName == null)) {
            return instructionMap.getTechCard(controllerName);
        }
        return "";
    }
    public List<String> getModulesNameList() {
        return modulesNameList;
    }
    public List<String> getModulesSerialNumberList() {
        return modulesSerialNumberList;
    }
    public List<String> getModulesFullNameList() {
        return modulesFullNameList;
    }
    public boolean isReserveChannelExist() {
        return isReserveChannelExist;
    }
    public boolean isInformationSecurityExist() {
        return isInformationSecurityExist;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getProjectDate() {
        return projectDate;
    }
    public String getProjectOrganization() {
        return projectOrganization;
    }

    private String getFileNameByKeyWord(String keyWord) {
        File mainDir = new File(substationFilePath);
        for (File subDir:
                Objects.requireNonNull(mainDir.listFiles())) {
            String docsName = subDir.getName();
            if (docsName.contains(keyWord)) {
                return getStringBeforeLastPoint(docsName);
            }
        }
        return "";
    }
    private String getStringBeforeLastPoint(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }
    private String getDataInFileNameWithoutExt(String fileName) {
        return (fileName.replaceFirst(".*от ", ""));
    }

    private List<String> createModulesFullNameList(List<String> modulesNameList) {
        List<String> modulesFullNameList = new ArrayList<>();
        for (String modulesName :
                modulesNameList) {
            modulesFullNameList.add(modulesMap.getModulesFullName(modulesName));
        }
        return modulesFullNameList;
    }
    private boolean isReserveChannel() {
        return connectionTable.getDataList(ConnectionTable.IP_COLUMN_NAME).size() > 1;
    }
    private boolean isInformationSecurity() {
        return !informationSecurityTable.getDataList(InformationSecurityDbTable.NAME_COLUMN_NAME).isEmpty();
    }
}
