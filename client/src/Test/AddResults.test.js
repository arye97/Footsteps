import AddResults from "../components/Activities/modals/AddResults";
import {createLocalVue, mount} from "@vue/test-utils";
import "jest";
import {BootstrapVue, BootstrapVueIcons, IconsPlugin} from 'bootstrap-vue';
import router from "../index";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(IconsPlugin);

jest.mock('../index');

let addResults;
let config;

const OUTCOME_DATA_WITH_RESULT = [{
    outcome_id: 1,
    title: "The outcome title",
    activity_id: 1,
    unit_name: "The unit name",
    unit_type: "TEXT",
    activeUsersResult: {
        user_id: 1,
        user_name: "BobbyNot CloneEm",
        outcome_id: 1,
        value: "Some value",
        did_not_finish: false,
        comment: "Some comment",
        submitted: true
    }
}];
const OUTCOME_DATA_NO_RESULT = [{
    outcome_id: 1,
    title: "The outcome title",
    activity_id: 1,
    unit_name: "The unit name",
    unit_type: "TEXT",
    activeUsersResult: {
        submitted: false
    }
}];

beforeEach(() => {
    config = {
        propsData: {
            outcomeList: OUTCOME_DATA_WITH_RESULT
        },
        router,
        localVue
    };
    addResults = mount(AddResults, config);
});

test('Is a vue instance', () => {
    expect(addResults.isVueInstance).toBeTruthy();
});

test('displays 1 outcome card', () => {
    expect(addResults.findAll('#outcomeAddResultCard').length).toBe(1);
});

describe('The active users result is already submitted', () => {

    test('displays 1 submit result button', () => {
        expect(addResults.find('#submittedInputValue1').exists()).toBeTruthy();
    });
});

describe('The active users result are not yet submitted', () => {

    beforeEach(() => {
        config = {
            propsData: {
                outcomeList: OUTCOME_DATA_NO_RESULT
            },
            router,
            localVue
        };
        addResults = mount(AddResults, config);
    });

    test('displays 1 input for the result', () => {
        expect(addResults.find('#NotSubmittedInputValue1').exists()).toBeTruthy();
    });
    test('displays 1 submit result button', () => {
        expect(addResults.find('#submitResult1').exists()).toBeTruthy();
    });
});