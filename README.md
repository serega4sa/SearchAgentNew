# Search Agent Web
Search Agent is an application that is meant to be helpful tool for finding and saving links of the pirate content. 

>This version is more flexible, scalable and have user friendly Web interface.

## Contents

- [Installation](https://github.com/serega4sa/SearchAgentNew#installation)
- [Configuration](https://github.com/serega4sa/SearchAgentNew#configuration)
- [Usage](https://github.com/serega4sa/SearchAgentNew#usage)

## Installation
For correct work of application you will need the following components to be installed:
* Java
  * To download the latest version follow the[link](https://www.java.com/inc/BrowserRedirect1.jsp?locale=ru)
  * Installation guide[link](https://www.java.com/ru/download/help/ie_online_install.xml)
  * Configure environment variables[link](https://stackoverflow.com/a/31340459)
* Tomcat
  * To download the latest version follow the[link](http://apache.cp.if.ua/tomcat/tomcat-9/v9.0.11/bin/apache-tomcat-9.0.11.zip)
  * Installation guide[link](https://www.wikihow.com/Install-Tomcat-on-Windows-7)
* MySQL
  * MySQL Community Server [download link](https://dev.mysql.com/downloads/mysql/)
  * MySQL Workbench (_optional_, to be able to work directly with database)[download link](https://dev.mysql.com/downloads/workbench/)

## Configuration

TODO: fill this paragraph

## Usage

Launch browser and follow the link **_http://localhost:8080/search-agent-web/_**, 
if server is running on the current machine, otherwise instead of _localhost:8080_ should be specified IP address 
of machine on which server is running. 
>**Notice!** To access server remotely, network should be properly configured (port forwarding, firewall, etc.) 
and access link might have another structure.

#### Search
To execute search in the specific search engine use corresponding submenu on the left side bar. On the chosen page 
you will see a form with all needed parameters that should be filled. After filling them, press start button on the 
right side and wait until it is done.

#### Results of the search
To get the results of the search go to statistics submenu on the left side bar. Specify all needed data and press 
get button. Results for the chosen period will be combined to the .xml file, that you can find in the _..webapps/search-agent-web/statistics/_
