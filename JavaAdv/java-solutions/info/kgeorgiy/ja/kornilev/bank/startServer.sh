#!/bin/zsh
find . -name \*.class -type f -delete
javac  $(find . -name "*.java" ! -path "./tests/*")
javasol="../../../../../../"
java -cp $javasol  info.kgeorgiy.ja.kornilev.bank.BankWebServer
"$@"
