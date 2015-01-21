package me.log.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.websocket.Session;

public class WebLogOutputStream extends OutputStream {

	/**
	 * 封装日志的消息队列
	 */


	private MessageSender messageSender  ;
	
	public WebLogOutputStream(Session session) {
		messageSender = new MessageSender(session);
	}
	
	public WebLogOutputStream(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	

	@Override
	public void write(int b) throws IOException {

	}

	public void flush() throws IOException {
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
			messageSender.putMessage(bytes, off, len);
	}

	@Override
	public void close() throws IOException {
		messageSender.close();
	}

	public void pause() {
		messageSender.pause();
	}
	public void resume() {
		messageSender.resume();
	}
}
