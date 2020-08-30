import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import SearchPage from '../views/Search/Search.vue';
import "jest";

let searchPage;

beforeEach(() => {
    searchPage = shallowMount(SearchPage, {
        methods: {
            logout: () => {},
        },
        router
    });

});

test('Is a vue instance', () => {
    expect(searchPage.isVueInstance).toBeTruthy();
});

test('Search mode selector exists', () => {
    expect(searchPage.find('#searchModeSelect').exists()).toBeTruthy();
});