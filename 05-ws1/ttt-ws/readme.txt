This is a distributed game of Tic Tac Toe implemented using Java Web Services.

The server is a Web Service.
It stores the game state and provides operations for modifying it.


Instructions using Maven:
------------------------

To compile:
  mvn compile

To run using exec plugin:
    mvn exec:java

To generate launch scripts for Windows and Linux:
  mvn package appassembler:assemble

To run using appassembler plugin:
  On Windows:
    target\appassembler\bin\ttt-ws http://localhost:8080/ttt-ws/endpoint
  On Linux:
    ./target/appassembler/bin/ttt-ws http://localhost:8080/ttt-ws/endpoint


To configure the project in Eclipse:
-----------------------------------

If Eclipse files (.project, .classpath) exist:
    'File', 'Import...', 'General'-'Existing Projects into Workspace'
    'Select root directory' and 'Browse' to the project base folder.
    Check if everything is OK and 'Finish'.

If Eclipse files do not exist:
    'File', 'Import...', 'Maven'-'Existing Maven Projects'.
    'Browse' to the project base folder.
    Check that the desired POM is selected and 'Finish'.

To run:
    Select the main class and click 'Run' (the green play button).
    Specify arguments using 'Run Configurations'


--
Revision date: 2015-03-12
leic-sod@disciplinas.tecnico.ulisboa.pt