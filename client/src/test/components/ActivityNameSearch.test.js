import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/ActivityNameSearch.vue';
import {BootstrapVue} from "bootstrap-vue"; // name of your Vue component
import api from '@/Api.js'

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

jest.mock('@/Api.js', () => jest.fn);

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

    test('Start typing', async () => {
        expect(wrapper.find('#name-input-field').exists()).toBe(true);
        await wrapper.find('#name-input-field').trigger('keyup');
        await wrapper.vm.$nextTick();
        expect(wrapper.emitted().input).toBeTruthy();
    });

});
