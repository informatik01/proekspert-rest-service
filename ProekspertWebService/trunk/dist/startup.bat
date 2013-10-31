@echo off
rem This is a startup script for the ProekspertWebService project.
rem It can be used in Apache Ant task or on its own.
rem Target OS is MS Windows.
rem For other OS write an appropriate script.

rem The optional command line argument (currently it can be ANYTHING)
rem is used to tell the application that it needs to create and start a local web server
rem To configure the root folder for the web server, use config.properties file
java -jar ProekspertWebService.jar 1