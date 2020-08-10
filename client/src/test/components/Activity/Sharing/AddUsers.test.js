import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/SharingActivity/AddUsers.vue';
import {BootstrapVue} from "bootstrap-vue"; // name of your Vue component
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

api.getProfileByEmailAsync = jest.fn(() => {
  return Promise.resolve({data: {results: []}, status: 200})
})

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {},
    mocks: {},
    stubs: {},
    methods: {},
    localVue,
    api
  });
});

afterEach(() => {
  wrapper.destroy();
});

describe('AddUsers.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('add users button is disabled when there is no input', async () => {
    expect(wrapper.find('#add-users-button').exists()).toBe(true);
    expect(wrapper.find('#emails-input').exists()).toBe(true);
    expect(wrapper.find('#emails-input').element.value).toBe('');
    await wrapper.find('#add-users-button').trigger('click');
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().usersAdded).toBeFalsy();
  })

  test('data emitted when there is correct input', async () => {
    expect(wrapper.find('#add-users-button').exists()).toBe(true);
    expect(wrapper.find('#emails-input').exists()).toBe(true);
    await wrapper.setData(
        {emailInput: "poly@pocket.com jackie@gmail.com;don@hotmail.com",})
    expect(wrapper.find('#emails-input').element.value).toBe(
        "poly@pocket.com jackie@gmail.com;don@hotmail.com");
    await wrapper.find('#add-users-button').trigger('click');
    expect(wrapper.vm.validInput).toBe(null);
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().usersAdded).toBeTruthy();
  })

  test('data not emitted when input is incorrect', async () => {
    expect(wrapper.find('#add-users-button').exists()).toBe(true);
    expect(wrapper.find('#emails-input').exists()).toBe(true);
    await wrapper.setData({emailInput: "poly@pocket.com jackiegmail.com",})
    expect(wrapper.find('#emails-input').element.value).toBe(
        "poly@pocket.com jackiegmail.com");
    await wrapper.find('#add-users-button').trigger('click');
    expect(wrapper.vm.validInput).toBe(false);
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().usersAdded).toBeFalsy();
  })
})