import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/Activity/Metric/ActivityMetricsEditor.vue';
import {BootstrapVue, IconsPlugin, NavbarPlugin, BootstrapVueIcons} from "bootstrap-vue";
import Vue from "vue"; // name of your Vue component

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(IconsPlugin);

let wrapper;

beforeEach(() => {
    wrapper = mount(Component, {
        mocks: {},
        stubs: {},
        methods: {},
        localVue
    });
});

afterEach(() => {
    wrapper.destroy();
});


describe('ActivityMetricsEditor.vue', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Create Button Adds Metric', async () => {
        expect(wrapper.find('#metric-editor-create-button').exists()).toBe(true);

        expect(wrapper.vm.metric_data.length).toEqual(0);
        await wrapper.find('#metric-editor-create-button').trigger('click');
        expect(wrapper.vm.metric_data.length).toEqual(1);

        expect(wrapper.find('#metric-title-0').exists()).toBe(true);
    });

    test('Can Add multiple metrics',  async () => {
        expect(wrapper.find('#metric-editor-create-button').exists()).toBe(true);

        expect(wrapper.vm.metric_data.length).toEqual(0);
        await wrapper.find('#metric-editor-create-button').trigger('click');
        expect(wrapper.vm.metric_data.length).toEqual(1);
        await wrapper.find('#metric-editor-create-button').trigger('click');
        expect(wrapper.vm.metric_data.length).toEqual(2);

        expect(wrapper.find('#metric-title-0').exists()).toBe(true);
        expect(wrapper.find('#metric-title-1').exists()).toBe(true);
    });

    test('Form fields expected exist', async () => {
        await wrapper.find('#metric-editor-create-button').trigger('click');

        expect(wrapper.find('#metric-title-0').exists()).toBe(true);
        expect(wrapper.find('#metric-rank-by-select-0').exists()).toBe(true);
        expect(wrapper.find('#metric-unit-select-0').exists()).toBe(true);
        expect(wrapper.find('#metric-description-0').exists()).toBe(true);
    });

    test('Form shows state correctly with no input' , async () => {
        await wrapper.find('#metric-editor-create-button').trigger('click');
        expect(wrapper.find('#metric-title-0').props('state')).toBeFalsy();
        expect(wrapper.find('#metric-rank-by-select-0').props('state')).toBeFalsy();
        expect(wrapper.find('#metric-unit-select-0').props('state')).toBeFalsy();
        expect(wrapper.find('#metric-description-0').props('state')).toBeTruthy();
    });

    test('Form shows state correctly with proper input', async () => {
        await wrapper.find('#metric-editor-create-button').trigger('click');

        // Fill with data
        await wrapper.find('#metric-title-0').setValue('Hello World');
        await wrapper.find('#metric-rank-by-select-0').setValue(true);
        await wrapper.find('#metric-unit-select-0').setValue('Distance');

        // Check is valid
        expect(wrapper.find('#metric-title-0').props('state')).toBeTruthy();
        expect(wrapper.find('#metric-rank-by-select-0').props('state')).toBeTruthy();
        expect(wrapper.find('#metric-unit-select-0').props('state')).toBeTruthy();
        expect(wrapper.find('#metric-description-0').props('state')).toBeTruthy();
    });

    test('Form js detects data is invalid' , async () => {
        await wrapper.find('#metric-editor-create-button').trigger('click');
        expect(wrapper.vm.validateMetricData()).toBeFalsy();
    });

    test('Form js detects data is valid', async () => {
        await wrapper.find('#metric-editor-create-button').trigger('click');

        // Fill with data
        await wrapper.find('#metric-title-0').setValue('Hello World');
        await wrapper.find('#metric-rank-by-select-0').setValue(true);
        await wrapper.find('#metric-unit-select-0').setValue('Distance');

        // Check is valid
        expect(wrapper.vm.validateMetricData()).toBeTruthy();
    });
})