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

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
  return new Promise(resolve => setTimeout(resolve, milliseconds))
};


function getChangeableUser() {
  let updatedUser = Object.assign({}, testUser);
  delete updatedUser.id;
  return updatedUser;
}

beforeEach(() => {
  return new Promise(resolve => {
    api.getActivityTypes.mockImplementation(() => Promise.resolve({
      data: [{name: 'testing'}, {name: 'developing'}],
      status: 200
    }));
    api.getUserData.mockImplementation(() => Promise.resolve({data: testUser, status: 200}));
    api.checkProfile.mockImplementation(() => Promise.resolve({status: 200}));
    api.editProfile.mockImplementation(() => Promise.resolve({status: 200}));
    api.getCountries.mockImplementation(() => Promise.resolve({
      data: [{name: 'New Zealand'}, {name: 'Australia'}],
      status: 200
    }));
    editWrapper = mount(Details, {router, attachToDocument: true, mocks: {api}});
    sleep(150).then(() => resolve());
  })
});

afterEach(() => {
  jest.clearAllMocks();
});

test('Is a vue instance', () => {
  expect(editWrapper.isVueInstance).toBeTruthy();
});

describe('On first name ', () => {
  it('change the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.firstname = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#firstname').setValue("Mutate");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });

  it('deletion an error is thrown', done => {
    const updatedUser = getChangeableUser();
    updatedUser.firstname = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#firstname').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledTimes(0);
      done();
    });
  });
});

describe('On middle name ', () => {
  it('change the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#middlename').setValue("Mutate");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });

  it('deletion the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#middlename').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });
});

describe('On last name ', () => {
  it('change the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.lastname = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#lastname').setValue("Mutate");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });

  it('deletion an error is thrown', done => {
    const updatedUser = getChangeableUser();
    updatedUser.lastname = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#lastname').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledTimes(0);
      done();
    });
  });
});

describe('On nickname ', () => {
  it('change the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.nickname = "Mutate";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#nickname').setValue("Mutate");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });

  it('deletion the profile is updated', done => {
    let updatedUser = getChangeableUser();
    updatedUser.nickname = "";
    //Have to wait for the next tick in the test so that the dom will render properly
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#nickname').setValue("");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });
  });
});