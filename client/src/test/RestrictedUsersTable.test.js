import {createLocalVue, mount} from '@vue/test-utils';
import Component from '../components/SharingActivity/RestrictedUsersTable';
import {BootstrapVue} from "bootstrap-vue"; // name of your Vue component

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

beforeEach(() => {
  wrapper = mount(Component, {
    propsData: {
      roleData: {
        users: [
          {
            name: "John Doe",
            email: "johndoe@doe.com",
            role: "organiser",
            selected: false,
            _rowVariant: 'danger'
          },
          {
            name: "James Smith",
            email: "jamessmith@google.com",
            role: "organiser",
            selected: true,
            _rowVariant: 'none'
          },
        ]
      },
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


describe('RestrictedUsersTable.vue', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test('Select All Button selects all users', async () => {
    expect(wrapper.find('#select-all-button').exists()).toBe(true);
    const selectAllButton = wrapper.find('#select-all-button');
    await selectAllButton.trigger('click');
    await wrapper.vm.$nextTick();
    let users = [
      {
        name: "John Doe",
        email: "johndoe@doe.com",
        role: "organiser",
        selected: true,
        _rowVariant: 'none'
      },
      {
        name: "James Smith",
        email: "jamessmith@google.com",
        role: "organiser",
        selected: true,
        _rowVariant: 'none'
      },
    ]
    expect(wrapper.vm.data.users).toEqual(users)

  });

  test('Deselect All Button deselects all users', async () => {
    expect(wrapper.find('#deselect-all-button').exists()).toBe(true);
    const deselectAllButton = wrapper.find('#deselect-all-button');
    await deselectAllButton.trigger('click');
    let users = [
      {
        name: "John Doe",
        email: "johndoe@doe.com",
        role: "organiser",
        selected: false,
        _rowVariant: 'danger'
      },
      {
        name: "James Smith",
        email: "jamessmith@google.com",
        role: "organiser",
        selected: false,
        _rowVariant: 'danger'
      },
    ]
    expect(wrapper.vm.data.users).toEqual(users)
  });
})