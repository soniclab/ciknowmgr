C-IKNOW Manager (ciknowmgr) is a web application for managing (create, update, copy, 
enable, disable, delete) C-IKNOW projects.


*********************************************************************************
I, System Requirements								
*********************************************************************************
C-IKNOW requires Java 1.6.x, Apache Tomcat 6.x, MySQL 5.x database in 
the server side, and requires Flash Player 10.x in the client browser.

C-IKNOW Manager also requires Flex SDK 4.5.1


*********************************************************************************
II, INSTALL CIKNOWMGR (Part 1)
*********************************************************************************
1, Create database ciknowmgr and db account ciknowmgr
login as root: 
# mysql -u root -p

create database and account:
mysql> create database ciknowmgr;
mysql> grant all privileges on ciknowmgr.* to ciknowmgr@localhost identified by 'ciknowmgr';

2, Download ciknowmgr.war, and drop into tomcat/webapps directory

Tomcat (already running) will automatically extract the war file into a folder 
"ciknowmgr", load the application, and generate database schema if it is not yet exist
(it is critical that you created database and credentials in previous step). 

This may take a few seconds.

Make sure the folder owner has "rwx" permission.

3, Load base data (ciknowmgr.sql in the ciknowmgr/WEB-INF/sql)
logout mysql
then type in the terminal: mysql -uciknowmgr -pciknowmgr ciknowmgr < path_to_sql_file

4, Ciknowmgr will call the Tomcat manager application, so configure the tomcat/conf/tomcat-users.xml
	
<role rolename="manager"/>
<user username="admin" password="northwestern" roles="manager"/>

(Tomcat come with some sample users/roles, you can ignore them or remove them from this file for clarity)

5, Restart tomcat, point browser to http://host:port/ciknowmgr (default port for tomcat is 8080)
6, After login (admin/ciknow), admin can manage user account (but not creating projects yet, see below)

*********************************************************************************
III, INSTALL CIKNOWMGR (Part 2)
*********************************************************************************
7, Install ant(version 1.7.x, NOT version 1.8.x), put it on PATH
7.1, download ant 1.7.1 from apache website
7.2, unzip into somewhere, e.g. /opt/apache-ant-1.7.1
7.3, export ANT_HOME=/opt/apache-ant-1.7.1
7.4, export PATH=$ANT_HOME/bin:$PATH
7.5, verify ant is installed correctly: ant -version

8, Install flexsdk4.5.1, put it on PATH (similar to ant)
Download FlexSDK 4.5.1 build 4.5.1.21328A from:
http://opensource.adobe.com/wiki/display/flexsdk/Download+Flex+4.5
or
http://sonicserver.northwestern.edu/publish/flex_sdk_4.5.1.21328A.zip

################################################################################################
Note: "put it on PATH" means make it permanently accessible to the account
in which tomcat is running. Setting them via the terminal won't work because
it is one time experience. In another word, if you open a new shell/terminal,
all those settings will be lost and and you need to set it again. The suggested
way to put them on PATH is to edit /etc/profile (this is in RHEL/CENTOS, 
modify similar file in other linux flavor). At the bottom of this file, add something like:
export ANT_HOME=path_to_ant
export FLEX_HOME=path_to_flex
export JAVA_HOME=path_to_java
export PATH=$ANT_HOME/bin:$FLEX_HOME/bin:$JAVA_HOME/bin:$PATH

For every login user, the operating system will execute the /etc/profile and make
these libraries and tools available to the user.

Also make sure that "rx" (read and execute) permissions on these libraries/tools folders 
are available to any user. These libraries/tools are publicly available, so you don't
need to worry about exposing any "secret" to others.
################################################################################################

9, Put ant.jar (under ANT_HOME/lib)  in to tomcat/lib directory

10, ciknowmgr need database administrative account (e.g. root)
in tomcat/webapps/ciknowmgr/WEB-INF/build.xml, locate the "db-create" and "db-delete" task, 
change root password to your mysql root password

11, Configure mail server host, self register, and recapcha in ciknowmgr/WEB-INF/classes/ciknowmgr.properties
	
12, configure tomcat to reserve more memory, in tomcat/bin/catalina.sh
e.g.
	JAVA_OPTS="$JAVA_OPTS -Xms4096m -Xmx16384m -XX:MaxPermSize=4096m"

(it is better to set xms and xmx to the same value because some VM cannot allocate memory flexibly)

(In Windows: set JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx768m -XX:MaxPermSize=512m)
(In Windows: it is better to install tomcat at C:/tomcat, instead of very long, deep directory. 
Also avoid space in path name. For example:

NOT c:\program files\apache\blahblahblah\tomcat
BUT c:\tomcat
)

After restart ciknowmgr application or reboot tomcat container, you should be able to 
view, create, restart, update, copy, disable/enable, delete projects in your server.


************************************************************************************
IV, UPDATE CIKNOWMGR (if you previously installed ciknowmgr, and now want to update)
************************************************************************************
14, Always remember to Backup 
Backup database "ciknowmgr". 
Backup old ciknowmgr folder in webapps.
Remove ciknowmgr folder from tomcat/webapps.

15, Repeat Step 2

16, Repeat Step 10, 11 
You can reference the values in backup WEB-INF/build.xml and WEB-INF/classes/ciknowmgr.properties. 
But please do not simply copy the old file and overwrite the new files, because there may be other 
logic other than root password, etc have been changed.

17, Restart tomcat and test drive ciknowmgr application to make sure it works and is updated


*********************************************************************************
v, ENABLE SSL
*********************************************************************************
Note: This will only enable SSL for ciknow projects, not the ciknowmgr itself.
Note: When SSL is enabled, file upload and download only work for Internet Explorer (Flash Player bug:()

18, Read README-SSL.txt for server configuration

19, Enable Serverside SSL
Rename file ciknowmgr/WEB-INF/template/server/WEB-INF/class/applicationContext-security.xml to applicationContext-security.http.xml 
and rename applicationContext-security.https.xml to applicationContext-security.xml

20, Enable Clientside SSL
Rename ciknowmgr/WEB-INF/template/server/WEB-INF/flex/remote-config.xml to remote-config.http.xml 
and rename remote-config.https.xml to remote-config.xml

21, Restart Tomcat. The newly created or updated project will now use secure channel https.


*********************************************************************************
VI, Documentation								
*********************************************************************************
http://ciknow.northwestern.edu/documentation/



*********************************************************************************
VII, Inquery, Bug Report, and Feature Request					
*********************************************************************************
Email: ciknow@northwestern.edu
http://ciknow.northwestern.edu/contact/