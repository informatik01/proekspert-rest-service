===========
DESCRIPTION
===========
TODO

========================
BUILDING THE APPLICATION
========================
Run the Ant task named "package" to compile and package the application in the form
of runnable JAR file. The resulting JAR file along with necessary libraries and
the startup script will be placed to the "dist" folder.

KNOWN ISSUE
After using Ant "package" task in Eclipse, running the Main application in Eclipse
for some reason will throw the following exception: 
	"No properties file with the name "config.properties" found on the class path."
To deal with it, you need first to clean project (in the menu choose "Project > Clean")
and after that the Main application will run successfully.
==========
HOW TO USE
==========
Use startup.bat script to run the application.
Use config.properties to make the appropriate changes.
Also make the appropriate changes to log4j.properties to configure the log file.