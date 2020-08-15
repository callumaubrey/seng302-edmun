<template>
    <b-input size="lg" id="name-input-field" v-model="searchQuery" v-on:keyup="emitQuery" placeholder="Search Activity"></b-input>
</template>


<script>

    export default {
        name: "ActivityNameSearch.vue",

        props: {
            value: {
                default: '',
                type: String
            }
        },
        methods: {
            emitQuery() {
                this.$emit('input', this.searchQuery)
            },
        }
    }
</script>


