#!/bin/bash
for FILE in  ../configs/sample-configs/*.xml
do

	rm -f "ds-system.xml" "ds-jobs.xml";
	name=$(basename $FILE);
	filename="${name%.*}"

	echo $filename
	parallel -u -k ::: "./ds-server -c $FILE -v brief -n > logs/group/${filename}.txt;" "sleep 1; cd ..; javac Client.java; java Client;";
	echo "";

	parallel -u -k ::: "./ds-server -c $FILE -v brief > logs/prec/${filename}.txt" "sleep 1; ./ds-client";
	rm -f "ds-system.xml" "ds-jobs.xml";
	echo "===================================================================================";
	echo "";
done;