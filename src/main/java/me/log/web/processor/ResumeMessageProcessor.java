package me.log.web.processor;

import me.log.web.MessageSender;
import me.log.web.WebLogOutputStream;

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
	public void process(MessageSender messageSender) {
		messageSender.resume();
	}
	@Override
	public void process(WebLogOutputStream outputStream) {
		outputStream.resume();
	}
}
