import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import Component from "@/components/Activity/RecordActivityResultModal";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      profileId: 1,
      activityId: 1,
      loggedInId: 1
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

describe('RecordActivityResultModal.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('"Record activity result modal" button exists', async () => {
    expect(wrapper.find('#activity-result-modal-button').exists()).toBe(true);
  });

})