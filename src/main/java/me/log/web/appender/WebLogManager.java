package me.log.web.appender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import me.log.web.WebLogOutputStream;
import me.log.web.WebLogger;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public class WebLogManager {
	private ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();

	private static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level-%logger{50} - %msg%n";

	private static class ManagerHolder {
		private static final WebLogManager WEB_LOG_MANAGER = new WebLogManager();
	}

	private Map<String, Appender<ILoggingEvent>> appenderMap = new ConcurrentHashMap<>();

	private WebLogManager() {
	}

	public static WebLogManager getInstance() {
		return ManagerHolder.WEB_LOG_MANAGER;
	}

	public WebLogAppender<ILoggingEvent> createWebLogAppender(Session session) {
		return createWebLogAppender(session, DEFAULT_PATTERN);
	}

	public WebLogAppender<ILoggingEvent> createWebLogAppender(Session session, String pattern) {
		WebLogAppender<ILoggingEvent> appender = new WebLogAppender<>(new WebLogOutputStream(session));
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext((LoggerContext) iLoggerFactory);
		if (pattern == null || pattern.replaceAll("\\s", "").length() == 0)
			pattern = DEFAULT_PATTERN;
		encoder.setPattern(pattern);
		encoder.start();
		String nameStr = "webLog_" + session.getId();
		String appenderName = "appender_" + nameStr;
		appender.setContext((LoggerContext) iLoggerFactory);
		appender.setName(appenderName);
		appender.setEncoder(encoder);
		registerAppender(session.getId(), appender);
		appender.start();
		return appender;
	}

	public Logger createLoggerWithAppender(Session session) {
		String loggerName = "logger_webLog_" + session.getId();
		Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
		logger.setLevel(Level.DEBUG);
		logger.addAppender(createWebLogAppender(session));
		return logger;
	}
	
	public Logger createWebLogger(Session session){
		WebLogger logger=new WebLogger(session);
		return logger.getLogger();
	}

	private void registerAppender(String sessionId, Appender<ILoggingEvent> appender) {
		this.appenderMap.put(sessionId, appender);
	}

	public Appender<ILoggingEvent> getAppender(String sessionId) {
		return appenderMap.get(sessionId);
	}

	public void removeAppender(String sessionId) {
		appenderMap.remove(sessionId);
	}

}
