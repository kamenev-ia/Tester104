package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class TSIOSheet extends XSSFSheet implements IOSheet {
    private static final String SHEET_NAME = "ТС";
    private static final int DEVICE_COLUMN_INDEX = 1;
    private static final int DEVICE_ADDRESS_COLUMN_INDEX = 2;
    private static final int DI_COLUMN_INDEX = 3;
    private static final int CHANNEL_ADDRESS_COLUMN_INDEX = 4;
    private static final int UNIT_COLUMN_ADDRESS = 5;
    private static final int BAY_NAME_COLUMN_INDEX = 6;
    private static final int PARAMETER_COLUMN_INDEX = 7;
    private static final int PARAMETER_TYPE_COLUMN_INDEX = 8;
    private static final int NAME_COLUMN_INDEX = 9;
    private static final int IOA_COLUMN_INDEX = 10;
    private static final int ASDU_TYPE_COLUMN_INDEX = 11;
    private static final int INVERSION_COLUMN_INDEX = 12;
    private static final int CHECK_COLUMN_INDEX = 13;
    private final XSSFSheet sheet;

    public TSIOSheet(XSSFSheet sheet) {
        this.sheet = sheet;
    }

    private int tsCounter(String asduType) {
        int tsCount = 0;
        for (Row currentRow : this.sheet) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == ASDU_TYPE_COLUMN_INDEX) {
                    if (currentCell.getRowIndex() > 0) {
                        if (currentCell.getStringCellValue().equals(asduType)) {
                            tsCount = tsCount + 1;
                        }
                    }
                }
            }
        }
        return tsCount;
    }

    public int getSinglePointTSCount() {
        return tsCounter("SPI(1,30)");
    }

    public int getDoublePointTSCount() {
        return tsCounter("DPI(3,31)");
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
    public String getDBSheetName(){
        return SHEET_NAME;
    }
    public int getDiColumnIndex(){
        return DI_COLUMN_INDEX;
    }
    public int getUnitColumnAddress() {
        return UNIT_COLUMN_ADDRESS;
    }
    public int getBayNameColumnIndex() {
        return BAY_NAME_COLUMN_INDEX;
    }
    public int getParameterColumnIndex() {
        return PARAMETER_COLUMN_INDEX;
    }
    public int getParameterTypeColumnIndex() {
        return PARAMETER_TYPE_COLUMN_INDEX;
    }
    public int getInversionColumnIndex() {
        return INVERSION_COLUMN_INDEX;
    }
}
