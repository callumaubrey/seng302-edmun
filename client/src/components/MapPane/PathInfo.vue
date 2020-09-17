<template>
    <div>
        <b-col style="margin: 1em">
            <hr>
            <b-row>
                <span><b>Selected Location:</b></span>
            </b-row>
            <b-row>
                <span>Name: {{selectedPath.name}}</span>
            </b-row>
            <b-row>
                <span>Coordinates: {{selectedPath.lat}}, {{selectedPath.lng}}</span>
            </b-row>
            <b-row>
                <span>Point Number: {{selectedPath.number}}</span>
            </b-row>
            <hr>
        </b-col>

        <b-list-group style="overflow: auto; max-height: 25em; max-width: 30em">
            <b-list-group-item style="cursor: pointer" v-for="point in points" :key="point.id" @click="pointSelected(point)">
                <b>{{point.id + 1}}:</b>    {{point.name}}
            </b-list-group-item>
        </b-list-group>
    </div>

</template>

<script>

    export default {
        name: "PathInfo",
        components: {
        },
        props: {
            points: Array,
        },
        data() {
            return {
                selectedPath: {
                    name: '',
                    lat: '',
                    lng: '',
                    number: ''
                }
            }
        },
        methods: {
            pointSelected(point){
                this.selectedPath.name = ''
                this.selectedPath.lat = point.position[0]
                this.selectedPath.lng = point.position[1]
                this.selectedPath.number = point.id + 1
            }
        }
        }
</script>

<style scoped>

</style>