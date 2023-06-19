package com.example.demo.service;

import com.example.demo.model.LogMessage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileUtils;




import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


public class LogService {

    public static List<LogMessage> getLogMessages() {
        List<LogMessage> logMessages = new ArrayList<>();

        // Get the root logger
        Logger rootLogger = LogManager.getRootLogger();

        // Retrieve all loggers including the root logger
        LoggerContext loggerContext =(LoggerContext) LogManager.getContext(false);
        ConfigurationFactory configurationFactory = ConfigurationFactory.getInstance();
        // Get the

         Configuration config = loggerContext.getConfiguration();
        Map<String, LoggerConfig> loggers = config.getLoggers();

        // Iterate over the loggers
        for (Map.Entry<String, LoggerConfig> entry : loggers.entrySet()) {
            LoggerConfig loggerConfig = entry.getValue();

            // Retrieve the logger's effective level
            Level loggerLevel = loggerConfig.getLevel();

            // Check if the logger level is sufficient to capture the log messages you want to monitor
            if (loggerLevel != null && loggerLevel.isMoreSpecificThan(Level.ERROR)) {
                // Retrieve the appender references associated with the logger
                AppenderRef[] appenderRefs = loggerConfig.getAppenderRefs().toArray(new AppenderRef[0]);

                // Iterate over the appender references
                for (AppenderRef appenderRef : appenderRefs) {
                    String appenderName = appenderRef.getRef();
                    Appender appender = config.getAppender(appenderName);

                    // Process the appender
                    if (appender instanceof FileAppender) {
                        FileAppender fileAppender = (FileAppender) appender;
                        String logFile = fileAppender.getFileName();

                        // Read log messages from the file
                        List<String> lines = readLogLines();

                        // Process log lines and convert them to LogMessage objects
                        for (String line : lines) {
                            LogMessage logMessage = parseLogLine(line);
                            if (logMessage != null) {
                                logMessages.add(logMessage);
                            }
                        }
                    }
                }
            }
        }

        return logMessages;

    }

    private static List<String> readLogLines() {
        List<String> lines = new ArrayList<>();

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = (Configuration) context.getExternalContext();
        Appender appender = configuration.getAppender("File"); // Assuming you have a FileAppender named "File"

        if (appender instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appender;
            String logFilePath = fileAppender.getFileName();
            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                // Handle any exception that occurred during reading the log file
                e.printStackTrace();
            }
        }

        return lines;
    }

    public static LogMessage parseLogLine(String logLine) {
        // Split the log line into different parts (assuming a specific log format)
        String[] parts = logLine.split("\\s+");

        // Extract relevant information from the log line
        String timestampString = parts[0] + " " + parts[1];
        String logLevel = parts[2];
        String message = parts[3];

        // Parse the timestamp
        LocalDateTime timestamp = parseTimestamp(timestampString);

        // Create the LogMessage object
        LogMessage logMessage = new LogMessage(timestamp, logLevel, message);

        return logMessage;
    }

    private static LocalDateTime parseTimestamp(String timestampString) {
        // Define the timestamp format based on your log format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Parse the timestamp string into LocalDateTime
        LocalDateTime timestamp = LocalDateTime.parse(timestampString, formatter);

        return timestamp;
    }



}
