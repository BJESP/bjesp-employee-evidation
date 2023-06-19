package com.example.demo.model;

import java.time.LocalDateTime;

public class LogMessage {
    private LocalDateTime timeStamp;
    private String logLevel;
    private String message;

    public LogMessage(LocalDateTime timeStamp, String logLevel, String message) {
        this.timeStamp = timeStamp;
        this.logLevel = logLevel;
        this.message = message;
    }

    public LogMessage() {
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
