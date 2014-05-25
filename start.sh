#!/bin/bash

rm -fv Sentiments.csv
rm -fv log.txt
java -Djava.util.logging.config.file=logging.properties -jar target/parallelComputing-0.1-SNAPSHOT-jar-with-dependencies.jar
