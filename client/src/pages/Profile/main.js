import Vue from 'vue'
import App from './User'

Vue.config.productionTip = false

import VueLogger from 'vuejs-logger';

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

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