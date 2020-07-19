fuser -k 9500/tcp || true
serve -s test-client/dist/ -l 9500
