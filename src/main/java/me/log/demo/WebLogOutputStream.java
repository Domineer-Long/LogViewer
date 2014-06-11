package me.log.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

public class WebLogOutputStream extends OutputStream {
	private Session session;

	/**
	 * 封装日志的消息队列
	 */

	private BlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingQueue<>();

	private ExecutorService executor;

	public WebLogOutputStream(Session session) {
		this.session = session;
		executor = Executors.newFixedThreadPool(1);
		executor.execute(sendMessageThread);
	}

	private boolean pause = false;

	/**
	 * 发送消息的线程 将消息广播到每个socket会话
	 */

	private Runnable sendMessageThread = new Runnable() {
		@Override
		public void run() {
			boolean done = false;
			while (!done) {
				try {
					String message = MESSAGE_QUEUE.take();
					try {
						session.getBasicRemote().sendText(message);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				catch (InterruptedException e) {
					done = true;
					System.err.println(e);
				}
			}
		}
	};

	@Override
	public void write(int b) throws IOException {

	}

	public void flush() throws IOException {
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		String log = new String(bytes, off, len);
		if (!pause) {
			try {
				MESSAGE_QUEUE.put(log);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
}
