package com.example.demo.security;


import com.example.demo.service.EmailService;
import com.example.demo.service.MessageService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Plugin(name = "LogEventListener", category = "Core", elementType = "appender", printObject = true)
public final class LogEventListener extends AbstractAppender implements ApplicationContextAware {


    private  static MessageService messageService;
    //private static SecurityLogger securityLogger;
    private  static EmailService emailService;
    private SimpMessagingTemplate messagingTemplate;
    protected LogEventListener() {

        //Layout<?> layout = PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build()
        super("LogEventListener", null, PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build(), false);
    }
//@PluginAttribute("name")
    @PluginFactory
    public static LogEventListener createAppender(@PluginAttribute("name") String name,
                                                  @PluginConfiguration Configuration configuration) {
        /*if (messageService == null) {
            LOGGER.error("No name provided for Messageeeeeeeeeeeee SErviceeeeeeeeeeeee");
            return null;
        }*/




        Layout<?> layout = PatternLayout.newBuilder().withPattern("%d{dd.MM.yyyy. HH:mm:ss}|%-5p|%-20c{1}|%m%n|").build();
        LogEventListener logEventListener = new LogEventListener();
        logEventListener.setMessageService(messageService);
        return logEventListener;
    }


    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void append(LogEvent logEvent) {
        Level logLevel = logEvent.getLevel();
        if (logLevel == Level.ERROR){
            //messagingTemplate.convertAndSend("/topic/message",  logEvent.getMessage().getFormattedMessage());
            messageService.sendMessage(logEvent.getMessage().getFormattedMessage());
            emailService.sendWarningEmail("cvetanaciki555@gmail.com",logEvent.getMessage().getFormattedMessage());
        }

    }





    @Override
    public boolean ignoreExceptions() {
        return false;
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext.getAutowireCapableBeanFactory().getBean("messageService") != null) {
            messageService = (MessageService) applicationContext.getAutowireCapableBeanFactory().getBean("messageService");
        }
        if(applicationContext.getAutowireCapableBeanFactory().getBean("emailService") != null){
            emailService = (EmailService) applicationContext.getAutowireCapableBeanFactory().getBean("emailService");
        }
    }
}
