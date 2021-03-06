# Search Agent Web
Search Agent is an application that is meant to be helpful tool for finding and saving links of the pirate content. 

>This version is more flexible, scalable and have user friendly Web interface.

## Contents

- [Features](https://github.com/serega4sa/SearchAgentNew#features)
- [Installation](https://github.com/serega4sa/SearchAgentNew#installation)
- [Usage](https://github.com/serega4sa/SearchAgentNew#usage)

## Features

Currently there are two available features:
* **Google search** - finds all links to the queried title of content by using Google search website. There are ability 
to specify:
    * video duration
    * search period
    * localization
    * number of result pages to be processed
* **Results export** - combines found results for the specified period of time to the .xml file

> There are already present icons for Mail.ru search and mail functionality, but so far they haven't been 
implemented yet

## Installation
For correct work of application you will need the following components to be installed:
* Java
  * Download [link](https://www.java.com/en/download/)
  * Installation guide [link](https://www.java.com/en/download/help/ie_online_install.xml)
* Tomcat
  * Download [link](http://apache.volia.net/tomcat/tomcat-9/v9.0.12/bin/apache-tomcat-9.0.12.exe)
  * Installation guide [link](https://www.wikihow.com/Install-Tomcat-on-Windows-7)
* MySQL
  * MySQL Installer [download link](https://dev.mysql.com/downloads/windows/installer/8.0.html)
    * MySQL Community Server - _must_ be installed
    * MySQL Workbench - _optional_, to be able to work directly with database

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
get button. Results for the chosen period will be combined to the .xml file, that you can find in the 
_..webapps/search-agent-web/statistics/_
