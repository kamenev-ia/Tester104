package org.openmuc.j60870.gui.utilities.pdfReader;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

    public class TableExtractor {

        public static void main(String[] args) {

            String userHome = System.getProperty("user.home");

            // Составляем путь к рабочему столу (для Windows рабочий стол обычно называется "Desktop")
            String desktopPath = userHome + File.separator + "OneDrive" + File.separator + "Рабочий стол";

            // Имя файла на рабочем столе, например "document.doc"
            String fileName = "БД1.pdf";
            File sourceDocument = new File(desktopPath, fileName);

            // Путь к tessdata (если требуется явное задание – например, "C:\\Program Files\\Tesseract-OCR\\tessdata")
            String tessDataPath = "C:\\Program Files\\Tesseract-OCR\\tessdata";

            // Язык распознавания (например, "rus" для русского или "eng" для английского)
            String language = "rus";  // поменяйте по необходимости

            PDDocument document = null;
            try {
                // Загружаем PDF документ
                document = PDDocument.load(sourceDocument);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                // Создаем инстанс Tesseract через Tess4J
                ITesseract tesseract = new Tesseract();
                tesseract.setDatapath(tessDataPath);
                tesseract.setLanguage(language);

                // Проходим по всем страницам документа
                int numberOfPages = document.getNumberOfPages();
                for (int page = 0; page < numberOfPages; page++) {
                    // Рендерим страницу в изображение; 300 DPI – хорошее качество для OCR
                    BufferedImage pageImage = pdfRenderer.renderImageWithDPI(page, 500);

                    // Выполняем OCR для полученного изображения
                    String ocrResult = tesseract.doOCR(pageImage);

                    // Выводим номер страницы
                    System.out.println("=== Страница " + (page + 1) + " ===");

                    // Разбиваем результат по строкам (учитывая возможные переводы строк)
                    String[] lines = ocrResult.split("\\r?\\n");
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            // Выводим строку (предполагается, что каждая строка соответствует строке таблицы)
                            System.out.println(line);
                        }
                    }
                    System.out.println();  // пустая строка между страницами
                }
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке PDF документа: " + e.getMessage());
                e.printStackTrace();
            } catch (TesseractException te) {
                System.err.println("Ошибка при выполнении OCR: " + te.getMessage());
                te.printStackTrace();
            } finally {
                if (document != null) {
                    try {
                        document.close();
                    } catch (IOException e) {
                        System.err.println("Ошибка при закрытии PDF документа: " + e.getMessage());
                    }
                }
            }
        }
    }
