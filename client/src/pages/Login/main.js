import Vue from 'vue'
import App from './App'
import VueLogger from 'vuejs-logger';
import {BootstrapVue, IconsPlugin, NavbarPlugin} from 'bootstrap-vue';

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

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: {App}
});
