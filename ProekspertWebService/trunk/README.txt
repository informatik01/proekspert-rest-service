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

==========
HOW TO USE
==========
Use startup.bat script to run the application.
Use config.properties to make the appropriate changes.
Also make the appropriate changes to log4j.properties to configure the log file.

============
KNOWN ISSUES
============
The default "Build Automatically" setting in Eclipse causes the following issues.

After using Ant "package" task in Eclipse, running the Main application in Eclipse
will throw the following exception: 
	"No properties file with the name "config.properties" found on the class path."
To resolve this issue, you need first to clean the project (in the menu choose "Project > Clean")
and after that the Main application will run successfully.

Also when trying to run JUnit tests in Eclipse right after building the project with Ant task,
an exception might be thrown. Again, the solution is to clean the project fefore running unit tests.

So either clean the project everytime **after** building with custom Ant build file in Eclipse,
or just uncheck the "Build Automatically" option (in the main menu "Project > Build Automatically")
to avoid the above described issues. Or just DONT'T USE Ant build file while in Eclipse, because
the default "Build Automatically" option will do just fine.

