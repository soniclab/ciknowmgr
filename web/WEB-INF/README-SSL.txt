How to turn on SSL in (Linux + Apache Httpd + Apache Tomcat) configuration?

Note: the examples below assume centos5.5, which is the free variant of Red Hat Enterpise Linux.
Note: due to bugs in Adobe Flash Player, upload/download functions in ciknow web application
ONLY work in Internet Explorer once the SSL is enabled.

1, Enable SSL in Apache Http Server: generate key and cert, the configure ssl.conf
http://wiki.centos.org/HowTos/Https

2, Configure Apache Http Server virtual host and jk_connector
#####################################################################################
#################### Example 1 (/etc/httpd/conf/httpd.conf) ########################
#####################################################################################
... (blah, blah, blah...)

# added by york
LoadModule      jk_module modules/mod_jk-1.2.31-httpd-2.2.x.so
JkWorkersFile   /etc/httpd/conf/workers.properties
JkShmFile       logs/mod_jk.shm
JkLogLevel      info
JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "
JkOptions     +ForwardKeySize +ForwardURICompat -ForwardDirectories
JkRequestLogFormat    "%w %V %T"

NameVirtualHost *:80

<VirtualHost *:80>
    #ServerAdmin webmaster@dummy-host.example.com
    DocumentRoot /var/www/html
    ServerName ciknow.northwestern.edu
    ErrorLog logs/ciknow.northwestern.edu-error_log
    CustomLog logs/ciknow.northwestern.edu-access_log combined

    JkLogFile     logs/ciknow.northwestern.edu-mod_jk.log

    JkMount /_* ciknow8080
</VirtualHost>

#####################################################################################
#################### Example 2 (/etc/httpd/conf/workers.properties) #################
#####################################################################################
# Define 1 real worker using ajp13
worker.list=ciknow8080

# Set properties for worker1
worker.ciknow8080.type=ajp13
worker.ciknow8080.host=127.0.0.1
worker.ciknow8080.port=8009

#####################################################################################
#################### Example 3 (/etc/httpd/conf.d/ssl.conf) #########################
#####################################################################################
... (blah, blah, blah...)
#   Per-Server Logging:
#   The home of a custom SSL log file. Use this when you want a
#   compact non-error SSL logfile on a virtual host basis.
CustomLog logs/ssl_request_log \
          "%t %h %{SSL_PROTOCOL}x %{SSL_CIPHER}x \"%r\" %b"

# added by york
    JkLogFile     logs/ciknow.northwestern.edu-mod_jk.log

    JkMount /_* ciknow8080
</VirtualHost>
  
    
3, restart httpd and tomcat  


Note: make sure the selinux is disabled or permissive, not "enabled"