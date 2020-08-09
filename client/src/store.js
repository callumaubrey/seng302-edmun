import Vue from 'vue';

// Global store for variables that are needed across multiple components

export const store = Vue.observable({
    adminAccess: true,
    logged_in_id: null,
});

export const mutations = {
    switchAccessControl(value) {
        store.adminAccess = value;
    },

    setLoggedInUser(user_id) {
        store.logged_in_id = user_id;
    },

    logout() {
        store.logged_in_id = null;
    },

    isLoggedIn() {
        return store.logged_in_id !== null;
    }
}
