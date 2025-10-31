package org.openmuc.j60870.gui.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AppsLauncher {
    public enum App{
        NETSCAN("Netscan", "/apps/netscan/netscan.exe"),
        PUTTY("putty", "/apps/putty/putty.exe");

        private final String appName;
        private final String resourcePath;

        App(String appName, String resourcePath) {
            this.appName = appName;
            this.resourcePath = resourcePath;
        }

        public String getAppName() {
            return appName;
        }

        public String getResourcePath() {
            return resourcePath;
        }
    }

    public void launchApp(String resourcePath) {
        try {
            // Создание временного файла
            Path tempFile = Files.createTempFile("temp-", ".exe");

            // Копирование EXE из resources во временный файл
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IOException("Файл не найден в resources: " + resourcePath);
                }
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Установка прав на выполнение
            tempFile.toFile().setExecutable(true);

            // Запуск процесса
            ProcessBuilder pb = new ProcessBuilder(tempFile.toString());
            pb.start();

            // Удаление временного файла после завершения
            tempFile.toFile().deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
