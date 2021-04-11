#!/bin/bash
for FILE in  ../configs/test/*
do
	#rm -f "./pre-compiled/ds-system.xml" "./pre-compiled/ds-jobs.xml";
	name=$(basename $FILE);
	filename="${name%.*}";

	echo $filename
	parallel -u -k ::: "./ds-server -c $FILE -v brief -n > logs/test/${filename}.txt;" "cd ..; javac Client.java; java Client;";
	rm -f "ds-system.xml" "ds-jobs.xml";

	echo "";

	#parallel -u ::: "./pre-compiled/ds-server -c $FILE -v brief > logs/prec/${filename}.txt" "./pre-compiled/ds-client";
	#rm -f "./pre-compiled/ds-system.xml" "./pre-compiled/ds-jobs.xml";
	echo "===================================================================================";
	echo "";
done;
