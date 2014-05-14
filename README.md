Seminar parallel computing
==========================

Term paper for the Zurich university of applied sciences seminar in parallel computing.

This paper aims to check for a possible correlation of twitter news from Switzerland and the changes of the Swiss market index (SMI).
The amount of needed data should be processed with help of the map reduce framework.

1. Analysis of the Swiss twitter news for mood
2. Analysis of the SMI trend
3. Check for a possible correlation


This building this application has some dependencies:

1. [http://alias-i.com/lingpipe] (LingPipe) is needed for sentiment analysis. A free version for research use is available
[http://alias-i.com/lingpipe/web/download.html](here). Afterwards jou have to add it to your local maven:
´´´
mvn install:install-file -DgroupId=com.aliasi -DartifactId=lingpipe -Dversion=4.1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=lingpipe-4.1.0.jar
´´´
2. A older term paper is used for map-reduce: https://github.com/mxmo0rhuhn/map-reduce
Just download it and run
´´´
mvn install
´´´