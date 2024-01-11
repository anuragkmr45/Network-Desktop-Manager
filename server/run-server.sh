#!/bin/bash

# Create the bin folder if it doesn't exist
mkdir -p bin

# Compile Java files and store the .class files in the bin folder
javac -d bin DesktopLockingServer.java DesktopSharing.java MessagingServer.java PortScanner.java

# Run each program in the background
java -cp bin DesktopLockingServer &
java -cp bin DesktopSharing &
java -cp bin MessagingServer &
java -cp bin PortScanner &
