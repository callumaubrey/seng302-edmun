<template>
    <div>
        <b-row>
            <b-col cols="10">
                <input type="range"
                       :disabled="disabled"
                       :min="0" :max="1"
                       step="0.005"
                       v-model="raw_value"
                       class="slider"
                       id="distance-slider" @change="emitDistance">
            </b-col>
            <b-col align="center">
                <label :class="{'text-secondary': disabled}">
                    {{distance}} km
                </label>
            </b-col>
        </b-row>
    </div>
</template>

<script>
    /**
     * Added value easing to the range so that users have finer control on lower values where it may be more important.
     * This was done to reflect that radius is exponentially proportional to area. By taking the slider value 0-1 and
     * squaring it, it becomes reflective of the area and not the radius. This is more human understandable than the
     * radius.
     */
    export default {
        name: "ActivityDistanceSlider",
        data: function() {
            return {
                // Uses raw value between 0-1 for value easing
                raw_value: 0.25
            };
        },
        props: {
            distance: {
                type: Number,
                default: 50.0
            },
            minSliderValue: {
                type: Number,
                default: 1.0
            },
            maxSliderValue: {
                type: Number,
                default: 200.0
            },
            disabled: {
                type: Boolean,
                default: false
            }
        },
        model: {
            prop: 'distance',
            event: 'distanceChange'
        },
        watch: {
            distance: function() {
                // Convert distance between 0-1
                let interpolation = (this.distance - this.minSliderValue) / (this.maxSliderValue - this.minSliderValue);
                this.raw_value = Math.sqrt(interpolation); // Ease value
            }
        },
        methods: {
            /**Emits a 'distanceChange' event when the slider value has been changed
             * Used for the radius for searching for activities.
             */
            emitDistance: function () {
                // Apply easing to distance value
                let interpolation = this.raw_value * this.raw_value;
                this.distance = Math.round(interpolation * (this.maxSliderValue - this.minSliderValue) + this.minSliderValue);

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