import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/ActivityTypeSearchBox.vue';
import {BootstrapVue} from "bootstrap-vue"; // name of your Vue component
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

api.getActivityTypes = jest.fn(() => {
    return Promise.resolve({data: ["Run","Bike","Hike","Walk","Swim"], status: 200})
});

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

describe('ActivityNameSearch.vue', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Select Activity Type', async () => {
        expect(wrapper.find('#activity-type-dropdown').exists()).toBe(true);
        await wrapper.find('#activity-type-dropdown').trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.emitted().selectedActivityTypes).toBeTruthy();
    });
});
