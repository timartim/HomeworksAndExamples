#!/bin/bash

javac -classpath ../../java-advanced-2023/artifacts/info.kgeorgiy.java.advanced.implementor.jar ../java-solutions/info/kgeorgiy/ja/kornilev/implementor/Implementor.java
jar -cfm implementor.jar MANIFEST.MF -C ../java-solutions info/kgeorgiy/ja/kornilev/implementor/Implementor.class
