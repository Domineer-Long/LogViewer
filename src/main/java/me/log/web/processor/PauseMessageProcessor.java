package me.log.web.processor;

import javax.websocket.Session;

import me.log.web.MessageSender;
import me.log.web.WebLogger;

/**
 * 暂停指令的消息处理器
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */
public class PauseMessageProcessor implements MessageProcessor {

	@Override
	public void process(Session session) {
	}

	@Override
	public void process(WebLogger webLogger) {
		webLogger.pause();

	}

	@Override
	public void process(MessageSender messageSender) {
		// TODO Auto-generated method stub

	}

}
