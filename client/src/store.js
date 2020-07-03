import Vue from 'vue';

export const store = Vue.observable({
    adminAccess: false
})

export const mutations = {
    switchAccessControl(value) {
        store.adminAccess = value;
    }
}
