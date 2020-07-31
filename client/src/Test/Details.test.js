import {mount} from '@vue/test-utils'
import Details from '../views/Settings/Details.vue'
import '../Api'
import 'vue-jest'
import api from "../Api";
import router from '../index'
jest.mock("../Api");

let editWrapper;
const testUser = {
  id:404,
  firstname:"Testing",
  middlename:"This",
  lastname:"Vue",
  nickname:"tester.boi",
  bio:"I test Vue files",
  date_of_birth:"1999-10-08",
  gender:"Male",
  fitness:0,
  passports:[],
  activityTypes:[]
};

function getChangeableUser() {
  let updatedUser = Object.assign({}, testUser);
  delete updatedUser.id;
  return updatedUser;
}

beforeEach(() => {
  api.getActivityTypes.mockImplementation( () => Promise.resolve({data: [{name: 'testing'},{name: 'developing'}], status: 200}));
  api.getUserData.mockImplementation( () => Promise.resolve({data: testUser, status: 200}));
  api.checkProfile.mockImplementation( () => Promise.resolve({status:200}));
  api.editProfile.mockImplementation( () => Promise.resolve({status:200}));
  editWrapper = mount(Details, {router, mocks: {api}, sync: false});
});

test('Is a vue instance', () => {
  expect(editWrapper.isVueInstance).toBeTruthy();
});

describe('On first name ', () => {
  test('change the profile is updated', () => {
    let updatedUser = getChangeableUser();
    updatedUser.firstname = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#firstname').setValue("Mutate");
      editWrapper.find('#saveChanges-btn').trigger('click').then(() => {
        expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      });
    });
  });

  test('deletion an error is thrown', () => {
    let updatedUser = getChangeableUser();
    updatedUser.firstname = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#firstname').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click').then(() => {
        expect(editWrapper.vm.api.editProfile).toBeCalledTimes(0);
      });
    });
  });
});

describe('On middle name ', () => {
  test('change the profile is updated', () => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#middlename').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click').then(() => {
        expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      });
    });
  });

  test('deletion the profile is updated', async () => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#middlename').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click').then(() => {
        expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      });
    });
  });
});