package me.log.web.processor;

import me.log.web.MessageSender;

/**
 * 消息处理器接口
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */

public interface MessageProcessor {
	
	void process(MessageSender messageSender);
}
