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
import java.time.LocalDateTime;
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
    private static final String LOG_FORMAT = "[%s] [%s] : %s\n";

    private static final ConsoleCommandSender sender = Bukkit.getConsoleSender();

    private final String key;
    private final String path;
    private final File file;
    private boolean sendConsole;

    private final BufferedWriter writer;

    private Logger() {
        this.key = null;
        this.path = null;
        this.file = null;
        this.sendConsole = false;
        this.writer = null;
    }

    private Logger(String key, String path) throws FileNotFoundException {
        this.key = key;
        this.path = path;

        this.file = new File(this.path);
        if (!this.file.exists()) {
            this.file.mkdirs();
        }

        this.sendConsole = false;
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file)));
    }

    public void log(LogType logType, String message) {
        String log = String.format(LOG_FORMAT, logType.toString(), LocalDateTime.now(), message);
        try {
            if (writer != null)
                writer.write(log);
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
            throw new CallUnregisteredLoggerException("로거가 등록되지 않아 Mongo 서버에 업로드할 수 없습니다.");
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
            throw new CallUnregisteredLoggerException("등록되지 않은 로거입니다.");
        }
    }

    private static void registerLogger(final String key, final String path){
        if (path == null) {
            throw new LoggerRegisterFailedException("로그를 작성할 파일의 경로를 찾을 수 없습니다.");
        }
        if (isLinkedFile(path)) {
            throw new LoggerRegisterFailedException("다른 로거와 연결된 파일이므로 연결할 수 없습니다.");
        }
        try {
            LOGGERS.put(key, new Logger(key, ROOT_DIR + path));
        } catch (FileNotFoundException e) {
            throw new LoggerRegisterFailedException("입력한 경로의 파일을 찾을 수 없습니다.");
        }
    }

    private static boolean isRegistered(@NonNull final String key) {
        return LOGGERS.containsKey(key);
    }

    private static boolean isLinkedFile(String path) {
        String logFilePath = ROOT_DIR + path;
        for (Logger logger : LOGGERS.values()) {
            if (logger.getPath().equals(logFilePath)) {
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
