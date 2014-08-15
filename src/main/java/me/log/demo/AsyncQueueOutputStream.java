package me.log.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

/**
 * 异步队列输出流 1、打印日志到控制台 2、同步输出日志到websocket会话中，并显示到页面
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */

public class AsyncQueueOutputStream extends OutputStream {

	/**
	 * 会话队列，封装所有的websocket会话
	 */

	public static final Queue<Session> SESSION_QUEUE = new ConcurrentLinkedDeque<>();

	/**
	 * 系统输出流，用于将日志输出到控制台
	 */

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public AsyncQueueOutputStream() {
		executor.execute(sendMessageThread);
	}

	/**
	 * 封装日志的消息队列
	 */

	private BlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingQueue<>();

	@Override
	public void write(int b) {
	}

	/**
	 * 发送消息的线程 将消息广播到每个socket会话
	 */

	private MessageThread sendMessageThread = new MessageThread();

	public void flush() throws IOException {
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		if (sendMessageThread.isInterrupted()) {
			sendMessageThread=new MessageThread();
		}
		String log = new String(bytes, off, len);
		try {
			this.MESSAGE_QUEUE.put(log);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		this.executor.shutdown();
		for (Session session : SESSION_QUEUE) {
			session.close();
		}

	}
	
	private class MessageThread implements Runnable{
	private 	boolean interrupted=false;
		@Override
		public void run() {
			boolean done = false;
			while (!done) {
				try {
					String message = MESSAGE_QUEUE.take();
					for (Session session : SESSION_QUEUE) {
						try {
							session.getBasicRemote().sendText(message);
						}
						catch (IOException e) {
							SESSION_QUEUE.remove(session);
							e.printStackTrace();
						}
					}
				}
				catch (InterruptedException e) {
					done = true;
					interrupted=true;
					(e).printStackTrace();
				}
			}
		}
		public boolean isInterrupted(){
			return interrupted;
		}
	}
}
