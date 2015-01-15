package me.log.demo.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import me.log.demo.AsyncQueueOutputStream;
import me.log.demo.processor.MessageProcessor;
import me.log.demo.processor.ProcessorSelector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@ServerEndpoint(value = "/viewer/log1")
public class LogViewerWebSocketServer extends WebSocketServer{
	private Session session;

	private static final Logger logger = LoggerFactory.getLogger("weblog");


	/**
	 * 当有客户端接入时触发 将新会话加入会话队列
	 * 
	 * @param session 客户端接入会话对象
	 */

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		logger.info("有一个客户端已连接 【{}】", session.getId());
		AsyncQueueOutputStream.SESSION_QUEUE.add(session);
	}

	/**
	 * 会话关闭触发该方法， 从会话队列中移除该会话
	 */

	@OnClose
	public void onClose() {
		if (AsyncQueueOutputStream.SESSION_QUEUE.contains(session))
			AsyncQueueOutputStream.SESSION_QUEUE.remove(session);
		logger.info("客户端【{}】已关闭 ", session.getId());
	}

	/**
	 * 会话发生异常时触发
	 * 
	 * @param throwable
	 */

	@OnError
	public void onError(Throwable throwable) {

	}

	/**
	 * 客户端发送消息时触发 设置了两个内置指令 当message内容是'pause'时触发暂停打印日志 当message内容是'resume'时触发继续打印日志
	 * 
	 * @param message 消息内容
	 * @throws IOException
	 */

	@OnMessage
	public void onMessage(String message) throws IOException {
		if (null != message) {
			MessageProcessor processor = ProcessorSelector.getProcessor(message);
			if (null != processor) {
				processor.process(session);
				return;
			}
		}
		logger.debug(message);
	}

	@Override
	public void onMessage(String message, boolean part) {
		// TODO Auto-generated method stub
		
	}
}
