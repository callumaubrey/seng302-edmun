<template>
    <div>
        <b-row>
            <b-col>
                <b-button id="allButton" :pressed.sync="allButtonPressedState" @click="toggleAllStateButton" pill
                          class="time-selection-button" variant="outline-primary">All
                </b-button>
                <b-button id="continuousButton" :pressed.sync="continuousButtonPressedState"
                          @click="toggleContinuousStateButton" pill class="time-selection-button" variant="outline-primary">
                    Continuous
                </b-button>
                <b-button v-b-toggle="'durationCollapse'" id="durationButton" :pressed.sync="durationButtonPressedState"
                          @click="toggleDurationStateButton" pill class="time-selection-button" variant="outline-primary">
                    Duration
                </b-button>
            </b-col>
        </b-row>
        <br v-if="durationButtonPressedState">
        <b-row>
            <b-col>
                <b-collapse id="durationCollapse" v-model="durationButtonPressedState">
                    <b-row align-v="center" class="mb-2">
                        <b-col cols="3">
                            <label>Start date: </label>
                        </b-col>
                        <b-col>
                            <b-form-input id="start-date-input" v-model=startDate type="date" @change="emitDates" :state="validDatesStates" ></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row align-v="center" class="mb-2">
                        <b-col cols="3">
                            <label>End date: </label>
                        </b-col>
                        <b-col>
                            <b-form-input id="end-date-input" v-model=endDate type="date" @change="emitDates" :state="validDatesStates" ></b-form-input>
                        </b-col>
                    </b-row>
                </b-collapse>
            </b-col>
        </b-row>


    </div>
</template>

<script>
    export default {
        name: "ActivityContinuousDurationSearchBox.vue",
        props: ["startDateProp", "endDateProp"],
        data() {
            return {
                allButtonPressedState: true,
                continuousButtonPressedState: false,
                durationButtonPressedState: false,
            }
        },
        methods: {
            /**
             * Sets the all button to selected and deselects other options
             */
            toggleAllStateButton: function () {
                this.allButtonPressedState = true;
                this.continuousButtonPressedState = false;
                this.durationButtonPressedState = false;
                this.emitSelected()
            },
            /**
             * Sets the continuous button to selected and deselects other options
             */
            toggleContinuousStateButton: function () {
                this.allButtonPressedState = false;
                this.continuousButtonPressedState = true;
                this.durationButtonPressedState = false;
                this.emitSelected()
            },
            /**
             * Sets the duration button to selected and deselects other options
             */
            toggleDurationStateButton: function () {
                this.allButtonPressedState = false;
                this.continuousButtonPressedState = false;
                this.durationButtonPressedState = true;
                this.emitSelected();
                this.emitDates();
            },
            emitSelected: function () {
                this.$emit("selected", this.currentlySelected)
            },
            emitDates: function () {
                let duration = {
                    startDate: this.startDate,
                    endDate: this.endDate
                };
                this.$emit("dates", duration)
            },
        },
        computed: {
            /**
             * Returns a string of the button which is currently selected
             */
            currentlySelected: function () {
                if (this.continuousButtonPressedState) {
                    return "continuous"
                } else if (this.durationButtonPressedState) {
                    return "duration"
                } else {
                    return "all"
                }
            },
            validDatesStates: function() {
                if (this.startDate && this.endDate) {
                    return this.validDates ? null : false;
                } else {
                    return null
                }
            },
            validDates: function() {
                return new Date(this.startDate) <= new Date(this.endDate);
            }
        },
        mounted: function() {
            if (this.startDateProp  == undefined && this.endDateProp == undefined) {
                let today = new Date();
                let year = today.getFullYear();
                let month = today.getMonth() + 1;
                let monthStr = "";
                if (month < 10) {
                    monthStr = "0" + month;
                } else {
                    monthStr = month;
                }
                let date = today.getDate();
                console.log(date)
                let dateStr = "";
                if (date < 10) {
                    dateStr = "0" + date;
                } else {
                    dateStr = date;
                }
                let todayString = year + "-" + monthStr + "-" + dateStr;
                this.startDate = todayString;
                this.endDate = todayString;
            } else {
                this.startDate = this.startDateProp;
                this.endDate = this.endDateProp;
            }

        }
    }
</script>

<style scoped>
    .time-selection-button {
        margin-right: 5px;
    }

</style>