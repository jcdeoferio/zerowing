#!/bin/bash

assert_file_exist(){
	if ! [ -a $1 ]
	then
		echo "$1 does not exist!"
		exit 1
	fi
}

if [ $# -lt 2 ] || [ $# -gt 3 ]
then
	echo "Usage:\treset_db.sh <database> <sql dump>"
	echo "\treset_db.sh <database> <sql schema dump> <sql data dump>"
	
	exit 1
fi

database=$1

if [ $# -eq 2 ]
then
	sqldump=$2
	
	assert_file_exist $sqldump	
elif [ $# -eq 3 ]
then
	sqlschema=$3
	sqldata=$4
	
	assert_file_exist $sqlschema
	assert_file_exist $sqldata
fi


pg_dump -sO -U postgres $database > resetdb-last-reset-schema.sql
pg_dump -aO -U postgres $database > resetdb-last-reset-data.sql

psql -U postgres postgres -c "DROP DATABASE $database;"
psql -U postgres postgres -c "CREATE DATABASE $database;"

if [ $# -eq 2 ]
then
	psql -U postgres $database -c "\i $sqldump"
elif [ $# -eq 3 ]
then
	psql -U postgres $database -c "\i $sqlschema"
	psql -U postgres $database -c "\i $sqldata"
fi
