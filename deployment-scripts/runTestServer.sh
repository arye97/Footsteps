fuser -k 9499/tcp || true
java -Dspring.profiles.active=dev -jar test-server/libs/server-0.0.1-SNAPSHOT.jar --server.port=9499
