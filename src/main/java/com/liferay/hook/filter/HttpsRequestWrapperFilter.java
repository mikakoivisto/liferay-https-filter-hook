package com.liferay.hook.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HTTPS Request Wrapper Filter.
 * 
 * @author Mika Kovisto <mika@javaguru.fi>
 * @author Tim Telcik <tim.telcik@permeance.com.au>
 */
public class HttpsRequestWrapperFilter extends BaseFilter {
	
	@Override
	public void init(FilterConfig filterConfig) {
		
		_schema = PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL);
		_serverName = PropsUtil.get(PropsKeys.WEB_SERVER_HOST);
		_serverPort = GetterUtil.getInteger(PropsUtil.get(PropsKeys.WEB_SERVER_HTTPS_PORT), Http.HTTPS_PORT);

		if (Validator.isNotNull(_schema) && _schema.equalsIgnoreCase(Http.HTTPS)) {
			_secure = true;
			_enabled = GetterUtil.getBoolean(PropsUtil.get(getClass().getName()), true); 
		}
		
		if (getLog().isInfoEnabled()) {
			getLog().info("init ...");
			getLog().info("schema : " + _schema);
			getLog().info("serverName : " + _serverName);
			getLog().info("serverPort : " + _serverPort);
			getLog().info("secure : " + _secure);
			getLog().info("enabled : " + _enabled);
		}

		super.init(filterConfig);
	}

	
	@Override
	public boolean isFilterEnabled() {
		return _enabled;
	}

	
	@Override
	public boolean isFilterEnabled(
		HttpServletRequest request, HttpServletResponse response) {
		
		if (getLog().isDebugEnabled()) {
			getLog().debug("isFilterEnabled ...");
			getLog().debug("request.class: " + request.getClass().getName());
			Enumeration attrNamesEnum = request.getAttributeNames();
			List attrNamesList = Collections.list(attrNamesEnum);
			getLog().debug("request.class: " + attrNamesList);
			getLog().debug("request attribute SKIP_FILTER : " + request.getAttribute(SKIP_FILTER));
		}

		boolean filterEnabled = false;
		
		if (isAlreadyFiltered(request)) {
			getLog().info("Request already filtered; ignore");
			filterEnabled = false;
		} else {
			getLog().info("Request not yet filtered; continue");
			filterEnabled = _enabled;
		}
		
		getLog().info("Filter enabled : " + filterEnabled);
		
		return filterEnabled;
	}
	
	
	protected boolean isAlreadyFiltered(HttpServletRequest request) {
		if (request.getAttribute(SKIP_FILTER) != null) {
			return true;
		} else {
			return false;
		}
	}
	

	@Override
	protected Log getLog() {
		return _log;
	}

	
	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {
		
		if (getLog().isDebugEnabled()) {
			getLog().debug("processFilter ...");
		}
		
		request.setAttribute(SKIP_FILTER, Boolean.TRUE);
		
		String completeURL = HttpUtil.getCompleteURL(request);
		
		String requstURL = HttpUtil.getRequestURL(request);
		
		if (getLog().isInfoEnabled()) {
			getLog().info("Start filter for HTTPS request wrapper at URL " + completeURL);
		}

		if (getLog().isDebugEnabled()) {
			getLog().debug("requstURL: " + requstURL);
			getLog().debug("request header (referer): " + request.getHeader("referer"));
		}
		
		HttpsRequestWrapper requestWrapper = new HttpsRequestWrapper(request);
		
		requestWrapper.setSchema(_schema);
		requestWrapper.setSecure(_secure);
		requestWrapper.setServerName(_serverName);
		requestWrapper.setServerPort(_serverPort);
		
		if (getLog().isDebugEnabled()) {
			getLog().debug("requestWrapper: " + requestWrapper);
			getLog().debug("request attribute SKIP_FILTER : " + request.getAttribute(SKIP_FILTER));
			getLog().debug("requestWrapper attribute SKIP_FILTER : " + requestWrapper.getAttribute(SKIP_FILTER));
		}
		
		processFilter(HttpsRequestWrapperFilter.class, requestWrapper, response, filterChain);
		
		if (getLog().isInfoEnabled()) {		
			getLog().info("End filter for web request logger at URL " + completeURL);		
		} 
	}

	
	private boolean _enabled;
	private boolean _secure;
	private String _schema;
	private String _serverName;
	private int _serverPort;

	private static Log _log = LogFactoryUtil.getLog(HttpsRequestWrapperFilter.class);
	
	private static final String SKIP_FILTER = HttpsRequestWrapperFilter.class.getName() + "SKIP_FILTER";

}
