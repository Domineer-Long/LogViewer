package me.log.web.websocket;

import java.util.regex.Pattern;

public abstract class WebSocketServer {

	abstract public void onMessage(String message, boolean part);

	public static String html2Text(String msg) {
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		/* & —— &amp; */
		p_html = Pattern.compile("\\&", Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(msg);
		msg = m_html.replaceAll("&amp;");

		/* < —— &lt; */
		p_html = Pattern.compile("\\<", Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(msg);
		msg = m_html.replaceAll("&lt;");

		/* > —— &gt; */
		p_html = Pattern.compile("\\>", Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(msg);
		msg = m_html.replaceAll("&gt;");

		/* 空格 —— &nbsp; */
		p_html = Pattern.compile("\\ ", Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(msg);
		msg = m_html.replaceAll("&nbsp;");

		/* \n —— <br/> */
		p_html = Pattern.compile("\\\n", Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(msg);
		msg = m_html.replaceAll("<br/>");

		return msg;
	}
}
