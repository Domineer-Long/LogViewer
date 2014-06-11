package me.log.demo.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 指令选择器，将指令和相应的MessageProcessor做绑定
 * 
 * @author Long
 * @version 2014年5月11日
 * @author (lastest modification by Long)
 * @since 1.0
 */

public enum ProcessorSelector {
	PAUSE("pause", PauseMessageProcessor.class), RESUME("resume", ResumeMessageProcessor.class);
	private String selector;

	private Class<? extends MessageProcessor> processor;

	private static Map<String, MessageProcessor> PROCESSOR_MAP;

	private ProcessorSelector(String selector, Class<? extends MessageProcessor> processor) {
		this.processor = processor;
		this.selector = selector;
	}

	/**
	 * 根据指令内容自动选择处理器
	 * 
	 * @param selector 指令
	 * @return MessageProcessor
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */

	public static MessageProcessor getProcessor(String selector) {
		if (null == PROCESSOR_MAP) {
			PROCESSOR_MAP = new ConcurrentHashMap<String, MessageProcessor>();
			for (ProcessorSelector ps : values()) {
				try {
					Class<? extends MessageProcessor> processorClass = ps.processor;
					MessageProcessor processor = processorClass.newInstance();
					PROCESSOR_MAP.put(ps.selector, processor);
				}
				catch (InstantiationException e) {
					e.printStackTrace();

				}
				catch (IllegalAccessException e) {
					e.printStackTrace();

				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();

				}
				catch (SecurityException e) {

					e.printStackTrace();

				}
			}
		}
		return PROCESSOR_MAP.get(selector.toLowerCase());
	}
}
