import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/ActivityTypeSearchBox.vue';
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue' // name of your Vue component
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(IconsPlugin);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

api.getActivityTypes = jest.fn(() => {
  return Promise.resolve(
      {data: ["Run", "Bike", "Hike", "Walk", "Swim"], status: 200})
});

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      selectedOptions: []
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

describe('ActivityNameSearch.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Add Activity Type', async () => {
    expect(wrapper.find('#activity-type-option-0').exists()).toBe(true);
    await wrapper.find('#activity-type-option-0').trigger('click');
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().selectedActivityTypes).toBeTruthy();
    expect(wrapper.emitted().selectedActivityTypes[0][0]).toStrictEqual(["Run"])
  });

  test('Add multiple Activity Type', async () => {
    expect(wrapper.find('#activity-type-option-0').exists()).toBe(true);
    await wrapper.find('#activity-type-option-0').trigger('click');
    expect(wrapper.find('#activity-type-option-1').exists()).toBe(true);
    await wrapper.find('#activity-type-option-1').trigger('click');
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().selectedActivityTypes).toBeTruthy();
    expect(wrapper.emitted().selectedActivityTypes[1][0]).toStrictEqual(
        ["Run", "Hike"])
  });

  test('Remove Activity Type', async () => {
    expect(wrapper.find('#activity-type-option-0').exists()).toBe(true);
    await wrapper.find('#activity-type-option-0').trigger('click');
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().selectedActivityTypes).toBeTruthy();
    expect(wrapper.emitted().selectedActivityTypes[0][0]).toStrictEqual(["Run"])
    expect(wrapper.find('#activity-type-tag-0').find('button').exists()).toBe(
        true);
    await wrapper.find('#activity-type-tag-0').find('button').trigger('click');
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted().selectedActivityTypes).toBeTruthy();
    expect(wrapper.emitted().selectedActivityTypes[1][0]).toStrictEqual([])
  });

  test('AND method emitted when there is correct AND input', async () => {
    expect(wrapper.find('#radio-and').exists()).toBe(true);
    await wrapper.find('#radio-and').trigger('change');
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted('activityTypeMethod')).toBeTruthy();
    expect(wrapper.emitted().activityTypeMethod[0]).toEqual(['AND'])
  })

  test('OR method emitted when there is correct OR input', async () => {
    expect(wrapper.find('#radio-or').exists()).toBe(true);
    await wrapper.find('#radio-or').trigger('click');
    await wrapper.vm.$nextTick();
    await wrapper.find('#radio-or').trigger('change');
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();
    expect(wrapper.emitted('activityTypeMethod')).toBeTruthy();
    expect(wrapper.emitted().activityTypeMethod[0]).toEqual(['OR'])
  })

});
