import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import Component from "@/components/Activity/RecordActivityResultForm";
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

api.deleteActivityResult = jest.fn(() => {
  return Promise.resolve(
      {data: {}, status: 200})
});


beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      result: {
        metric_id: 1,
        user_id: 2,
        special_metric: "DidNotFinish",
        result: 1,
        result_finish: "2020-08-10T21:50:00",
        result_start: "2020-08-10T21:50:00",
        type: "Count",
        isEditMode: false,
        title: "Eggs Collected",
        description: "A description"
      },
      metricDict: {
        1: {
          id: 1,
          title: "Eggs Collected",
          description: "A description",
          activity_id: 2,
          rank_asc: 0,
          unit: 1
        },
        2: {
          id: 2,
          title: "Distance travelled",
          description: "A description",
          activity_id: 2,
          rank_asc: 0,
          unit: 2
        }
      },
      isCreateResult: false
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

describe('RecordActivityResultForm.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Select "Edit" button turns on edit mode', async () => {
    expect(wrapper.find('#edit-result-button').exists()).toBe(true);
    const editResultButton = wrapper.find('#edit-result-button');
    await editResultButton.trigger('click');
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.result.isEditMode).toBe(true)
  });

  test('Delete button exists and is clickable', async () => {
    expect(wrapper.find('#delete-result-button').exists()).toBe(true);
    const editResultButton = wrapper.find('#delete-result-button');
    await editResultButton.trigger('click');
    await wrapper.vm.$nextTick();
  });
})