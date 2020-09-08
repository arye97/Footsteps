fuser -k 9000/tcp || true
source ./prod-client/envVars.txt
serve -s prod-client/dist/ -l 9000
