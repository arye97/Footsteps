import axios from 'axios'

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const server = axios.create({
  baseURL: SERVER_URL,
  timeout: 2000
});

export default server;
