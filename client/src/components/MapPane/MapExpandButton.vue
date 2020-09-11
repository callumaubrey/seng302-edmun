<template>
    <span>
        <i v-if="state" class="far map-expand-button text-secondary fa-caret-square-right" @click="toggleButton"></i>
        <i v-else class="far map-expand-button text-secondary fa-caret-square-left" @click="toggleButton"></i>
    </span>
</template>

<script>
    export default {
        name: "MapExpandButton",

        data: function () {
            return {
                state: true
            }
        },

        prop: {
            value: {
                type: Boolean,
                default: true
            }
        },

        methods: {
            toggleButton() {
                this.value = !this.value;
                this.state = this.value;
                this.$emit('input', this.value)
            }
        }
    }
</script>

<style scoped>
    .map-expand-button {
        opacity: 0.25;
    }

    .map-expand-button:hover {
        opacity: 1;
    }
</style>