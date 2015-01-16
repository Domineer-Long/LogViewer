package me.log.web.processor;

import javax.websocket.Session;

import me.log.web.MessageSender;
import me.log.web.WebLogger;

/**
 * 重新开始指令的消息处理器
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */
public class ResumeMessageProcessor implements MessageProcessor {

	@Override
	public void process(Session session) {
	}

	@Override
	public void process(WebLogger webLogger) {
		webLogger.resume();
	}

	@Override
	public void process(MessageSender messageSender) {
		// TODO Auto-generated method stub

	}
}
