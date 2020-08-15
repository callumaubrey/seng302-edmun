import {createLocalVue, mount} from '@vue/test-utils';
import Component from '@/components/ActivityContinuousDurationSearchBox.vue';
import {BootstrapVue} from 'bootstrap-vue' // name of your Vue component

const localVue = createLocalVue();
localVue.use(BootstrapVue);
let wrapper;

beforeEach(() => {
    wrapper = mount(Component, {
        propsData: {
        },
        mocks: {},
        stubs: {},
        methods: {},
        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('ActivityContinuousDurationSearchBox.vue', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Click all button emits "all"', async () => {
        expect(wrapper.find('#allButton').exists()).toBe(true);
        await wrapper.find('#allButton').trigger('click');
        expect(wrapper.emitted().selected).toBeTruthy()
        expect(wrapper.emitted().selected[0][0]).toBe("all")
    });

    test('Click continuous button emits "continuous"', async () => {
        expect(wrapper.find('#continuousButton').exists()).toBe(true);
        await wrapper.find('#continuousButton').trigger('click');
        expect(wrapper.emitted().selected).toBeTruthy()
        expect(wrapper.emitted().selected[0][0]).toBe("continuous")
    });

    test('Click duration button emits "duration"', async () => {
        expect(wrapper.find('#durationButton').exists()).toBe(true);
        await wrapper.find('#durationButton').trigger('click');
        expect(wrapper.emitted().selected).toBeTruthy()
        expect(wrapper.emitted().selected[0][0]).toBe("duration")
    });

    test("Changing start date emits both dates", async() => {
        expect(wrapper.find("#start-date-input").exists()).toBe(true);
        const input = wrapper.find("#start-date-input");
        input.element.value = "2020-01-01"
        await input.trigger("change")
        expect(wrapper.emitted().dates[0][0].startDate).toBe("2020-01-01")
    });

    test("Changing end date emits both dates", async() => {
        expect(wrapper.find("#end-date-input").exists()).toBe(true);
        const input = wrapper.find("#end-date-input");
        input.element.value = "2020-01-01"
        await input.trigger("change")
        expect(wrapper.emitted().dates[0][0].endDate).toBe("2020-01-01")
    });
});
