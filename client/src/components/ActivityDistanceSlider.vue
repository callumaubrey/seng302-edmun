<template>
    <div class="slidecontainer">
        <input type="range" min="1" max="100" v-model="distance" class="slider" id="distance-slider" @change="emitDistance">
    </div>
</template>

<script>
    export default {
        name: "ActivityDistanceSlider",
        data() {
            return {
                distance: 50,
            }
        },
        methods: {
            emitDistance: function() {
                this.$emit('distanceChange', this.distance);
            }
        },
    }
</script>

<style scoped>
    .slider {
        width: 100%; /* Full-width */
    }
</style>