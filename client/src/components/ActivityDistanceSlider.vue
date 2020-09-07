<template>
    <div>
        <b-row style="padding: 1.5em">
            <b-col cols="10">
                <input type="range" :min="minSliderValue" :max="maxSliderValue" v-model="distance" class="slider"
                       id="distance-slider" @change="emitDistance">
            </b-col>
            <b-col align="center">
                <label>
                    Radius: {{distance}}
                </label>
            </b-col>
        </b-row>
    </div>
</template>

<script>
    export default {
        name: "ActivityDistanceSlider",
        data() {
            return {
                distance: 50,
                minSliderValue: 1,
                maxSliderValue: 200
            }
        },
        methods: {
            /**Emits a 'distanceChange' event when the slider value has been changed
             * Used for the radius for searching for activities.
             */
            emitDistance: function () {
                this.$emit('distanceChange', this.distance);
            }
        },
    }
</script>

<style scoped>
    .slider {
        width: 100%;
    }
</style>