fuser -k 9500/tcp || true
source ./test-client/envVars.txt
serve -s test-client/dist/ -l 9500
