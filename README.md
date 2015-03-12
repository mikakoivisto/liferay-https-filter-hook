Liferay HTTPS Servlet Request Filter Hook
-----------------------------------------
 
This filter hook can be used with Liferay when you have a broken setup where the HTTPS is terminated at a LB or Apache in front of Liferay but the Application server does not know the requests are being served over https and won't generate correct url. Liferay provides a few properties to configure those but those do not affect any software that relies on request.getSchema() to return https and request.isSecure() to return true. So you can set following properties in portal-ext.properties to control what HttpServletRequest responds to any app. 

portal(-ext).properties

	# This is a must without it the filter won't be enabled
	web.server.protocol=https

	# Controls request.getServerPort() 
	web.server.https.port=443

	# Optional and it controls request.getServerName()
	#web.server.host=
