import ViewParticipants from "../components/Activities/modals/ViewParticipants";
import {createLocalVue, mount} from "@vue/test-utils";
import "jest";
import {BootstrapVue, BootstrapVueIcons, IconsPlugin} from 'bootstrap-vue';
import router from "../index";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(IconsPlugin);

jest.mock('../index');

let viewParticipants;
let config;
const NO_PARTICIPANTS_DATA = [];
const PARTICIPANTS_DATA = [
    {firstname: "S", middlename: "", lastname: "M", id: 1},
    {firstname: "Larry", middlename: "the", lastname: "Cucumber", id: 2}
];

beforeEach(() => {
    config = {
        propsData: {
            participants: PARTICIPANTS_DATA
        },
        router,
        localVue
    };
    viewParticipants = mount(ViewParticipants, config);
});

test('Is a vue instance', () => {
    expect(viewParticipants.isVueInstance).toBeTruthy();
});

describe('Has 2 participants', () => {

    test('Displays 2 participants', () => {
        // The set up data is set with 2 people participating in this activity
        expect(viewParticipants.findAll('#participant').length).toBe(2);
    });

    test('The no participant message does not exist', () => {
        expect(viewParticipants.find('#noParticipants').exists()).toBeFalsy();
    });
});

describe('Had no participants', () => {

    beforeEach(() => {
        config = {
            propsData: {
                participants: NO_PARTICIPANTS_DATA
            },
            router,
            localVue
        };
        viewParticipants = mount(ViewParticipants, config);
    });

    test('Participant buttons do not exist', () => {
        expect(viewParticipants.find('#participant').exists()).toBeFalsy();
    });

    test("When there are no participants, it displays 'No participants to show'", () => {
        expect(viewParticipants.find('#noParticipants').text()).toBe('No participants to show');
    });
});