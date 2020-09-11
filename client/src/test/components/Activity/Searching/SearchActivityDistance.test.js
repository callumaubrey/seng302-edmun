import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/ActivityDistanceSlider.vue';
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

describe('ActivityDistanceSlider.vue', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Change in slider emits distance', async () => {
        expect(wrapper.find('#distance-slider').exists()).toBe(true);

        await wrapper.find('#distance-slider').trigger('change');
        await wrapper.vm.$nextTick();
        expect(wrapper.emitted().distanceChange).toBeTruthy();
    });
});