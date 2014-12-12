#java -agentlib:jdwp=transport=dt_socket,server=n,address=debian:15005,suspend=y -classpath "./libs/*:./config/" Client
p=`echo $PWD`
cd $HOME/client/
java -classpath "./libs/*:./config/" Client &
cd $p
exit 0;
