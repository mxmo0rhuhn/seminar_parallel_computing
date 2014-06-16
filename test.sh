#!/bin/bash
# Little script for performance tests

LOG_FILE=test.log
APPLICATION_LOG_FILE=log.log
EXECUTION_FILE=start.sh

rm -fv $(LOG_FILE)
touch $(LOG_FILE)

# Iterate over number of threads
sed -i "/offset*/c\offset=20" parallelComputing.properties

for i in {1..30}
do
  echo "" >> $(LOG_FILE)
  sed -i "/nThreadWorkers.*/c\nThreadWorkers=$i" mapreduce.properties
  ./$(EXECUTION_FILE)

  echo "nThreadWorkers=$i offset=20" >> $(LOG_FILE)
  cat $(APPLICATION_LOG_FILE) | grep Elapsed >> $(LOG_FILE)
  echo CPU: `uptime | grep -o "load average.*"` >> $(LOG_FILE)
done

for j in 5 10 15 20
do
  # Iterate over offsets
  sed -i "/nThreadWorkers.*/c\nThreadWorkers=$j" mapreduce.properties

  for i in {1..100}
  do
    echo "" >> $(LOG_FILE)
    sed -i "/offset*/c\offset=$i" parallelComputing.properties
    ./$(EXECUTION_FILE)

    echo "nThreadWorkers=$j offset=$i" >> $(LOG_FILE)
    cat $(APPLICATION_LOG_FILE) | grep Elapsed >> $(LOG_FILE)
    echo CPU: `uptime | grep -o "load average.*"` >> $(LOG_FILE)
  done
done
