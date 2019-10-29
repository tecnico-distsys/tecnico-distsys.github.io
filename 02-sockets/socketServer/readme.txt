This example demonstrates the use of sockets in the Java platform.
The server opens a socket and blocks waiting for connections.


Instructions using Maven:
------------------------

To compile:
  mvn compile

To run using exec plugin:
  mvn exec:java

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run using appassembler plugin:
  On Windows:
    target\appassembler\bin\socket-server 8000
  On Linux:
    ./target/appassembler/bin/socket-server 8000


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2017-03-10
leic-sod@disciplinas.tecnico.ulisboa.pt
