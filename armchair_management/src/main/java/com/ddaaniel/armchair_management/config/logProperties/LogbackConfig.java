package com.ddaaniel.armchair_management.config.logProperties;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LogbackConfig {

//    private final LoggingProperties loggingProperties;

//    public LogbackConfig(LoggingProperties loggingProperties) {
//        this.loggingProperties = loggingProperties;
//    }

    @PostConstruct
    public void setupLogging() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        // NÃO use context.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{HH:mm:ss}  =Backend-API  level=%level  thread_name=%thread  logger=%logger{36}  message=%msg%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(consoleAppender);
    }


//    @PostConstruct
//    public void setupLogging() {
//        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        context.reset();
//
//        // === APP LOG ===
//        RollingFileAppender<ILoggingEvent> appAppender = new RollingFileAppender<>();
//        appAppender.setContext(context);
//        //appAppender.setFile(loggingProperties.getApp().getPath());
//
//        TimeBasedRollingPolicy<ILoggingEvent> appPolicy = new TimeBasedRollingPolicy<>();
//        appPolicy.setContext(context);
//        appPolicy.setParent(appAppender);
////        appPolicy.setFileNamePattern(loggingProperties.getApp().getPattern());
////        appPolicy.setMaxHistory(loggingProperties.getApp().getMaxHistory());
//        appPolicy.start();
//
//        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
//        encoder.setContext(context);
//        encoder.setPattern("%d{HH:mm:ss}  =Backend-API  level=%level  thread_name=%thread  logger=%logger{36}  message=%msg%n");
//        encoder.start();
//
////        appAppender.setRollingPolicy(appPolicy);
//        appAppender.setEncoder(encoder);
//        appAppender.start();
//
////        LogstashEncoder jsonEncoder = new LogstashEncoder();
////        jsonEncoder.setContext(context);
////        jsonEncoder.setCustomFields("{\"app\":\"armchair-management\"}");
////        jsonEncoder.setIncludeStructuredArguments(true);
////        jsonEncoder.setIncludeMdc(true);
////        jsonEncoder.start();
//
//
////        appAppender.setRollingPolicy(appPolicy);
////        appAppender.setEncoder(jsonEncoder);
////        appAppender.start();
//
//
//
//        // === AUDIT LOG ===
////        RollingFileAppender<ILoggingEvent> auditAppender = new RollingFileAppender<>();
////        auditAppender.setContext(context);
////        //auditAppender.setFile(loggingProperties.getAudit().getPath());
////
////        TimeBasedRollingPolicy<ILoggingEvent> auditPolicy = new TimeBasedRollingPolicy<>();
////        auditPolicy.setContext(context);
////        auditPolicy.setParent(auditAppender);
////        auditPolicy.setFileNamePattern(loggingProperties.getAudit().getPattern());
////        auditPolicy.setMaxHistory(loggingProperties.getAudit().getMaxHistory());
////        auditPolicy.start();
////
////        PatternLayoutEncoder auditEncoder = new PatternLayoutEncoder();
////        auditEncoder.setContext(context);
////        auditEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} level=%level  thread_name=%thread  traceId=%X{traceId}  [AUDIT]  message%msg%n");
////        auditEncoder.start();
////
////        auditAppender.setRollingPolicy(auditPolicy);
////        auditAppender.setEncoder(auditEncoder);
////        auditAppender.start();
//
////        LogstashEncoder auditEncoder = new LogstashEncoder();
////        auditEncoder.setContext(context);
////        auditEncoder.start();
////
////        auditAppender.setRollingPolicy(auditPolicy);
////        auditAppender.setEncoder(auditEncoder);
////        auditAppender.start();
//
//        // === CONSOLE ===
//        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
//        consoleAppender.setContext(context);
//        consoleAppender.setEncoder(encoder);
//        //consoleAppender.setEncoder(encoder);
//        consoleAppender.start();
//
//        // === Loggers ===
////        Logger auditLogger = context.getLogger("AUDIT");
////        auditLogger.setAdditive(false);
////        auditLogger.setLevel(Level.INFO);
////        auditLogger.addAppender(auditAppender);
//
//        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
//        rootLogger.setLevel(Level.INFO);
//        rootLogger.addAppender(appAppender);
//        rootLogger.addAppender(consoleAppender);
//    }
}
