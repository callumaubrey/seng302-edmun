<template>
    <div>
        <b-card border-variant="grey" style="margin: 0.5em">
            <b>Total Distance:</b>

            <hr style="margin: 0.5em">
            <b-col>
                <b-row>
                    <span><b>Selected Location:</b></span>
                </b-row>

                <b-row>
                    <span>Latitude: {{selectedPath.lat}}</span>
                </b-row>
                <b-row>
                    <span>Longitude: {{selectedPath.lng}}</span>
                </b-row>
                <b-row>
                    <span>Point Number: {{selectedPath.number}}</span>
                </b-row>
                <b-row>
                    <span>Distance To Next Point: {{selectedPath.number}} km</span>
                </b-row>
            </b-col>
        </b-card>
        <b-row style="margin: 1em; display: flex">
            <b-col align="center">
                <b-button style="margin: 0.5em" v-on:click="directions = false" size="sm" variant="outline-primary">Pins</b-button>
                <b-button style="margin: 0.5em" v-on:click="directions = true" size="sm" variant="outline-primary">Directions</b-button>
            </b-col>

        </b-row>
        <hr>

        <b-list-group style="overflow: auto; max-height: 20em; max-width: 30em; margin: 0.5em" v-if="!directions">
            <b-list-group-item style="cursor: pointer" v-for="point in points" :key="point.id" @click="pointSelected(point)">
                <b>{{point.id + 1}}:</b>    {{point.name}}
            </b-list-group-item>
        </b-list-group>

        <b-list-group style="overflow: auto; max-height: 20em; max-width: 30em; margin: 0.5em" v-else>
            <b-list-group-item style="cursor: pointer" v-for="point in points" :key="point.id" @click="pointSelected(point)">
<!--                <b>{{point.id + 1}}:</b>    -->
                Test Location
            </b-list-group-item>
        </b-list-group>
    </div>
</template>

<script>

    export default {
        name: "PathInfo",
        components: {
        },
        /** The points of the path of which information are displayed for*/
        props: {
            points: Array,
        },
        data() {
            return {
                //The selected path point being displayed
                selectedPath: {
                    name: '',
                    lat: '',
                    lng: '',
                    number: ''
                },
                directions:false
            }
        },
        methods: {
            /**
             * Changes the selected point in the pathInfo list
             * This is used for displaying the location, name and point number
             * @param point
             */
            pointSelected(point){
                //TODO: Set a point number instead of the ID, Set the Name of the point
                this.selectedPath.name = '';
                this.selectedPath.lat = point.position[0];
                this.selectedPath.lng = point.position[1];
                this.selectedPath.number = point.id + 1;
            }
        },
        mounted() {
            console.log(this.points)
        }
    }
</script>

<style scoped>

</style>