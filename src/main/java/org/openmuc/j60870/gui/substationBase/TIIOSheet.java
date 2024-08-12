package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class TIIOSheet extends XSSFSheet implements IOSheet {
    private static final String SHEET_NAME = "ТИ";
    private static final int DEVICE_COLUMN_INDEX = 1;
    private static final int DEVICE_ADDRESS_COLUMN_INDEX = 2;
    private static final int CHANNEL_ADDRESS_COLUMN_INDEX = 3;
    private static final int UNIT_COLUMN_INDEX = 4;
    private static final int BAY_NAME_COLUMN_INDEX = 5;
    private static final int PARAMETER_COLUMN_INDEX = 6;
    private static final int NAME_COLUMN_INDEX = 7;
    private static final int IOA_COLUMN_INDEX = 8;
    private static final int ASDU_TYPE_COLUMN_INDEX = 9;
    private static final int MEASURE_UNIT_COLUMN_INDEX = 10;
    private static final int TRANSFORMATION_RATIO_COLUMN_INDEX = 11;
    private static final int APERTURE_COLUMN_INDEX = 12;
    private static final int CHECK_COLUMN_INDEX = 13;
    private final XSSFSheet sheet;
    private final int tiCount;

    public TIIOSheet(XSSFSheet sheet) {
        this.sheet = sheet;
        tiCount = sheet.getLastRowNum() - sheet.getFirstRowNum() - 1;
    }

    public int getTiCount() {
        return tiCount;
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
    public int getBayNameColumnIndex() {
        return BAY_NAME_COLUMN_INDEX;
    }
    public int getParameterColumnIndex() {
        return PARAMETER_COLUMN_INDEX;
    }
    public int getMeasureUnitColumnIndex() {
        return MEASURE_UNIT_COLUMN_INDEX;
    }
    public int getTransformationRatioColumnIndex() {
        return TRANSFORMATION_RATIO_COLUMN_INDEX;
    }
    public int getApertureColumnIndex() {
        return APERTURE_COLUMN_INDEX;
    }
}
