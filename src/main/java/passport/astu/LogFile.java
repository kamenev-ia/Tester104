package passport.astu;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LogFile {

    private static int errorCounter;
    private final static List<String> mainList = new ArrayList<>();

    public static void addEmptyRow() {
        mainList.add("");
    }

    public static void addRow(String text) {
        mainList.add(text);
    }
    public static void addFinalRow(String substationNumber) {
        if (errorCounter > 0) {
            mainList.add("Обнаружено ошибок: " + errorCounter + "!");
        } else {
            mainList.add("Паспорт АСТУ " + substationNumber + " успешно создан. Ошибок не обнаружено");
        }
        addEmptyRow();
    }

    public static void addErrorRow(String text) {
        mainList.add("! " + text);
        errorCounter += 1;
    }

    public static void resetErrorCounter() {
        errorCounter = 0;
    }

    public static void createLogFile(String pathToSave) {
        try {
            FileWriter fileWriter = new FileWriter(pathToSave + "\\log.txt");
            for (String s :
                    mainList) {
                fileWriter.write(s + System.getProperty("line.separator"));
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
