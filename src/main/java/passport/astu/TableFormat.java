package passport.astu;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

public class TableFormat {

    public void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            CTHMerge hmerge = CTHMerge.Factory.newInstance();
            if (colIndex == fromCol) {
                hmerge.setVal(STMerge.RESTART);
            } else {
                hmerge.setVal(STMerge.CONTINUE);
            }
            XWPFTableCell cell = table.getRow(row).getCell(colIndex);
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setHMerge(hmerge);
            } else {
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setHMerge(hmerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    public void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int colIndex = fromRow; colIndex <= toRow; colIndex++) {
            CTVMerge hmerge = CTVMerge.Factory.newInstance();
            if (colIndex == fromRow) {
                hmerge.setVal(STMerge.RESTART);
            } else {
                hmerge.setVal(STMerge.CONTINUE);
            }
            for (int i = fromRow; i <= toRow; i++) {
                XWPFTableCell cell = table.getRow(i).getCell(col);
                CTTcPr tcPr = cell.getCTTc().getTcPr();
                if (tcPr != null) {
                    tcPr.setVMerge(hmerge);
                } else {
                    tcPr = CTTcPr.Factory.newInstance();
                    tcPr.setVMerge(hmerge);
                    cell.getCTTc().setTcPr(tcPr);
                }
            }
        }
    }
}
