import {createLocalVue, mount} from '@vue/test-utils';
import RestrictedUsersTabs
  from '@/components/SharingActivity/RestrictedUsersTabs';
import ShareActivity from "@/components/SharingActivity/ShareActivity";
import {BootstrapVue} from "bootstrap-vue";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

afterEach(() => {

  wrapper.destroy();
});

describe('RestrictedUsersTable.vue', () => {
  test(
      'does not display RestrictedUsersTabs when activity is selected as public',
      async () => {
        wrapper = mount(ShareActivity, {
          data() {
            return {
              selected: 'Public',
              busy: false
            }
          },
          propsData: {
            modal: true,
            visibility: 'Public',
            activityId: "1"
          },
          localVue
        })

        expect(wrapper.find("#activity-visibility-button").exists()).toBe(true);
        await wrapper.find("#activity-visibility-button").trigger('click')
        expect(wrapper.find('#modal-1').exists()).toBe(true);
        expect(wrapper.find(RestrictedUsersTabs).exists()).toBe(false);
      })

  test(
      'does not display RestrictedUsersTabs when activity is selected as private',
      async () => {
        wrapper = mount(ShareActivity, {
          data() {
            return {
              selected: 'Private',
              busy: false
            }
          },
          propsData: {
            modal: true,
            visibility: 'Private',
            activityId: "1"
          },
          localVue
        })
        expect(wrapper.find("#activity-visibility-button").exists()).toBe(true);
        await wrapper.find("#activity-visibility-button").trigger('click')
        expect(wrapper.find('#modal-1').exists()).toBe(true);
        expect(wrapper.find(RestrictedUsersTabs).exists()).toBe(false);
      })

  test(
      'does display RestrictedUsersTabs when activity is selected as restricted',
      async () => {
        wrapper = mount(ShareActivity, {
          data() {
            return {
              selected: 'Restricted',
              busy: false
            }
          },
          propsData: {
            modal: true,
            visibility: 'Restricted',
            activityId: "1"
          },
          localVue
        })
        expect(wrapper.find("#activity-visibility-button").exists()).toBe(true);
        await wrapper.find("#activity-visibility-button").trigger('click')
        expect(wrapper.find('#modal-1').exists()).toBe(true);
        expect(wrapper.find(RestrictedUsersTabs).exists()).toBe(true);
      })
})