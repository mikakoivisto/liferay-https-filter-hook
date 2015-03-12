package com.liferay.hook.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class HttpsRequestWrapperFilter extends BaseFilter {

	
	@Override
	public void init(FilterConfig filterConfig) {
		_schema = PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL);
		_serverName = PropsUtil.get(PropsKeys.WEB_SERVER_HOST);
		_serverPort = GetterUtil.getInteger(PropsUtil.get(PropsKeys.WEB_SERVER_HTTPS_PORT), 443);

		if (Validator.isNotNull(_schema) && _schema.equals("https")) {
			_secure = true;
			_enabled = true;
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

		return _enabled;
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

		filterChain.doFilter(new HttpsRequestWrapper(request), response);
	}

	private boolean _enabled;
	private boolean _secure;
	private String _schema;
	private String _serverName;
	private int _serverPort;

	private static Log _log = LogFactoryUtil.getLog(
		HttpsRequestWrapperFilter.class);

	public class HttpsRequestWrapper extends HttpServletRequestWrapper {

		public HttpsRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getScheme() {
			return _schema;
		}

		@Override
		public String getServerName() {
			if (Validator.isNotNull(_serverName)) {
				return _serverName;
			}

			return super.getServerName();
		}

		@Override
		public int getServerPort() {
			if (_serverPort > -1) {
				return _serverPort;
			}

			return super.getServerPort();
		}

		@Override
		public boolean isSecure() {
			return _secure;
		}
	}
}
