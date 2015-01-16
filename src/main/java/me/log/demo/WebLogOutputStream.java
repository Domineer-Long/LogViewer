package me.log.demo;

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
	
	public WebLogOutputStream() {
		messageSender = new MessageSender();
	}
	public WebLogOutputStream(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	
	private boolean pause = false;


	@Override
	public void write(int b) throws IOException {

	}

	public void flush() throws IOException {
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		if (!pause) {
			messageSender.putMessage(new String(bytes, off, len));
		}
	}

	@Override
	public void close() throws IOException {
		messageSender.close();
	}

	public void pause() {
		this.pause = true;
	}
	public void resume() {
		this.pause = false;
	}
}
