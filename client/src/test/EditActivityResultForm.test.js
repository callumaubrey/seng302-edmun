import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import Component from "@/components/Activity/RecordActivityResultForm";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      result: {
        title: null,
        type: null,
        result: null,
        resultStart: null,
        resultEnd: null,
        isEditMode: false
      },
      metricDict: {
        "Eggs collected": "Count",
        "Distance flown": "Distance",
        "Duration spent trippin": "Duration",
        "Time to 10km": "StartFinish"
      },
      isCreateResult: false
    },
    mocks: {},
    stubs: {},
    methods: {},
    localVue
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

})