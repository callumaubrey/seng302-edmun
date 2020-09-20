<template>
    <div>
        <b-tabs pills card align="center">
            <!-- Marker Info -->
            <b-tab active class="p-0 overflow-auto" style="max-height: 565px" id="pathInfoMarkerTab">
                <!-- Icon -->
                <template v-slot:title>
                    <i class="fas fa-map-marker-alt info-category-icon"></i>
                </template>

                <b-list-group>
                    <b-list-group-item  v-for="keypoint in keypointInfo" :key="keypoint.index"
                                        :class="{'selected_keypoint': selectedKeypoint === keypoint.index}"
                                        style="cursor: pointer" @click="selectKeypoint(keypoint.index)">
                        <b-row>
                            <!-- Marker Icon -->
                            <b-col style="flex: 0 0 35px; font-size: 0.75em" class="p-0">
                                <span v-if="keypoint.type==='start'" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:#2af582"></i>
                                  <i class="fas fa-map-marker-alt fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else-if="keypoint.type==='end'" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:Tomato"></i>
                                  <i class="fas fa-flag-checkered fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:#1295ff"></i>
                                  <i class="fas fa-map-marker-alt fa-stack-1x fa-inverse"></i>
                                </span>
                            </b-col>

                            <!-- Marker Info -->
                            <b-col>
                                <span  v-if="keypoint.distance!==null"
                                        v-b-tooltip.hover title="To next point">{{keypoint.distance}} km</span>
                                <span v-else>Finish, {{totalDistance}} km Total</span>

                                <br>
                                <span class="text-secondary">{{keypoint.lat}}, {{keypoint.lng}}</span>
                            </b-col>
                        </b-row>
                    </b-list-group-item>
                </b-list-group>
            </b-tab>

            <!-- Directions -->
            <b-tab class="p-0 overflow-auto" style="max-height: 565px" id="pathInfoDirectionTab"
                   :disabled="path.type==='STRAIGHT'" >
                <!-- Icon -->
                <template v-slot:title>
                    <i class="fas fa-directions info-category-icon"></i>
                </template>


                <b-list-group  v-for="step in directionSteps" :key="step.index">
                    <b-list-group-item :id="isStepTypeGoal(step) ? `PathInfoStepDistanceKeypoint_${step.keypoint_index}` : undefined"
                                       :class="{'selected_keypoint': selectedKeypoint === step.keypoint_index,
                                                'clickable': isStepTypeGoal(step)}"
                                       @click="selectKeypoint(step.keypoint_index)">
                        <b-row align-v="center">
                            <!-- Marker Icon -->
                            <b-col style="flex: 0 0 35px; font-size: 0.75em" class="p-0">
                                <span v-if="step.end_goal" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:Tomato"></i>
                                  <i class="fas fa-flag-checkered fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else-if="isStepTypeGoal(step)" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:#1295ff"></i>
                                  <i class="fas fa-map-marker-alt fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else class="fa-stack fa-2x">
                                  <i class="fas fa-square fa-stack-2x" style="color:#ab6eff"></i>
                                  <i :class="getDirectionStepIconClasses(step)"></i>
                                </span>
                            </b-col>

                            <!-- Marker Info -->
                            <b-col>
                                <span v-if="!isStepTypeGoal(step)">{{step.distance}} m<br></span>
                                <span class="text-secondary">{{step.instruction}}</span>
                            </b-col>
                        </b-row>
                    </b-list-group-item>
                </b-list-group>
            </b-tab>
        </b-tabs>
    </div>
</template>

