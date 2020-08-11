<template>
    <b-col>
        <b-input id="name-input-field" v-model="searchQuery" v-on:keyup="emitQuery" placeholder="Search Activity"></b-input>
    </b-col>
</template>


<script>

    export default {
        name: "ActivityNameSearch.vue",

        data() {
            return {
                searchQuery: null,
            }
        },
        methods: {
            emitQuery() {
                this.$emit('updateSearchQuery', this.searchQuery)
            },
        }
    }
</script>


