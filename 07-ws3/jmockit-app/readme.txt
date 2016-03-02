This is a simple Java application with JUnit tests
using JMockit to simulate unimplemented objects.
 
JUnit is a framework to write repeatable tests 
(http://junit.org/).

JMockit is a framework to generate mock objects with annotations
(http://jmockit.org/


Instructions using Maven:
------------------------

To use JUnit in Maven, it is necessary to put the test classes under src/test/java. 
It is also necessary to add dependencies for JMockit (first) and JUnit.

To compile:
  mvn compile

To test:
  mvn test


To configure the Maven project in Eclipse:
-----------------------------------------

If Maven pom.xml exist:
    'File', 'Import...', 'Maven'-'Existing Maven Projects'
    'Select root directory' and 'Browse' to the project base folder.
	Check that the desired POM is selected and 'Finish'.

If Maven pom.xml do not exist:
    'File', 'New...', 'Project...', 'Maven Projects'.
	Check 'Create a simple project (skip architype selection)'.
	Uncheck  'Use default Workspace location' and 'Browse' to the project base folder.
	Fill the fields in 'New Maven Project'.
	the Check if everything is OK and 'Finish'.

To run:
    Select the main class and click 'Run' (the green play button).
    Specify arguments using 'Run Configurations'


--
Revision date: 2015-04-15
leic-sod@disciplinas.tecnico.ulisboa.pt
