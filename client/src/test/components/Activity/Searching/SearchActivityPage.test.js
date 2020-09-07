import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/pages/SearchActivity/App';
import {BootstrapVue} from "bootstrap-vue"; // name of your Vue component
import api from '@/Api.js'
import searchActivityApi from "@/scripts/Activity/activitySearch"

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);
jest.mock('@/scripts/Activity/activitySearch', () => jest.fn);
jest.mock("goatonly_navbar.png", () => {
  return {};
});

api.getLoggedInProfile = jest.fn(() => {
  return Promise.resolve({data: {firstname: "Max"}, status: 200})
})

api.getActivitiesBySearch = jest.fn(() => {
  return Promise.resolve(
      {data: {results: [{activity_name: "Hike"}]}, status: 200})
})

api.getActivityCountBySearch = jest.fn(() => {
  return Promise.resolve({data: 1, status: 200})
})

api.getFirstName = jest.fn(() => {
  return Promise.resolve({data: {firstname: "Max"}, status: 200})
})

api.getActivityTypes = jest.fn(() => {
  return Promise.resolve(
      {data: ["Run", "Bike", "Hike", "Walk", "Swim"], status: 200})
});

searchActivityApi.getSearchActivitiesQueryParams = jest.fn(() => {
  return "activities/search?name=hike&offset=1&limit=10&lat=10.23&lon=9.83&radius=10"
})

searchActivityApi.getSearchActivtyParamsFromQueryURL = jest.fn(() => {
  return {}
})

const $route = {
  fullPath: 'http://localhost:9499'
}

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {},
    mocks: {
      $route
    },
    stubs: {},
    methods: {},
    localVue,
    api,
    searchActivityApi
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('App.vue in search activity page', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('mounted function works', async () => {
    expect(wrapper.vm.activity_data.length).toBe(1);
  })

  // test('data emitted when there is correct input', async () => {
  //   expect(wrapper.find('#add-users-button').exists()).toBe(true);
  //   expect(wrapper.find('#emails-input').exists()).toBe(true);
  //   await wrapper.setData(
  //       {emailInput: "john@pocket.com jackie@gmail.com;don@hotmail.com",})
  //   expect(wrapper.find('#emails-input').element.value).toBe(
  //       "john@pocket.com jackie@gmail.com;don@hotmail.com");
  //   await wrapper.find('#add-users-button').trigger('click');
  //   expect(wrapper.vm.validInput).toBe(null);
  //   await wrapper.vm.$nextTick();
  //   await wrapper.vm.$nextTick();
  //   expect(wrapper.emitted().usersAdded).toBeTruthy();
  // })
  //
  // test('data not emitted when email does not exist', async () => {
  //   expect(wrapper.find('#add-users-button').exists()).toBe(true);
  //   expect(wrapper.find('#emails-input').exists()).toBe(true);
  //   await wrapper.setData({emailInput: "jackiegmail.com",})
  //   expect(wrapper.find('#emails-input').element.value).toBe(
  //       "jackiegmail.com");
  //   await wrapper.find('#add-users-button').trigger('click');
  //   expect(wrapper.vm.validInput).toBe(false);
  //   await wrapper.vm.$nextTick();
  //   await wrapper.vm.$nextTick();
  //   expect(wrapper.emitted().usersAdded).toBeFalsy();
  // })
  //
  // test('data not emitted when email is owned by the creator', async () => {
  //   expect(wrapper.find('#add-users-button').exists()).toBe(true);
  //   expect(wrapper.find('#emails-input').exists()).toBe(true);
  //   await wrapper.setData({emailInput: "poly@pocket.com",})
  //   expect(wrapper.find('#emails-input').element.value).toBe(
  //       "poly@pocket.com");
  //   await wrapper.find('#add-users-button').trigger('click');
  //   expect(wrapper.vm.validInput).toBe(false);
  //   await wrapper.vm.$nextTick();
  //   await wrapper.vm.$nextTick();
  //   expect(wrapper.emitted().usersAdded).toBeFalsy();
  // })
})