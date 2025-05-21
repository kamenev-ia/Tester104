package org.openmuc.j60870.gui.utilities.pdfReader;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFTest {
    public static void main(String[] args) {

        String userHome = System.getProperty("user.home");

        // Составляем путь к рабочему столу (для Windows рабочий стол обычно называется "Desktop")
        String desktopPath = userHome + File.separator + "OneDrive" + File.separator + "Рабочий стол";

        // Имя файла на рабочем столе, например "document.doc"
        String fileName = "БД1.pdf";

        // Создаём объект File, представляющий документ на рабочем столе
        File sourceDocument = new File(desktopPath, fileName);

        try (PDDocument document = PDDocument.load(sourceDocument)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("rus");

            // Для повышения качества распознавания можно повысить DPI при рендеринге страницы
            StringBuilder ocrResult = new StringBuilder();
            int pageCount = document.getNumberOfPages();

            // Если документ многостраничный, можно обрабатывать все страницы или только нужные (например, первую)
            for (int page = 0; page < pageCount; page++) {
                // Рендерим страницу в изображение с разрешением 300 dpi
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);

                // Опционально – сохранить изображение для отладки:
                // ImageIO.write(image, "png", new File("page_" + page + ".png"));

                // Выполняем OCR для текущего изображения
                String pageText = tesseract.doOCR(image);
                ocrResult.append(pageText).append("\n");
            }

            // Получаем весь распознанный текст из документа
            String text = ocrResult.toString();
            System.out.println("Распознанный текст:\n" + text);

            // Используем регулярное выражение для поиска строки, содержащей "Номер объекта:" с последующими цифрами
            Pattern pattern = Pattern.compile("Объект.*?(\\d{1,5})(?!\\d)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String objectNumber = matcher.group(1);
                System.out.println("Найден номер объекта: " + objectNumber);
            } else {
                System.out.println("Номер объекта не найден в документе.");
            }

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке PDF: " + e.getMessage());
            e.printStackTrace();
        } catch (TesseractException e) {
            System.err.println("Ошибка при выполнении OCR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
