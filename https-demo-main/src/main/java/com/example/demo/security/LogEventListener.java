package com.example.demo.security;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
//@Component
@Plugin(name = "LogEventListener", category = "Core", elementType = "appender", printObject = true)
public final class LogEventListener extends AbstractAppender {

    private LogEventListener(String name) {

        //Layout<?> layout = PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build()
        super(name, null, PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build(), false);
    }

    @PluginFactory
    public static LogEventListener createAppender(@PluginAttribute("name") String name) {
        if (name == null) {
            LOGGER.error("No name provided for LogEventListener");
            return null;
        }

        Layout<?> layout = PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build();
        return new LogEventListener(name);
    }





    @Override
    public void append(LogEvent logEvent) {
        Level logLevel = logEvent.getLevel();
        System.out.println("eeeeeeeeeeeeee"+logLevel);
        System.out.println(logEvent.getMessage().getFormattedMessage()+"dobije porukuuuuuuuuuuuuuuu");

    }





    @Override
    public boolean ignoreExceptions() {
        return false;
    }

    @Override
    public ErrorHandler getHandler() {
        return null;
    }

    @Override
    public void setHandler(ErrorHandler errorHandler) {

    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