<script>
    import L from 'leaflet';
    import axios from "axios";


    const directionIconTypes = [
        'fa-long-arrow-alt-left', // Left
        'fa-long-arrow-alt-right', // Right
        'fa-long-arrow-alt-left', // Sharp left
        'fa-long-arrow-alt-right', // Sharp right
        'fa-long-arrow-alt-left', // Slight left
        'fa-long-arrow-alt-right', // Slight right
        'fa-long-arrow-alt-up', // Straight
        'fa-sync-alt', // Enter roundabout
        'fa-sync-alt', // Exit roundabout
        'fa-undo', // U-turn
        'fa-flag-checkered', // Goal
        'fa-long-arrow-alt-up', // Depart
        'fa-long-arrow-alt-left', // Keep left
        'fa-long-arrow-alt-right', // Kepp right

    ];

    export default {
        name: "PathInfo",
        components: {
        },
        /** The points of the path of which information are displayed for*/
        props: {
            path: Object,
            selectedKeypoint: Number
        },
        model: {
            prop: 'selectedKeypoint',
            event: 'selected'
        },
        data() {
            return {
                keypointInfo: [],
                totalDistance: 0,

                directionSegments: [],
                directionSteps: []
            }
        },
        methods: {

            /**
             * Generates keypointInfo with keypoint information such as
             * distance, lat, lng.
             **/
            generateKeypointInfoFromPath() {
                this.keypointInfo = [];
                this.totalDistance = 0;
                for(let i = 0; i < this.path.locations.length; i++) {
                    let keypoint = this.path.locations[i];


                    // Calculate distance
                    let distance = null;
                    if(i < this.path.locations.length-1) {
                        if(this.path.type === "STRAIGHT") {
                            let next_keypoint = this.path.locations[i+1];
                            distance = this.getStraightLineDistanceBetweenKeypoints(keypoint, next_keypoint);
                        } else {
                            distance = this.directionSegments[i].distance / 1000;
                        }

                       this.totalDistance += distance;
                       distance = distance.toFixed(2);
                    }


                    // Calculate type
                    let type = "keypoint";
                    if(i === 0) type = "start";
                    if(i === this.path.locations.length-1) type = "end";

                    this.keypointInfo.push({
                        index: i,
                        lat: keypoint.latitude.toFixed(6),
                        lng: keypoint.longitude.toFixed(6),
                        distance: distance,
                        type: type
                    });
                }

                this.totalDistance = this.totalDistance.toFixed(2);
            },

            /**
             *  Get distance between two locations in kilometers.
             **/
            getStraightLineDistanceBetweenKeypoints(keypointA, keypointB) {
                let a = L.latLng(keypointA.latitude, keypointA.longitude);
                let b = L.latLng(keypointB.latitude, keypointB.longitude);
                return a.distanceTo(b) / 1000.0
            },

            /**
             * Selects a keypoint to show to user
             * @param index keypoint index
             */
            selectKeypoint(index) {
                if(index !== undefined) {
                    this.$emit('selected', index);
                }
            },


            /**
             * Calls openrouteservice api to get direction info.
             */
            getDirectionInfo: async function() {
                // Create api keypoints from path locations
                let keypoints = [];
                for(const keypoint of this.path.locations) {
                    keypoints.push([keypoint.longitude, keypoint.latitude]);
                }

                // Get direction route using keypoints
                let res = await axios.post("https://api.openrouteservice.org/v2/directions/foot-hiking/geojson",
                    {
                        coordinates: keypoints,
                    },
                    {headers: {Authorization: "5b3ce3597851110001cf6248183abbef295f42049b13e7a011f98247"}})

                this.directionSegments = res.data.features[0].properties.segments;
            },

            isStepTypeGoal(step) {
                return parseInt(step.type)===10;
            },

            generateDirectionInfo() {
                let i = 0;
                let keypoint_index = 1;
                this.directionSteps = [];
                for(const segment of this.directionSegments) {
                    for(let step of segment.steps) {
                        step.index = i;
                        i++;
                        step.end_goal=false;
                        if(this.isStepTypeGoal(step)) {
                            step.keypoint_index = keypoint_index;
                            keypoint_index++;
                            if(keypoint_index===this.path.locations.length) step.end_goal=true;
                        }

                        // Round distance to nearest 50 if greater than 50
                        if(step.distance > 50) step.distance = Math.round(step.distance / 50) * 50;

                        this.directionSteps.push(step);
                    }
                }
            },

            getDirectionStepIconClasses(step) {
                let classes = {
                    'fas':true,
                    'fa-stack-1x':true,
                    'fa-inverse':true
                };
                classes[directionIconTypes[step.type]] = true;
                return classes;
            }
        },
        async mounted() {
            await this.getDirectionInfo();
            this.generateKeypointInfoFromPath();
            this.generateDirectionInfo();
        },
        watch: {
            path: async function() {
                await this.getDirectionInfo();
                this.generateKeypointInfoFromPath();
                this.generateDirectionInfo();
            },
            selectedKeypoint: function () {
                // Set offset for keypoint info
                let key_offset = 74 * this.selectedKeypoint;


                // Set offset for direction info
                let dir_offset = 0;
                if(this.selectedKeypoint!==0) {
                    let keypoint_element = document.getElementById(`PathInfoStepDistanceKeypoint_${this.selectedKeypoint}`);
                    dir_offset = keypoint_element.offsetTop - 74;
                }

                let key_tab = document.getElementById("pathInfoMarkerTab");
                let dir_tab = document.getElementById("pathInfoDirectionTab");
                key_tab.scrollTo({top:key_offset, behavior:'smooth'});
                dir_tab.scrollTo({top:dir_offset, behavior:'smooth'});
            }
        }
    }
</script>

<style scoped>
    .info-category-icon {
        font-size: 2em;
    }
    .selected_keypoint {
        background-color: #cff0ff;
    }
    .clickable {
        cursor: pointer;
    }
</style>