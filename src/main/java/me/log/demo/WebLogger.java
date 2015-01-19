package me.log.demo;

import java.io.IOException;

import javax.websocket.Session;

import me.log.web.WebLogOutputStream;
import me.log.web.appender.WebLogAppender;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public class WebLogger {
	private WebLogOutputStream outputStream;

	private static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level-%logger{50} - %msg%n";

	private Session session;

	private Logger logger;

	private Appender<ILoggingEvent> appender;

	public WebLogger(Session session) {
		this.session = session;
		outputStream = new WebLogOutputStream(session);
		logger = (Logger) LoggerFactory.getLogger("logger_weblog_" + session.getId());
		appender = createWebLoggerAppender();
		logger.addAppender(appender);
	}

	public Logger getLogger() {
		return logger;
	}

	public WebLogAppender<ILoggingEvent> createWebLoggerAppender() {
		return createWebLoggerAppender(DEFAULT_PATTERN);
	}

	public WebLogAppender<ILoggingEvent> createWebLoggerAppender(String pattern) {
		WebLogAppender<ILoggingEvent> appender = new WebLogAppender<>(this.outputStream);
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		if (pattern == null || pattern.replaceAll("\\s", "").length() == 0)
			pattern = DEFAULT_PATTERN;
		encoder.setPattern(pattern);
		encoder.start();
		String nameStr = "webLog_" + session.getId();
		String appenderName = "appender_" + nameStr;
		appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		appender.setName(appenderName);
		appender.setEncoder(encoder);
		appender.start();
		return appender;
	}

	public void pause() {
		outputStream.pause();
	}

	public void resume() {
		outputStream.resume();
	}

	public void close() {
		appender.stop();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
