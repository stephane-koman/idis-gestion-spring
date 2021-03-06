#!/bin/sh
SERVICE_NAME=ServiceIdisGestion
PATH_TO_JAR=idis-gestion.jar
PID_PATH_NAME=/tmp/IdisGestion-pid
case $1 in 
	start)
		echo "Starting $SERVICE_NAME ..."
		if [ ! -f $PID_PATH_NAME ]; then
			nohup java -jar $PATH_TO_JAR 2> /tmp/idisgestion_error.log > /tmp/idisgestion.log &
					echo $! > $PID_PATH_NAME
			echo "$SERVICE_NAME started ..."	
		else
			echo "$SERVICE_NAME is already running ..."
		fi
	;;
	stop)
		if [ -f $PID_PATH_NAME ]; then
			PID=$(cat $PID_PATH_NAME)
			echo "$SERVICE_NAME stopping ..."
			kill $PID
			echo "$SERVICE_NAME stopped ..."
			rm $PID_PATH_NAME
		else
			echo "$SERVICE_NAME is not running ..."
		fi
	;;
	restart)
		if [ -f $PID_PATH_NAME ]; then
			PID=$(cat $PID_PATH_NAME)
			echo "$SERVICE_NAME stopping ..."
			kill $PID
			echo "$SERVICE_NAME stopped ..."
			rm $PID_PATH_NAME
			echo "Starting $SERVICE_NAME ..."
            nohup java -jar $PATH_TO_JAR 2> /tmp/idisgestion_error.log > /tmp/idisgestion.log &
                    echo $! > $PID_PATH_NAME
			echo "$SERVICE_NAME started ..."
		else
			echo "$SERVICE_NAME is not running ..."
		fi
	;;
esac

