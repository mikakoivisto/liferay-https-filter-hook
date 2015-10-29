package com.liferay.hook.filter;

import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * HTTPS Request Wrapper.
 * 
 * @author Mika Kovisto <mika@javaguru.fi>
 * @author Tim Telcik <tim.telcik@permeance.com.au>
 */
public class HttpsRequestWrapper extends HttpServletRequestWrapper {
	
	private boolean secure;
	private String scheme;
	private String serverName;
	private int serverPort;
	

	public HttpsRequestWrapper(HttpServletRequest request) {
		super(request);
	}


	public void setSchema(String schema) {
		this.scheme = schema;
	}

	
	@Override
	public String getScheme() {
		if (Validator.isNotNull(this.scheme)) {
			return this.scheme;
		}

		return super.getScheme();
	}
	
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	

	@Override
	public String getServerName() {
		if (Validator.isNotNull(this.serverName)) {
			return this.serverName;
		}

		return super.getServerName();
	}

	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	
	@Override
	public int getServerPort() {
		if (this.serverPort > -1) {
			return this.serverPort;
		}

		return super.getServerPort();
	}

	
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	
	@Override
	public boolean isSecure() {
		return this.secure;
	}


	@Override
	public String toString() {
		return "HttpsRequestWrapper [secure=" + isSecure() + ", scheme=" + getScheme()
				+ ", serverName=" + getServerName() + ", serverPort=" + getServerPort()
				+ "]";
	}

}
