#!/bin/bash

MODULE_LIST="redis-event-bus neo4j-change-hook neo4j-rest neo4j-worker rethinkdb-change-hook rethinkdb-rest rethinkdb-worker"

for i in $MODULE_LIST
do
	(cd $i; mvn clean install)
done
