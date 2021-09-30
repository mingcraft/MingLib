package com.mingcraft.minglib.logs;

import com.mingcraft.minglib.colors.Color;
import com.mingcraft.minglib.exceptions.log.CallUnregisteredLoggerException;
import com.mingcraft.minglib.exceptions.log.LoggerRegisterFailedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Logger {

    @Getter
    @AllArgsConstructor
    public enum LogType {

        ERROR(Color.LogColor.ERROR),
        WARNING(Color.LogColor.WARNING),
        ENABLE(Color.LogColor.ENABLE),
        DISABLE(Color.LogColor.DISABLE),
        LOAD(Color.LogColor.LOAD),
        UNLOAD(Color.LogColor.UNLOAD),
        SAVE(Color.LogColor.SAVE),
        DELETE(Color.LogColor.DELETE),
        MESSAGE(Color.LogColor.MESSAGE),
        INFO(Color.LogColor.INFO);

        private final String color;

    }

    private static final Map<String, Logger> LOGGERS = new HashMap<>();

    private static final String ROOT_DIR = "log/";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private static final String LOG_FORMAT = "[%s] [%s] : %s\n";

    private static final ConsoleCommandSender sender = Bukkit.getConsoleSender();

    private final String key;
    private final File file;
    private boolean sendConsole;

    private final BufferedWriter writer;

    private Logger() {
        this.key = null;
        this.file = null;
        this.sendConsole = false;
        this.writer = null;
    }

    private Logger(String key, String path) throws IOException {
        this.key = key;
        this.file = new File(path);
        this.sendConsole = false;

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file, true)));
    }

    public void log(LogType logType, String message) {
        LocalDateTime time = LocalDateTime.now();
        String log = String.format(LOG_FORMAT, TIME_FORMAT.format(time), logType.toString(), message);
        try {
            if (writer != null)
                writer.write(log);
                writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isSendConsole()) {
            sender.sendMessage(Color.colored(logType.color + log));
        }
    }

    public void setConsoleSender(boolean sendConsole) {
        this.sendConsole = sendConsole;
    }

    public static void uploadMongo(String key) {
        if (isRegistered(key)) {
            getLogger(key).upload();
        }
        else {
            throw new CallUnregisteredLoggerException("File cannot be uploaded to MongoDB server, Logger is not registered.");
        }
    }

    public static Logger getLogger(@NonNull final String key) {
        return getLogger(key, null);
    }

    public static Logger getLogger(@NonNull final String key, @Nullable final String path) {
        if (!isRegistered(key)) {
            registerLogger(key, path);
            return LOGGERS.get(key);
        }
        return LOGGERS.get(key);
    }

    public static void removeLogger(@NonNull final String key) {
        if (isRegistered(key)) {
            getLogger(key).disableLogger();
        }
        else {
            throw new CallUnregisteredLoggerException("Cannot be deleted, It is an unregistered logger.");
        }
    }

    private static void registerLogger(final String key, final String path){
        if (path == null) {
            throw new LoggerRegisterFailedException("You did not enter the path of the log file.");
        }
        if (isLinkedFile(path)) {
            throw new LoggerRegisterFailedException("File already linked to another logger.");
        }
        try {
            LOGGERS.put(key, new Logger(key, ROOT_DIR + path));
        } catch (IOException e) {
            throw new LoggerRegisterFailedException("File does not exist.");
        }
    }

    private static boolean isRegistered(@NonNull final String key) {
        return LOGGERS.containsKey(key);
    }

    private static boolean isLinkedFile(String path) {
        File file = new File(ROOT_DIR + path);
        for (Logger logger : LOGGERS.values()) {
            if (logger.getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }

    private void upload() {
        //TODO MongoDB Upload Method 작성할 것
    }

    private void disableLogger() {
        disableWriter();
        LOGGERS.remove(this.key);
    }

    private void disableWriter() {
        try {
            if (writer != null) {
                this.writer.flush();
                this.writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
