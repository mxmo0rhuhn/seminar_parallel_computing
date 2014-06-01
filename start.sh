#!/bin/bash

rm -fv results.csv
rm -fv sentiments.csv
rm -fv Comparison.png
rm -fv log.txt
java -Djava.util.logging.config.file=logging.properties -jar target/parallelComputing-0.1-SNAPSHOT-jar-with-dependencies.jar
