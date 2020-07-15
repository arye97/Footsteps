fuser -k 8999/tcp || true
java -Dspring.profiles.active=prod -jar test-server/libs/server-0.0.1-SNAPSHOT.jar --server.port=8999
