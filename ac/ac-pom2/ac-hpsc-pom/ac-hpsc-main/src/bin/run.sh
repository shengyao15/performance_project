#!/bin/sh

classpath=patch

if [ -e "patch" ]; then
for file in "`ls patch/*.jar`"; do
	classpath=$classpath:$file
done
fi

for file in lib/*.jar; do
	classpath=$classpath:$file
done

echo "CLASSPATH: $classpath"

JAVA_OPTS="-XX:+DisableExplicitGC -XX:MaxPermSize=128m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:./logs/gc.log -Xms1g -Xmx1g -XX:NewSize=512m -XX:MaxNewSize=512m"

java $JAVA_OPTS -classpath $classpath -Djava.rmi.server.hostname=`hostname` -Dac.hpsc.preference=data/spperf_ac_preferences.yaml com.hp.it.perf.ac.app.hpsc.main.HpscMain
