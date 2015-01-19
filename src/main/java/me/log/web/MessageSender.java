package me.log.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

public class MessageSender implements Runnable {

	private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	private ExecutorService executorService;
	private Queue<Session> sessionsQueue = new ConcurrentLinkedDeque<>();
	private boolean pause = false;

	public MessageSender(Session session) {
		sessionsQueue.add(session);
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(this);
	}

	public MessageSender(Collection<Session> sessions) {
		sessionsQueue.addAll(sessions);
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(this);
	}

	public MessageSender() {
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(this);
	}

	public MessageSender(Collection<Session> sessions,
			ExecutorService executorService) {
		sessionsQueue.addAll(sessions);
		executorService.execute(this);
	}

	public void addSession(Session session) {
		sessionsQueue.add(session);
	}

	public void removeSession(Session session) {
		sessionsQueue.remove(session);
	}

	public void addAllSession(Collection<Session> sessions) {
		sessionsQueue.addAll(sessions);
	}

	private boolean interrupted = false;
	private boolean done = false;

	/**
	 * 将消息广播到webSocket会话
	 */
	@Override
	public void run() {
		while (!isDone()) {
			try {
				String message = messageQueue.take();
				for (Session session : sessionsQueue) {
					session.getBasicRemote().sendText(message);
				}
			} catch (InterruptedException | IOException e) {
				done = true;
				interrupted = true;
			}
		}
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public boolean isDone() {
		return done;
	}

	public void close() {
		this.done = true;
		executorService.shutdown();
		for (Session session : sessionsQueue) {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		sessionsQueue.clear();
		messageQueue.clear();
	}

	public void putMessage(byte[] bytes, int off, int len) {
		if (!pause) {
			putMessage(new String(bytes, off, len));
		}
	}

	public void putMessage(String message) {
		if (!pause) {
			try {
				messageQueue.put(message);
			} catch (InterruptedException e) {
				close();
				interrupted = true;
			}
		}
	}

	public void pause() {
		this.pause = true;
	}

	public void resume() {
		this.pause = false;
	}
}
