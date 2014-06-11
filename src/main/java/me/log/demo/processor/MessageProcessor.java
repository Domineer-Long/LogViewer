package me.log.demo.processor;

import javax.websocket.Session;

import me.log.demo.WebLogger;

/**
 * 消息处理器接口
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */

public interface MessageProcessor {
	void process(Session session);

	void process(WebLogger webLogger);
}
