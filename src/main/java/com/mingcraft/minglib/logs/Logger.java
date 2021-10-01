package com.mingcraft.minglib.logs;

import com.mingcraft.minglib.colors.Color;
import com.mingcraft.minglib.events.log.LoggerRegisteredEvent;
import com.mingcraft.minglib.events.log.LoggerUnregisteredEvent;
import com.mingcraft.minglib.exceptions.log.CallUnregisteredLoggerException;
import com.mingcraft.minglib.exceptions.log.LoggerRegisterFailedException;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Minecraft Server Logger <br>
 * <br>
 * Version 1.0.1 <br>
 * - Initialize logger <br>
 * <br>
 * @author Lede
 */
public class Logger {

    /**
     * Log type used in logger
     */
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

        LogType(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

    }

    private static final String ROOT_DIR = "log/";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private static final String LOG_FORMAT = "[%s] [%s] : %s\n";
    private static final ConsoleCommandSender sender = Bukkit.getConsoleSender();

    /**
     * Registered logger map
     */
    private static final Map<String, Logger> LOGGERS = new HashMap<>();

    /**
     * Logger key
     */
    private final String key;

    /**
     * Logger's log file
     */
    private final File file;

    /**
     * Whether to print the log to the console or not.
     */
    private boolean sendConsole;

    /**
     * Log file writer
     */
    private final BufferedWriter writer;

    private Logger() {
        this.key = null;
        this.file = null;
        this.sendConsole = false;
        this.writer = null;
    }

    /**
     * Create new logger with key and file path
     * @param key Logger key
     * @param path Log file path
     * @throws IOException Throw when file not create or not found
     */
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

    /**
     * Log message in logger's log file
     * @param logType Log level
     * @param message Log message
     */
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
        if (sendConsole) {
            sender.sendMessage(Color.colored(logType.color + log));
        }
    }

    /**
     * Sets whether the logger outputs logs to the console.
     * @param sendConsole true or false
     */
    public void setConsoleSender(boolean sendConsole) {
        this.sendConsole = sendConsole;
    }

    /**
     * Get registered logger <br>
     * If logger is unregistered. throw CallUnregisteredLoggerException <br>
     * @see CallUnregisteredLoggerException
     * @param key Logger key
     * @return Logger
     */
    public static Logger getLogger(@Nonnull final String key) {
        return getLogger(key, null);
    }

    /**
     * Get registered logger or new logger with key and log file path <br>
     * If path is null or already used. throw LoggerRegisterFailedException <br>
     * @see LoggerRegisterFailedException
     * @param key Logger key
     * @param path Log file path
     * @return Logger
     */
    public static Logger getLogger(@Nonnull final String key, @Nullable final String path) {
        if (!isRegistered(key)) {
            registerLogger(key, path);
        }
        return LOGGERS.get(key);
    }

    /**
     * Unregister the logger
     * @param key Logger key
     */
    public static void removeLogger(@Nonnull final String key) {
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
            Logger logger = new Logger(key, ROOT_DIR + path);
            LOGGERS.put(key, logger);
            callLoggerRegisteredEvent(logger.getKey(), logger);
        } catch (IOException e) {
            throw new LoggerRegisterFailedException("File does not exist.");
        }
    }

    private static boolean isRegistered(@Nonnull final String key) {
        return LOGGERS.containsKey(key);
    }

    private static boolean isLinkedFile(String path) {
        File file = new File(ROOT_DIR + path);
        for (Logger logger : LOGGERS.values()) {
            if (logger.file.equals(file)) {
                return true;
            }
        }
        return false;
    }

    private void disableLogger() {
        disableWriter();
        LOGGERS.remove(this.key);
        callLoggerUnregisteredEvent(this.key, this);
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

    public File getFile() {
        return file;
    }

    public String getKey() {
        return key;
    }

    public boolean isSendConsole() {
        return sendConsole;
    }

    private static void callLoggerRegisteredEvent(String key, Logger logger) {
        LoggerRegisteredEvent event = new LoggerRegisteredEvent(key, logger);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void callLoggerUnregisteredEvent(String key, Logger logger) {
        LoggerUnregisteredEvent event = new LoggerUnregisteredEvent(key, logger);
        Bukkit.getPluginManager().callEvent(event);
    }

}
