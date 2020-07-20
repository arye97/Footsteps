fuser -k 9000/tcp || true
serve -s prod-client/dist/ -l 9000
