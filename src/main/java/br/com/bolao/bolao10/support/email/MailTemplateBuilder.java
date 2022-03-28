package br.com.bolao.bolao10.support.email;

import java.util.Map;

import com.google.common.collect.Maps;

public class MailTemplateBuilder {

	private Map<String, String> params;
	private String to;
	private String cc;
	private String from = "noreply@clubflex.com.br";
	private String subject;
	private String htmlTemplate;

	public MailTemplateBuilder() {
		super();
		this.params = Maps.newConcurrentMap();
	}

	public MailTemplateBuilder withParams(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}

	public MailTemplateBuilder addParam(String key, String value) {
		this.params.put(key, value);
		return this;
	}

	public MailTemplateBuilder to(String email) {
		this.to = email;
		return this;
	}

	public MailTemplateBuilder cc(String email) {
		this.cc = email;
		return this;
	}

	public MailTemplateBuilder from(String email) {
		this.from = email;
		return this;
	}

	public MailTemplateBuilder subject(String assunto) {
		this.subject = assunto;
		return this;
	}

	public MailTemplateBuilder template(String templateHtml) {
		this.htmlTemplate = templateHtml;
		return this;
	}

	public MailTemplate build() {
		return new MailTemplate(params, to, cc, from, subject, htmlTemplate);
	}
}
