package org.openmuc.j60870.gui.utilities;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileManager {
    public void downloadExcelFileTo(String sourceFile, Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документ Excel", "*.xlsm")
        );
        File destFile = fileChooser.showSaveDialog(parentWindow);
        if (destFile != null) {
            try (InputStream is = getClass().getResourceAsStream(sourceFile);
                 OutputStream os = Files.newOutputStream(destFile.toPath())) {
                if (is == null) {
                    throw new IllegalArgumentException("Resource not found: " + sourceFile);
                }
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Map<String, String>> getResourceStructure(String resourceRoot) {
        Map<String, Map<String, String>> structure = new LinkedHashMap<>();
        try {
            URL resourceUrl = getClass().getClassLoader().getResource(resourceRoot);
            if (resourceUrl == null) {
                System.err.println("Ресурс не найден: " + resourceRoot);
                return structure;
            }

            if (resourceUrl.getProtocol().equals("jar")) {
                // Работаем с JAR (Java 8 compatible)
                String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(resourceRoot) && !entry.isDirectory()) {
                            String[] parts = name.substring(resourceRoot.length()).split("/");
                            if (parts.length >= 1) {
                                String folder = parts.length > 1 ? parts[0] : "";
                                String file = parts[parts.length - 1];
                                structure.computeIfAbsent(folder, k -> new LinkedHashMap<>())
                                        .put(file, name);
                            }
                        }
                    }
                }
            } else {
                // Работаем из IDE (файловая система)
                Path resourcePath = Paths.get(resourceUrl.toURI());
                Files.walk(resourcePath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            Path relativePath = resourcePath.relativize(file);
                            if (relativePath.getNameCount() >= 1) {
                                String folder = relativePath.getNameCount() > 1 ?
                                        relativePath.getParent().toString() : "";
                                String fileName = relativePath.getFileName().toString();
                                structure.computeIfAbsent(folder, k -> new LinkedHashMap<>())
                                        .put(fileName, resourceRoot + relativePath.toString());
                            }
                        });
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return structure;
    }

    public void handleResourceSelection(String resourcePath) {
        try {
            // Получаем расширение файла из оригинального пути
            String fileExtension = "";
            int lastDotIndex = resourcePath.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = resourcePath.substring(lastDotIndex);
            }

            // Создаем временный файл с оригинальным расширением
            Path tempFile = Files.createTempFile("temp_", fileExtension);

            // Копируем ресурс
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (is != null) {
                    Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

                    // Открываем файл (для Windows)
                    new ProcessBuilder("cmd", "/c", "start", "\"\"", "\"" + tempFile.toString() + "\"").start();

                    // Удаляем после завершения
                    tempFile.toFile().deleteOnExit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Здесь можно добавить логирование ошибки
        }
    }
}
