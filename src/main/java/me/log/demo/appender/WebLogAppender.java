package me.log.demo.appender;

import java.io.OutputStream;

import me.log.demo.AsyncQueueOutputStream;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;

/**
 * 自定义LogAppender
 * 
 * @author Long
 * @version $Revision$ 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 * @param <E>
 */

public class WebLogAppender<E> extends OutputStreamAppender<E> {

	private OutputStream os;

	public WebLogAppender() {
	}

	public WebLogAppender(OutputStream os) {
		this.os = os;
	}

	@Override
	public void setEncoder(Encoder<E> encoder) {
		super.setEncoder(encoder);
		if (os == null)
			os = new AsyncQueueOutputStream();
		this.setOutputStream(os);
	}


}
