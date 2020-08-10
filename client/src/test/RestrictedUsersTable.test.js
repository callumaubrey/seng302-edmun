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
})