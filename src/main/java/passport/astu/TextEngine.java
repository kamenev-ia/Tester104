package passport.astu;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class TextEngine {

    public void createParagraph(XWPFDocument document, String text, boolean isBold, boolean isItalic, boolean isUnderline, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setItalic(isItalic);
        if (isUnderline) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
        run.setFontSize(fontSize);
    }

    public XWPFRun createRun(XWPFParagraph paragraph, String text, boolean isBold, boolean isItalic, boolean isUnderline, int fontSize) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setItalic(isItalic);
        if (isUnderline) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
        run.setFontSize(fontSize);
        return run;
    }

    public void createMultiParagraph(XWPFDocument document, String text, String formattingText, boolean isBold, boolean isItalic, boolean isUnderline, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        XWPFRun formattingRun = paragraph.createRun();
        formattingRun.setText(formattingText);
        formattingRun.setBold(isBold);
        formattingRun.setItalic(isItalic);
        if (isUnderline) {
            formattingRun.setUnderline(UnderlinePatterns.SINGLE);
        }
        formattingRun.setFontSize(fontSize);
    }
}
