package me.log.demo.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import me.log.web.MessageSender;
import me.log.web.WebLogOutputStream;
import me.log.web.appender.WebLogAppender;
import me.log.web.processor.MessageProcessor;
import me.log.web.processor.ProcessorSelector;
import me.log.web.websocket.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * 全局日志查看器
 * 
 * @author Ma Long
 *
 */
@ServerEndpoint(value = "/viewer/log")
public class LogViewerWebSocketServer extends WebSocketServer {
	private Session session;

	private static final Logger logger = LoggerFactory.getLogger("_weblog_");
	private static Appender<ILoggingEvent>  appender;
	
	private static MessageSender messageSender=new MessageSender();
	/**
	 * 当有客户端接入时触发 将新会话加入会话队列
	 * 
	 * @param session 客户端接入会话对象
	 */

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		logger.info("有一个客户端已连接 【{}】", session.getId());
		if (appender==null) {
			appender=new WebLogAppender<ILoggingEvent>(new WebLogOutputStream(messageSender));
			((ch.qos.logback.classic.Logger)logger).addAppender(appender);
		}
		messageSender.addSession(session);
	}

	/**
	 * 会话关闭触发该方法， 从会话队列中移除该会话
	 */

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("客户端【{}】已关闭 ", session.getId());
		logger.info(closeReason.toString());
		messageSender.removeSession(session);
	}

	/**
	 * 会话发生异常时触发
	 * 
	 * @param throwable
	 */

	@OnError
	public void onError(Throwable throwable) {
		throwable.printStackTrace();
	}

	/**
	 * 客户端发送消息时触发 设置了两个内置指令 当message内容是'pause'时触发暂停打印日志 当message内容是'resume'时触发继续打印日志
	 * 
	 * @param message 消息内容
	 * @param parts 分片处理消息
	 * @throws IOException
	 */

	@OnMessage
	public void onMessage(String message, boolean parts) {
		message = html2Text(message);
		if (null != message) {
			MessageProcessor processor = ProcessorSelector.getProcessor(message);
			if (null != processor) {
				processor.process(session);
				return;
			}
		}
		if (!"__!__".equals(message)) {
			logger.debug(message);
		}
	}
}
