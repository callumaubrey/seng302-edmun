<template>
    <span v-b-tooltip.hover :title="icon_name" class="fa-stack fa-2x">
        <i class="fas fa-square fa-stack-2x activity-type-icon"></i>
        <i :class="icon_class"></i>
    </span>
</template>

<script>
    const activity_type_icons = {
        'Run':'fa-running',
        'Bike':'fa-biking',
        'Hike':'fa-hiking',
        'Walk':'fa-walking',
        'Swim':'fa-swimmer',
    };

    export default {
        name: "ActivityTypeIcon",

        data: () => {
            return {
                icon_name: '',
                icon_class: []
            }
        },

        props: {
            type_name: {
                type: String,
                default: ''
            }
        },

        mounted: function() {
            this.setIconFromTypeName();
        },

        watch: {
            type_name: 'setIconFromTypeName'
        },

        methods: {
            setIconFromTypeName() {
                this.icon_name = '';
                this.icon_class = [];

                if (this.type_name in activity_type_icons) {
                    this.icon_class.push('fas');
                    this.icon_class.push(activity_type_icons[this.type_name]);
                    this.icon_class.push('fa-stack-1x');
                    this.icon_class.push('fa-inverse');

                    this.icon_name = this.type_name
                }
            }
        }
    }
</script>

<style scoped>
    .activity-type-icon {
        color: #fd1e64;
    }
</style>