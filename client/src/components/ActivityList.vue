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
    import axios from 'axios';

    export default {
        name: "ActivityList",
        props: {
            tag: String
        },
        data() {
            return {
                items: [],
                fields: ['name', 'description', 'location', 'start_date', 'end_date', 'activity_types']
            }
        },
        methods: {
            getActivities: function() {
                if (!this.tag) {
                    return;
                }

                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/activities/' + this.tag)
                    .then((res) => {
                        this.items = res.data;
                        for (let i = 0; i < res.data.length; i++) {
                            this.items[i].activityTypes = this.items[i].activityTypes.join(", ");
                        }
                    })
                    .catch((err) => {
                        console.log(err);
                    });
            }
        },
        mounted() {
            this.getActivities();
        }
    }
</script>