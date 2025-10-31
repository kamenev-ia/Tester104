package org.openmuc.j60870.gui.commandShell;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class CommandExecutor {
    private volatile Process currentProcess = null;

    /**
     * Асинхронно выполняет команду через cmd.exe.
     *
     * @param command         Команда для выполнения (без приглашения).
     * @param outputConsumer  Колбэк для вывода (каждая строка выводится через этот consumer).
     * @param onFinish        Callback, вызываемый в конце выполнения (например, для вывода приглашения).
     */
    public void executeCommandAsync(String command, Consumer<String> outputConsumer, Runnable onFinish) {
        new Thread(() -> {
            try {
                // Запуск cmd.exe с параметром /c
                currentProcess = new ProcessBuilder("cmd.exe", "/c", command).start();

                // Чтение стандартного вывода
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(currentProcess.getInputStream(), "CP866"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputConsumer.accept(line + "\n");
                    }
                }

                // Чтение потока ошибок
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(currentProcess.getErrorStream(), "CP866"))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        outputConsumer.accept("Ошибка: " + line + "\n");
                    }
                }

                currentProcess.waitFor();
            } catch (IOException | InterruptedException exception) {
                outputConsumer.accept("Ошибка выполнения команды: " + exception.getMessage() + "\n");
                exception.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                currentProcess = null;
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        }).start();
    }

    /**
     * Прерывает выполнение текущей команды, если процесс запущен.
     *
     * @param outputConsumer Callback для вывода сообщения (например, "^C\n").
     */
    public void cancelRunningCommand(Consumer<String> outputConsumer) {
        if (currentProcess != null) {
            long pid = getPid(currentProcess);
            if (pid != -1) {
                try {
                    Process killer = new ProcessBuilder("taskkill", "/F", "/T", "/PID", Long.toString(pid)).start();
                    killer.waitFor();
                } catch (IOException | InterruptedException e) {
                    outputConsumer.accept("Ошибка прерывания команды: " + e.getMessage() + "\n");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            currentProcess = null;
        }
    }

    /**
     * Получает PID запущенного процесса.
     *
     * @param process Процесс, для которого требуется получить PID.
     * @return PID процесса или -1, если не удалось получить.
     */
    private long getPid(Process process) {
        long pid = -1;
        try {
            Field handleField = process.getClass().getDeclaredField("handle");
            handleField.setAccessible(true);
            long handle = handleField.getLong(process);
            pid = Kernel32.INSTANCE.GetProcessId(new WinNT.HANDLE(new Pointer(handle)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }
}
