package br.com.bolao.bolao10.support.email;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class MailTemplate implements Serializable{

	private static final long serialVersionUID = -526228294202149020L;

	private static final String RESOURCES_MAIL_TEMPLATES = "templates/mail";

	private Map<String, String> params;
	private String to;
	private String cc;
	private String from;
	private String subject;
	private String htmlTemplate;
	
	public MailTemplate() {
		super();
	}

	public MailTemplate(Map<String, String> params, String to, String cc, String from, String subject, String htmlTemplate) {
		super();
		this.params = params;
		this.to = to;
		this.cc = cc;
		this.from = from;
		this.subject = subject;
		this.htmlTemplate = htmlTemplate;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getTo() {
		return to;
	}

	public String getCc() {
		return cc;
	}

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getHtmlTemplate() {
		return htmlTemplate;
	}

	@SuppressWarnings("resource")
	public String getContent() {
		//@formatter:off
		String file = new Scanner(Thread.currentThread()
				                .getContextClassLoader()
				                .getResourceAsStream(RESOURCES_MAIL_TEMPLATES.concat(File.separator).concat(this.htmlTemplate)))
								.useDelimiter("°")
								.next();
		for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
			String prop = iterator.next();
			if (params.get(prop) == null)
				params.put(prop, "");
			file = file.replace("{{" + prop + "}}", params.get(prop));
		}
		return file;
		//@formatter:on
	}

}
