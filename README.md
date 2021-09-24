# Tic-Tac-Toe
A local multiplayer Tic-Tac-Toe game written in Java using sockets.

## Building
To compile the application, open up a terminal and compile all of the Java
source files:
```
javac *.java
```

## Running
It is important to note that when running this application you must
run the dispatcher first, **then** the client. The dispatcher
calls the server, so it is important to run the dispatcher so that
you can actually play the game.

Enter the directory where the `.class` files are stored and run the dispatcher:
```
java Dispatcher
```

Once the dispatcher is running, open another terminal window under the
same directory and run the client:
```
java Client
```

Enjoy!