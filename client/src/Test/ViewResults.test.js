import ViewResults from "../components/Activities/modals/ViewResults";
import {createLocalVue, mount} from "@vue/test-utils";
import "jest";
import {BootstrapVue, BootstrapVueIcons, IconsPlugin} from 'bootstrap-vue';
import router from "../index";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(IconsPlugin);

jest.mock('../index');

let viewResults;
let config;

const OUTCOME_DATA_WITH_RESULTS = [{
    outcome_id: 1,
    title: "The outcome title",
    activity_id: 1,
    unit_name: "The unit name",
    unit_type: "TEXT",
    results: [{
        user_id: 1,
        user_name: "BobbyNot CloneEm",
        outcome_id: 1,
        value: "Some value",
        did_not_finish: false,
        comment: "Some comment",
        submitted: true
    }]
}];
const OUTCOME_DATA_NO_RESULTS = [{
    outcome_id: 1,
    title: "The outcome title",
    activity_id: 1,
    unit_name: "The unit name",
    unit_type: "TEXT",
    results: []
}];
const NO_OUTCOME_DATA = [];

beforeEach(() => {
    config = {
        propsData: {
            outcomeList: OUTCOME_DATA_WITH_RESULTS
        },
        router,
        localVue
    };
    viewResults = mount(ViewResults, config);
});

test('Is a vue instance', () => {
    expect(viewResults.isVueInstance).toBeTruthy();
});

describe('The activity has outcomes and results', () => {

    test('Displays a result button', () => {
        expect(viewResults.find('#viewResultsBtn1').exists()).toBeTruthy();
    });

    test('Displays a collapse element', () => {
        expect(viewResults.find('#collapse1').exists()).toBeTruthy();
    });

    test('Displays 1 result card', () => {
        expect(viewResults.findAll('#viewResultCard').length).toBe(1);
    });

    test("Displays result text", () => {
        expect(viewResults.find('#viewResultCard').text()).toBe(`${
            OUTCOME_DATA_WITH_RESULTS[0].results[0].user_name}: ${
            OUTCOME_DATA_WITH_RESULTS[0].results[0].value} ${
            OUTCOME_DATA_WITH_RESULTS[0].unit_name}  (${
            OUTCOME_DATA_WITH_RESULTS[0].results[0].comment})`);
    });
});

describe('The activity has outcomes but and no results', () => {

    beforeEach(() => {
        config = {
            propsData: {
                outcomeList: OUTCOME_DATA_NO_RESULTS
            },
            router,
            localVue
        };
        viewResults = mount(ViewResults, config);
    });

    test('Displays no result cards', () => {
        expect(viewResults.findAll('#viewResultCard').length).toBe(0);
    });

    test("Displays no result card test", () => {
        expect(viewResults.find('#noResultCard').text())
            .toBe('This outcome currently has no registered results');
    });
});

describe('The activity has no outcomes', () => {

    beforeEach(() => {
        config = {
            propsData: {
                outcomeList: NO_OUTCOME_DATA
            },
            router,
            localVue
        };
        viewResults = mount(ViewResults, config);
    });

    test('Displays no result button', () => {
        expect(viewResults.find('#viewResultsBtn1').exists()).toBeFalsy();
    });

    test('Displays no collapse element', () => {
        expect(viewResults.find('#collapse1').exists()).toBeFalsy();
    });
});