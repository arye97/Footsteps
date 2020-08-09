import axios from 'axios'

// Needed for testing
require('dotenv').config();

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const server = axios.create({
  baseURL: SERVER_URL,
  timeout: 3000
});

function getTokenHeader() {
  return {
    headers: {'Token': sessionStorage.getItem("token"), "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  }
}

function getExtendedTokenHeader(extendedHeaders) {
  let header = {
    headers: {'Token': sessionStorage.getItem("token"), 'email' : extendedHeaders, "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  };
  return header;
}

export default {
  login: (loginData) => server.post('/login', loginData),
  register: (userData) => server.post('/profiles', userData),
  getActivityTypes: () => server.get('/activity-types', getTokenHeader()),
  editProfile: (userData, profileId) => server.put(`profiles/${profileId}`, userData, getTokenHeader()),
  getUserData: (profileId) => server.get(`/profiles/${profileId}`, getTokenHeader()),
  checkProfile: (profileId) => server.get(`/check-profile/`.concat(profileId), getTokenHeader()),
  getUserEmails: (profileId) => server.get(`/profiles/${profileId}/emails`, getTokenHeader()),
  checkUserEmail: (insertedEmail) => server.get(`/email`, getExtendedTokenHeader({'email': insertedEmail})),
  setAdditionalEmails: (emails, profileId) => server.post(`/profiles/${profileId}/emails`, emails, getTokenHeader()),
  setEmails: (emails, profileId) => server.put(`/profiles/${profileId}/emails`, emails, getTokenHeader()),
  logout: () => server.post('/logout', null, getTokenHeader()),
  updatePassword: (userId, oldPass, newPass, repeatPass) => server.put(`/profiles/${userId}/password`,
    {'old_password': oldPass, 'new_password': newPass, 'repeat_password': repeatPass}, getTokenHeader()),
  getAllUserData: () => server.get('/profiles', getTokenHeader()),
  getUserActivities: (profileId) => server.get(`/profiles/${profileId}/activities`, getTokenHeader()),
  getUserId: () => server.get(`/profiles/userId`, getTokenHeader()),
  deleteActivity: (profileId, activityId) => server.delete(`/profiles/${profileId}/activities/${activityId}`, getTokenHeader()),
  createActivity: (activityData, profileId) => server.post(`/profiles/${profileId}/activities`, activityData, getTokenHeader()),
  updateActivity: (activityData, profileId, activityId) => server.put(`/profiles/${profileId}/activities/${activityId}`, activityData, getTokenHeader()),
  getActivityData: (activityId) => server.get(`/activities/${activityId}`, getTokenHeader()),
  getUserRoles: (userId) => server.get(`/profiles/${userId}/role`, getTokenHeader()),
  getUsersByActivityType: (activityTypes, method) => {
    let activityTypesStr = activityTypes.map(a => a.replace(/\s/g, '-')).join(' ');  // Use RegEx to replace ALL spaces with dashes (because str.replace is stupid)
    return server.get(`profiles?activity=${activityTypesStr}&method=${method}`, getTokenHeader())
  },
}
