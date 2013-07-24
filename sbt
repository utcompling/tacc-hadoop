java -Dfile.encoding=UTF8 -Xmx16g -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -jar sbt-launch-*.jar "$@"
