package org.openmuc.j60870.gui.substationBase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface IOSheet {
    default int counter(int typeColumnIndex, String type) {
        int count = 0;
        for (Row currentRow : this.getSheet()) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == typeColumnIndex) {
                    if (currentCell.getRowIndex() > 0) {
                        if (currentCell.getStringCellValue().contains(type)) {
                            count = count + 1;
                        }
                    }
                }
            }
        }
        return count;
    }
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
