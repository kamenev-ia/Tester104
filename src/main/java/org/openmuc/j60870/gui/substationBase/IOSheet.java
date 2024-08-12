package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface IOSheet {
    XSSFSheet getSheet();
    int getDeviceColumnIndex();
    int getDeviceAddressColumnIndex();
    int getChannelAddressColumnIndex();
    int getNameColumnIndex();
    int getIOAColumnIndex();
    int getASDUTypeColumnIndex();
    int getCheckColumnIndex();
    String getDBSheetName();
}
