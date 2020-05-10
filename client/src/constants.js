// Base URLs to access APIs
const restCountriesBaseUrl = "https://restcountries.eu/rest/v2"

// API endpoints
export const getCountryNames = restCountriesBaseUrl + "/all?fields=name"

// Fitness levels
export const fitnessLevels = [{value: 0 , desc: "Unfit, no regular exercise, being active is very rare"},
    {value: 1, desc: "Not overly fit, occasional recreational fitness activity, active a few times a month"},
    {value: 2, desc: "Moderately fit, enjoys fitness activities for recreation, active once or twice a week"},
    {value: 3, desc: "Fit, may compete occasionally in small scale events, active most days"},
    {value: 4, desc: "Very fit, competitive athlete, extremely active"}]
