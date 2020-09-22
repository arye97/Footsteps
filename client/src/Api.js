import axios from 'axios'
import {getCountryNames} from "./constants";

// Needed for testing
require('dotenv').config();

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const server = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000
});

function getTokenHeader() {
  let token = sessionStorage.getItem("token");
  let header = {
    headers: {"Token": token, "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  };
  return header;
}

function getExtendedSearchFilterPageNumberTokenHeader(pageNumber, searchFilter) {
  let header = {
    headers: {"Token": sessionStorage.getItem("token"), "Page-Number":  pageNumber, "Search-Filter": searchFilter,
      "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  };
  return header;
}

function getExtendedPageNumberTokenHeader(pageNumber) {
  let header = {
    headers: {"Token": sessionStorage.getItem("token"), 'Page-Number' :  pageNumber, "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  };
  return header;
}

function getExtendedEmailTokenHeader(extendedHeaders) {
  let header = {
    headers: {"Token": sessionStorage.getItem("token"), 'email' :  extendedHeaders.email, "Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
    withCredentials: true
  };
  return header;
}

export default {
  login: (loginData) => server.post('/login', loginData),
  register: (userData) => server.post('/profiles', userData),
  getActivityTypes: () => server.get('/activity-types', getTokenHeader()),
  getParticipants: (activityId) => server.get(`/activities/${activityId}/participants`, getTokenHeader()),
  editProfile: (userData, profileId) => server.put(`profiles/${profileId}`, userData, getTokenHeader()),
  editLocation: (locationData, profileId) => server.put(`/profiles/${profileId}/location`, locationData, getTokenHeader()),
  getUserData: (profileId) => server.get(`/profiles/${profileId}`, getTokenHeader()),
  checkProfile: (profileId) => server.get(`/check-profile/`.concat(profileId), getTokenHeader()),
  getUserEmails: (profileId) => server.get(`/profiles/${profileId}/emails`, getTokenHeader()),
  checkUserEmail: (insertedEmail) => server.get(`/email`, getExtendedEmailTokenHeader({'email': insertedEmail})),
  setAdditionalEmails: (emails, profileId) => server.post(`/profiles/${profileId}/emails`, emails, getTokenHeader()),
  setEmails: (emails, profileId) => server.put(`/profiles/${profileId}/emails`, emails, getTokenHeader()),
  logout: () => server.post('/logout', null, getTokenHeader()),
  updatePassword: (userId, oldPass, newPass, repeatPass) => server.put(`/profiles/${userId}/password`,
      {'old_password': oldPass, 'new_password': newPass, 'repeat_password': repeatPass}, getTokenHeader()),
  getAllUserData: () => server.get('/profiles', getTokenHeader()),
  getUserActivities: (profileId, pageNumber, searchFilter) => server.get(`/profiles/${profileId}/activities`, getExtendedSearchFilterPageNumberTokenHeader(pageNumber, searchFilter)),
  getUserId: () => server.get(`/profiles/userId`, getTokenHeader()),
  deleteActivity: (profileId, activityId) => server.delete(`/profiles/${profileId}/activities/${activityId}`, getTokenHeader()),
  createActivity: (activityData, profileId) => server.post(`/profiles/${profileId}/activities`, activityData, getTokenHeader()),
  updateActivity: (activityData, profileId, activityId) => server.put(`/profiles/${profileId}/activities/${activityId}`, activityData, getTokenHeader()),
  getActivityData: (activityId) => server.get(`/activities/${activityId}`, getTokenHeader()),
  isActivityEditable: (activityId) => server.get(`/check-activity/${activityId}`, getTokenHeader()),
  getUserRoles: (userId) => server.get(`/profiles/${userId}/role`, getTokenHeader()),
  getUsersByActivityType: (activityTypes, method, pageNumber) => {    // method denotes "and" or "or"
    let activityTypesStr = activityTypes.map(a => a.replace(/\s/g, '-')).join(' ');  // Use RegEx to replace ALL spaces with dashes (because str.replace is stupid)
    return server.get(`profiles?activity=${activityTypesStr}&method=${method}`, getExtendedPageNumberTokenHeader(pageNumber))
  },
  getCountries: () => server.get(getCountryNames),
  getUserSubscribed: (activityId, userId) => server.get(`/profiles/${userId}/subscriptions/activities/${activityId}`, getTokenHeader()),
  setUserSubscribed: (activityId, userId) => server.post(`/profiles/${userId}/subscriptions/activities/${activityId}`, null, getTokenHeader()),
  deleteUserSubscribed: (activityId, userId) => server.delete(`/profiles/${userId}/subscriptions/activities/${activityId}`, getTokenHeader()),
  createOutcome: (outcome) => server.post(`/activities/outcomes`, outcome, getTokenHeader()),
  deleteOutcome: (outcomeId) => server.delete(`/activities/${outcomeId}/outcomes`, getTokenHeader()),
  getFeedEvents: (userId, pageNumber) => server.get(`/profiles/${userId}/subscriptions/`, getExtendedPageNumberTokenHeader(pageNumber)),
  getActivityOutcomes: (activityId) => server.get(`/activities/${activityId}/outcomes`, getTokenHeader()),
  getOutcomeResults: (outcomeId) => server.get(`/outcomes/${outcomeId}/results`, getTokenHeader()),
  createResult: (resultData, outcomeId) => server.post(`/outcomes/${outcomeId}/results`, resultData, getTokenHeader()),
  updateOutcome: (outcomeData) => server.put(`/activities/outcomes`, outcomeData, getTokenHeader()),
  getActivityByActivityTitle: (activityNames, minFitnessLevel, maxFitnessLevel, pageNumber) => {
    let activityNamesStr = activityNames.replace(/\s/g, '%20');  // Use RegEx to replace ALL spaces with dashes
    activityNamesStr = activityNamesStr.replace(/\+/, '%2b');
    return server.get(`/activities?activityKeywords=${activityNamesStr}&fitnessMin=${minFitnessLevel}&fitnessMax=${maxFitnessLevel}`, getExtendedPageNumberTokenHeader(pageNumber))
  },
  getActivityByActivityType: (activityTypes, method, minFitnessLevel, maxFitnessLevel, pageNumber) => {    // method denotes "and" or "or"
    let activityTypesStr = activityTypes.map(a => a.replace(/\s/g, '-')).join(' ');  // Use RegEx to replace ALL spaces with dashes
    return server.get(`activities?activity=${activityTypesStr}&method=${method}&fitnessMin=${minFitnessLevel}&fitnessMax=${maxFitnessLevel}`, getExtendedPageNumberTokenHeader(pageNumber))
  },
  getActivityPins: (profileId, pageNumber) => server.get(`/profiles/${profileId}/activities/pins`, getExtendedPageNumberTokenHeader(pageNumber)),
  getActivityByLocation: (coordinates, minFitnessLevel, maxFitnessLevel, activityTypes, cutoffDistance, method, pageNumber) => {
    return server.get(`/activities?coordinates=${coordinates}&activityTypes=${activityTypes}&cutoffDistance=${cutoffDistance}&method=${method}&fitnessMin=${minFitnessLevel}&fitnessMax=${maxFitnessLevel}`, getExtendedPageNumberTokenHeader(pageNumber))
  },
  getPinsByLocation: (coordinates, minFitnessLevel, maxFitnessLevel, activityTypes, cutoffDistance, method, pageNumber) => {
    return server.get(`/activities/pins?coordinates=${coordinates}&activityTypes=${activityTypes}&cutoffDistance=${cutoffDistance}&method=${method}&fitnessMin=${minFitnessLevel}&fitnessMax=${maxFitnessLevel}`, getExtendedPageNumberTokenHeader(pageNumber))
  }
}
