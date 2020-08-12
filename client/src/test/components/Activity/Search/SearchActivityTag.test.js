import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/Activity/SearchActivityTag.vue';
import {BootstrapVue} from "bootstrap-vue";
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

api.getProfileByEmailAsync = jest.fn(() => {
  return Promise.resolve({data: {results: []}, status: 200})
})

api.getActivityCreatorId = jest.fn(() => {
  return Promise.resolve({data: 1, status: 200})
})

api.getProfileEmails = jest.fn(() => {
  return Promise.resolve({data: {emails: [{address:"poly@pocket.com", primary: true}]}, status: 200})
})

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      maxEntries: 30,
      options: [],
      inputPlaceholder: '',
      helpText: 'Max 30 Hashtags',
      values: [],
      inputCharacterLimit: 140
    },
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

describe('SearchActivityTag.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('add hashtag button is disabled when no input', async () => {
    expect(wrapper.find('#add-hashtag-button').exists()).toBe(true);
    expect(wrapper.find('#hashtag-input').exists()).toBe(true);
    expect(wrapper.find('#hashtag-input').element.value).toBe('');
    await wrapper.find('#add-hashtag-button').trigger('click');
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().emitInput).toBeFalsy();
  })

  test('data emitted when there is correct hashtag input', async () => {
    expect(wrapper.find('#add-hashtag-button').exists()).toBe(true);
    expect(wrapper.find('#hashtag-input').exists()).toBe(true);
    await wrapper.setData({value: "hashtag"});
    expect(wrapper.find('#hashtag-input').element.value).toBe(
        "hashtag");
    await wrapper.find('#add-hashtag-button').trigger('click');
    expect(wrapper.vm.inputState()).toBe(null);
    expect(wrapper.emitted().emitInput).toBeTruthy();
  })

  test('data not emitted when invalid hashtag input', async () => {
    expect(wrapper.find('#add-hashtag-button').exists()).toBe(true);
    expect(wrapper.find('#hashtag-input').exists()).toBe(true);
    await wrapper.setData({value: "someHashtag*",});
    expect(wrapper.find('#hashtag-input').element.value).toBe(
        "someHashtag*");
    await wrapper.find('#add-hashtag-button').trigger('click');
    expect(wrapper.vm.inputState()).toBe(false);
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().emitInput).toBeFalsy();
  })

});