#java -agentlib:jdwp=transport=dt_socket,server=n,address=debian:17558,suspend=y -classpath "./libs/*:./config/" sr.Server
p=`echo $PWD`
cd $HOME/server/
java -classpath "./libs/*:./config/" sr.Server &
cd $p
exit 0;
