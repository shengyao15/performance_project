Install log-view
===

## Consumer, Producer or Web Application 

### Download the log-view.war

	/usr/bin/wget "http://repo1.corp.hp.com/nexus/service/local/artifact/maven/redirect?r=releases&g=com.hp.hpsc.log-monitor&a=log-view-app&v=LATEST&e=war" -O log-view.war


### Install 
Install at the weblogic console.

### Configuration (Optional)

#### Load log file locations from System properties
Usually we don't need to config anything. We use the system parameter to locate the log file location. See the logview/java_env.properties

	param.1=appserver.home.dir
	param.2=LOG_ROOT
	param.3=log_dir
	param.4=com.vignette.portal.installdir.path

#### Load log file locations from other folders
We can add the customized log location at global_resource/logview/log_path.properties. Like:

	log.link.1=/opt/sasuapps
	log.link.2=/opt/sasuapps/itrc/vignetteSPFA

### Verify 
- Open the browser to visit the log-view application. (**Should visit the server name with port, not the loadbalance location**) Like http://c0007614.itcs.hp.com:50002/log-view
- logged in with email/password. The user need to be under hpsc-log-view group. Ask Feng or Tristan if you logged in failed.
- In the home page, check whether we can download log file.


## Http Server (Apache)

### Download apache configuration files

	cd /opt/webhost/local/spdepot
	/usr/bin/wget "http://repo1.corp.hp.com/nexus/service/local/artifact/maven/redirect?r=releases&g=com.hp.hpsc.log-monitor&a=log-view-apache&v=LATEST&e=tar" -O log-view-apache.tar


### Install

#### Extract log-view-apache files

	# Login as application account (itrc), and set default file permission
	umask 022
	
	cd /opt/webhost/local/WHA-COMMON/apache/conf
	tar xvf /opt/webhost/local/spdepot/log-view-apache.tar


#### Integrate log-view web configuration (Only for First Setup)

	# Update /opt/webhost/local/WHA-COMMON/apache/conf/httpd.conf
	vi /opt/webhost/local/WHA-COMMON/apache/conf/httpd.conf
	
	# Include log-view configuration
	Include /opt/webhost/local/WHA-COMMON/apache/conf/log-view.conf
	# End of log-view configuration
	# Save the change

#### Restart all apache instances

	/opt/webhost/whaeng/bin/restart_apache WHA-HPP-ANON
	/opt/webhost/whaeng/bin/restart_apache WHA-HPP-AUTH
	/opt/webhost/whaeng/bin/restart_apache WHA-ATHP-AUTH
	/opt/webhost/whaeng/bin/restart_apache WHA-ATHP-ANON


### Verify
- Open the browser to visit log-view. (**Should visit the server name with port, not the loadbalance location**) Like http://c0007614.itcs.hp.com:1480/log-view/ (NOTE: May require web proxy.)
- check whether we can download log file.
