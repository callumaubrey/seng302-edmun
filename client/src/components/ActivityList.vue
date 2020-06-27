<template>
    <div>
        <h3>Activities</h3>
        <p>Activity Results with the tag: {{ tag }}</p>
        <b-table
            :items="items"
            :fields="fields"
        ></b-table>
    </div>
</template>

<script>
    export default {
        name: "ActivityList",
        props: {
            tag: String,
            items: Array
        },
        data() {
            return {
                fields: ['name', 'description', 'location', 'start_date', 'end_date', 'activity_types']
            }
        }
    }
</script>