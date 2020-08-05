import Vue from 'vue';

// Global store for variables that are needed across multiple components

export const store = Vue.observable({
    adminAccess: true,
    notifications: [],
    //Style should be a vue variant name (eg. 'success', 'warning', 'danger')
    newNotification(message, style, time) {
        let notification = {
            notificationMessage: 'Hello :)',
            notificationStyle: 'success',
            dismissCountDown: 3,
        }
        notification.notificationMessage = message
        notification.notificationStyle = style
        notification.dismissCountDown = time
        store.notifications.push(notification)
    }
})

export const mutations = {
    switchAccessControl(value) {
        store.adminAccess = value;
    }
}
