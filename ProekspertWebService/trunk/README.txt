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

=========================
ECLIPSE IDE RELATED NOTES
=========================
The default "Build Automatically" setting in Eclipse causes the following issue:
running the Main application or JUnit tests in Eclipse AFTER building the project using the Ant tasks,
will cause an exception to be thrown.

To resolve this issue, either clean the project (in the main menu choose "Project > Clean...")
every time after building with the Ant build file in Eclipse, or uncheck the "Build Automatically"
option (in the main menu "Project > Build Automatically") to avoid the above described issue.
Or just DONT'T USE Ant build file while in Eclipse, because it is mainly meant for using outside of the IDE.