import {mount} from '@vue/test-utils'
import EditDetails from "../components/Settings/EditDetails";
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
  //Uses timeout to prevent issues on the server that were occurring due to the wrapper not mounting in time
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
    editWrapper = mount(EditDetails, {router, attachToDocument: true, mocks: {api}});
    sleep(150).then(() => resolve());
  })
});

afterEach(() => {
  jest.clearAllMocks();
});

test('Is a vue instance', () => {
  expect(editWrapper.isVueInstance).toBeTruthy();
});

function testInputFieldSuccess(fieldName, updatedUser, done) {
  //Have to wait for the next tick in the test so that the dom will render properly
  editWrapper.vm.$nextTick().then(() => {
    editWrapper.find('#' + fieldName).setValue(updatedUser[fieldName]);
    editWrapper.find('#saveChanges-btn').trigger('click');
    expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
    done();
  });
}
function testInputFieldFailure(fieldName, updatedUser, done) {
  //Have to wait for the next tick in the test so that the dom will render properly
  editWrapper.vm.$nextTick().then(() => {
    editWrapper.find('#' + fieldName).setValue(updatedUser[fieldName]);
    editWrapper.find('#saveChanges-btn').trigger('click');
    expect(editWrapper.vm.api.editProfile).toBeCalledTimes(0);
    done();
  });
}

describe('Successful changes', () => {
  it('Mutate first name', done => {
    let updatedUser = getChangeableUser();
    updatedUser.firstname = "Mutate";
    testInputFieldSuccess('firstname', updatedUser, done);
  });

  it('Names with hyphens and apostrophes', done => {
    let updatedUser = getChangeableUser();
    updatedUser.firstname = "Te-st'r";
    updatedUser.middlename = "Te-st'r";
    updatedUser.lastname = "Te-st'r";
    editWrapper.vm.$nextTick().then(() => {
      editWrapper.find('#firstname').setValue("Te-st'r");
      editWrapper.find('#middlename').setValue("Te-st'r");
      editWrapper.find('#lastname').setValue("Te-st'r");
      editWrapper.find('#saveChanges-btn').trigger('click');
      expect(editWrapper.vm.api.editProfile).toBeCalledWith(updatedUser, testUser.id);
      done();
    });  });

  it('Mutate middle name', done => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "Mutate";
    testInputFieldSuccess('middlename', updatedUser, done);
  });

  it('Empty middle name', done => {
    let updatedUser = getChangeableUser();
    updatedUser.middlename = "";
    testInputFieldSuccess('middlename', updatedUser, done);
  });

  it('Mutate last name', done => {
    let updatedUser = getChangeableUser();
    updatedUser.lastname = "Mutate";
    testInputFieldSuccess('lastname', updatedUser, done);
  });

  it('Mutate nickname', done => {
    let updatedUser = getChangeableUser();
    updatedUser.nickname = "Mutate";
    testInputFieldSuccess('nickname', updatedUser, done);
  });

  it('Empty nickname', done => {
    let updatedUser = getChangeableUser();
    updatedUser.nickname = "";
    testInputFieldSuccess('nickname', updatedUser, done);
  });

  it('Mutate bio', done => {
    let updatedUser = getChangeableUser();
    updatedUser.bio = "Mutate";
    testInputFieldSuccess('bio', updatedUser, done);
  });

  it('Empty bio', done => {
    let updatedUser = getChangeableUser();
    updatedUser.bio = "";
    testInputFieldSuccess('bio', updatedUser, done);
  });

});

describe('Unsuccessful changes', () => {
  it('Empty first name', done => {
    const updatedUser = getChangeableUser();
    updatedUser.firstname = "";
    testInputFieldFailure('firstname', updatedUser, done);
  });
  it('Numbered first name', done => {
    const updatedUser = getChangeableUser();
    updatedUser.firstname = "1234Tester";
    testInputFieldFailure('firstname', updatedUser, done);
  });
  it('Empty last name', done => {
    const updatedUser = getChangeableUser();
    updatedUser.lastname = "";
    testInputFieldFailure('lastname', updatedUser, done);
  });
  it('Numbered last name', done => {
    const updatedUser = getChangeableUser();
    updatedUser.lastname = "1234Tester";
    testInputFieldFailure('lastname', updatedUser, done);
  });
  it('Numbered last name', done => {
    const updatedUser = getChangeableUser();
    updatedUser.lastname = "1234Tester";
    testInputFieldFailure('lastname', updatedUser, done);
  });
});

describe('Required fields', () => {
  it('Required fields are marked', done => {
    expect(editWrapper.find('#firstname').element.required).toBeTruthy();
    expect(editWrapper.find('#lastname').element.required).toBeTruthy();
    //Multiselect boxes require a bit of extra work to check
    expect(editWrapper.find('#genderDiv').find('.multiselect').attributes('required')).toBeTruthy();
    expect(editWrapper.find('#date_of_birth').element.required).toBeTruthy();
    done();
  });
  it('Not required fields are not marked', done => {
    expect(editWrapper.find('#middlename').element.required).toBeFalsy();
    expect(editWrapper.find('#nickname').element.required).toBeFalsy();
    expect(editWrapper.find('#fitnessDiv').find('.multiselect').attributes('required')).toBeFalsy();
    expect(editWrapper.find('#passportsDiv').find('.multiselect').attributes('required')).toBeFalsy();
    expect(editWrapper.find('#activityTypesDiv').find('.multiselect').attributes('required')).toBeFalsy();
    expect(editWrapper.find('#bio').element.required).toBeFalsy();
    done();
  });
});