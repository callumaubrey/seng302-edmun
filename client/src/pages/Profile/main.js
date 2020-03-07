import Vue from 'vue'
import App from './User'
import {BootstrapVue, IconsPlugin, NavbarPlugin} from 'bootstrap-vue';
import VueLogger from 'vuejs-logger';

Vue.config.productionTip = false;

const options = {
    isEnabled: true,
    logLevel: 'debug',
    stringifyArguments: false,
    showLogLevel: true,
    showMethodName: false,
    separator: '|',
    showConsoleColors: true
};

Vue.use(VueLogger, options);
Vue.use(NavbarPlugin);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

Vue.component('delete-Button', {
    template: `
        <div>
            <button v-on:click='onClick'>Delete</button>
        </div>
    `,
    methods: {
        onClick() {
            this.parentNode.parentNode.removeChild(this.parentNode);
        }
    }
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: { App }
});
