/**
 * Set the timezone to New Zealand time to properly test util.js functions that are dependant on timezone.
 * i.e. otherwise the tests would break if ran in different timezone
 */
export default () => {
    process.env.TZ = 'NZ';
}