#/bin/bash

# startup shell script for cloud-crawler
# @author Ryan Z

# cloud-crawler [--help]
if [ $# -lt 1 ] || [ $1 = "--help" ]
then
    echo "Usage: cloud-crawler [OPTION] [IP] [PORT] [CODE]"
    echo "OPTION list:"
    echo "	--help		see usage."
    echo "	-m, --master	strat master service."
    echo "	-s, --slave	strat slave service."
    echo "[IP], [PORT] and [CODE] are only available when trying to start slave service."
    echo "If not given, defalut setting will be applied."

# cloud-crawler -m/--master
elif [ $1 = "-m" ] || [ $1 = "--master" ]
then
    if [ -e "/usr/crawler/cloud-crawler-jar-with-dependencies.jar" ]
    then
        cd /usr/crawler
        java -jar cloud-crawler-jar-with-dependencies.jar master
        echo "[INFO] Master service started."
    else
        echo "[ERROR] Target file not found!"
        echo "[INFO] Master service NOT started."
    fi

# cloud-crawler -s
elif [ $1 = "-s" ] || [ $1 = "--slave" ]
then
    if [ -e "/usr/crawler/cloud-crawler-jar-with-dependencies.jar" ]
    then

	if [ $# -eq 4 ]
	then 
	    cd /usr/crawler
	    java -jar cloud-crawler-jar-with-dependencies.jar slave $2 $3 $4
	    echo "[INFO] Slave service started. Master at $2:$3 with code $4."
	elif [ $# -eq 1 ]
	then
	    cd /usr/crawler
	    java -jar cloud-crawler-jar-with-dependencies.jar slave
	    echo "[INFO] Slave service started. Using default setting."
	fi  

    else
        echo "[ERROR] Target file not found!"
        echo "[INFO] Slave service NOT started."
    fi

# other
else
    echo "[ERROR] Wrong arguments!"
fi

