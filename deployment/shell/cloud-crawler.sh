#/bin/bash

# startup shell script for cloud-crawler
# @author Ryan Z

# cloud-crawler [--help]
if [ $# -lt 1 ] || [ $1 = "--help" ]
then
    echo "Usage: cloud-crawler [OPTION]"
    echo "OPTION list:"
    echo "	--help		see usage."
    echo "	-m, --master	strat master service."
    echo "	-s, --slave	strat slave service."

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
        cd /usr/crawler
        java -jar cloud-crawler-jar-with-dependencies.jar slave
        echo "[INFO] Slave service started."
    else
        echo "[ERROR] Target file not found!"
        echo "[INFO] Slave service NOT started."
    fi

# other
else
    echo "[ERROR] Wrong argument!"
fi

