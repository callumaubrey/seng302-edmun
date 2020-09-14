<template>
    <span>
        <i v-if="value" class="far map-expand-button text-secondary fa-caret-square-right" @click="toggleButton"></i>
        <i v-else class="far map-expand-button text-secondary fa-caret-square-left" @click="toggleButton"></i>
    </span>
</template>

<script>
    export default {
        name: "MapExpandButton",

        props: {
            value: Boolean
        },

        methods: {
            toggleButton() {
                this.$emit('input', !this.value)
            }
        }
    }
</script>

<style scoped>
    .map-expand-button {
        opacity: 0.25;
        cursor: pointer;
    }

    .map-expand-button:hover {
        opacity: 1;
    }
</style>