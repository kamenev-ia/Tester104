package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class TUIOSheet extends XSSFSheet implements IOSheet {
    private static final String SHEET_NAME = "ТУ";
    private static final int DEVICE_COLUMN_INDEX = 1;
    private static final int DEVICE_ADDRESS_COLUMN_INDEX = 2;
    private static final int CHANNEL_ADDRESS_COLUMN_INDEX = 3;
    private static final int UNIT_COLUMN_INDEX = 4;
    private static final int CONTROL_TYPE_COLUMN_INDEX = 5;
    private static final int NAME_COLUMN_INDEX = 6;
    private static final int IOA_COLUMN_INDEX = 7;
    private static final int ASDU_TYPE_COLUMN_INDEX = 8;
    private static final int CHECK_COLUMN_INDEX = 9;
    private final XSSFSheet sheet;

    public TUIOSheet(XSSFSheet sheet) {
        this.sheet = sheet;
    }

    private int tuCounter(String controlType) {
        return counter(CONTROL_TYPE_COLUMN_INDEX, controlType);
    }

    public int getCircuitBreakerControlCount() {
        return tuCounter("ТУ В");
    }
    public int getConfirmationControlCount() {
        return tuCounter("Квитирование");
    }
    public int getTestControlCount() {
        return tuCounter("Тестирование");
    }
    public int getAvrControlCount() {
        return tuCounter("Вывод АВР");
    }

    @Override
    public XSSFSheet getSheet() {
        return sheet;
    }
    @Override
    public int getDeviceColumnIndex(){
        return DEVICE_COLUMN_INDEX;
    }
    @Override
    public int getDeviceAddressColumnIndex(){
        return DEVICE_ADDRESS_COLUMN_INDEX;
    }
    @Override
    public int getChannelAddressColumnIndex(){
        return CHANNEL_ADDRESS_COLUMN_INDEX;
    }
    @Override
    public int getNameColumnIndex() {
        return NAME_COLUMN_INDEX;
    }
    @Override
    public int getIOAColumnIndex() {
        return IOA_COLUMN_INDEX;
    }
    @Override
    public int getASDUTypeColumnIndex() {
        return ASDU_TYPE_COLUMN_INDEX;
    }
    @Override
    public int getCheckColumnIndex() {
        return CHECK_COLUMN_INDEX;
    }
    @Override
    public String getDBSheetName() {
        return SHEET_NAME;
    }

    public int getUnitColumnIndex() {
        return UNIT_COLUMN_INDEX;
    }
    public int getControlTypeColumnIndex() {
        return CONTROL_TYPE_COLUMN_INDEX;
    }
}
