import axios from 'axios'  
  
const SERVER_URL = process.env.VUE_APP_SERVER_ADD;
console.log(SERVER_URL + "@@@");
  
const instance = axios.create({  
  baseURL: SERVER_URL,  
  timeout: 1000  
});  
  
export default {  
  // (C)reate  
  createNew: (name) => instance.post('students', {name}),  
  // (R)ead  
  getAll: () => instance.get('students', {  
    transformResponse: [function (data) {  
      return data? JSON.parse(data)._embedded.students : data;  
    }]  
  }),  
  // (U)pdate  
  updateForId: (id, name) => instance.put('students/'+id, {name}), 
  // (D)elete  
  removeForId: (id) => instance.delete('students/'+id)  
}