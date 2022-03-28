package br.com.segmedic.clubflex.support;

import java.time.LocalDateTime;
import java.util.List;

import br.com.segmedic.clubflex.domain.Audit;
import br.com.segmedic.clubflex.domain.User;

public class AuditBuilder {
	
	private Audit audit;
	
	public AuditBuilder(User user) {
		super();
		this.audit = new Audit();
		this.audit.setDateTimeOcorr(LocalDateTime.now());
		this.audit.setIdUser(user.getId());
	}

	public AuditBuilder withUrl(String url) {
		this.audit.setUrl(url);
		return this;
	}
	
	public AuditBuilder withMethod(String method) {
		this.audit.setMethod(method);
		return this;
	}
	
	public AuditBuilder withIp(String ip) {
		this.audit.setIp(ip);
		return this;
	}
	
	public AuditBuilder withClassMethod(String classMethod) {
		this.audit.setClassMethod(classMethod);
		return this;
	}
	
	public AuditBuilder withArgs(List<Object> args) {
		StringBuilder strArg = new StringBuilder();
		args.forEach(arg->{
			if(arg != null) {
				String objectToJsonString = JsonUtils.objectToJsonString(arg);
				if(objectToJsonString != null) {
					strArg.append(objectToJsonString);
				}
			}
		});
		this.audit.setBodyData(strArg.toString());
		return this;
	}
	
	
	public Audit build() {
		return this.audit;
	}

	
}
