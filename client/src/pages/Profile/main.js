import Vue from 'vue'
import App from './App'

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


Vue.component('custom-button', {
  template: '\
    <button class="normal"\
      :class="type"\
      :disabled="disabled"\
      @click="callback($event)"\
      >\
        <slot></slot>\
    </button>\
  ',
  props: {
    type: String,
    disabled: Boolean,
    editMode: Boolean,
  },
  methods: {
    callback: function(e) {
      this.$emit('click', e);
      console.log("HelloWorld")
    }
  }
});

Vue.use(VueLogger, options);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: { App }
});