<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <div class="container">
            <b-container fluid>
                <b-row align-h="between">
                    <b-col>
                        <b-button @click="goToActivity()" style="float: right;" variant="primary">View activity
                        </b-button>
                        <h3>Edit Your Activity: {{ form.name }}</h3>
                        <hr>
                    </b-col>
                </b-row>
                <b-form @submit.stop.prevent="onSubmit" novalidate>
                    <b-row>
                        <b-col>
                            <b-form-group>
                                <b-form-radio-group id="duration-type-group" v-model="isContinuous">
                                    <b-form-radio name="duration-type" value='0'>Continuous</b-form-radio>
                                    <b-form-radio name="duration-type" value='1'>Duration</b-form-radio>
                                </b-form-radio-group>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row v-if="isContinuous == '1'">
                        <b-col>
                            <b-form-group id="start-date-input-group" label="Start Date" label-for="start-date-input">
                                <b-form-input
                                        :state="validateDurationState('startDate')"
                                        aria-describedby="start-date-feedback"
                                        id="start-date-input"
                                        type="date"
                                        v-model="$v.durationForm.startDate.$model"
                                ></b-form-input>
                                <b-form-invalid-feedback id="start-date-feedback">This is a required field. Start date
                                    must be earlier than end date
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group id="end-date-input-group" label="End Date" label-for="end-date-input">
                                <b-form-input
                                        :state="validateDurationState('endDate')"
                                        aria-describedby="end-date-feedback"
                                        id="end-date-input"
                                        type="date"
                                        v-model="$v.durationForm.endDate.$model"
                                ></b-form-input>
                                <b-form-invalid-feedback id="end-date-feedback">This is a required field. Start date
                                    must be earlier than end date
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row style="margin-bottom:10px;border-bottom:1px solid #ececec;" v-if="isContinuous == '1'">
                        <b-col>
                            <b-form-group id="start-time-input-group" label="Start Time" label-for="start-time-input">
                                <b-form-input
                                        :state="validateDurationState('startTime')"
                                        id="start-time-input"
                                        type="time"
                                        v-model="$v.durationForm.startTime.$model"
                                ></b-form-input>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group id="end-time-input-group" label="End Time" label-for="end-time-input">
                                <b-form-input
                                        :state="validateDurationState('endTime')"
                                        id="end-time-input"
                                        type="time"
                                        v-model="$v.durationForm.endTime.$model"
                                ></b-form-input>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <span v-if="this.form.selectedActivityTypes.length > 0">
                                Activity types:
                            </span>
                            <hr>
                            <div :key="index" v-for="(activityType, index) in this.form.selectedActivityTypes">
                                <b-row>
                                    <b-col>
                                        <label>{{activityType}}</label>
                                    </b-col>
                                    <b-col>
                                        <b-button @click="deleteActivityType(index)" class="invisible-btn"
                                                  style="float: right;">Remove
                                        </b-button>
                                    </b-col>
                                </b-row>
                            </div>
                            <hr>
                            <b-form-group id="activity-type-group" label="Add Activity Type" label-for="activity-type">
                                <b-form-select
                                        :options="activityTypes"
                                        :state="validateState('selectedActivityType')"
                                        aria-describedby="activity-type-feedback"
                                        id="activity-type"
                                        name="activity-type"
                                        v-model="$v.form.selectedActivityType.$model"
                                        v-on:change="addActivityType()"
                                ></b-form-select>
                                <b-form-invalid-feedback id="activity-type-feedback">Please select an activity type.
                                </b-form-invalid-feedback>
                            </b-form-group>
                            <hr>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-form-group id="name-input-group" label="Name" label-for="name-input">
                                <b-form-input
                                        :state="validateState('name')"
                                        aria-describedby="name-feedback"
                                        id="name-input"
                                        name="name-input"
                                        v-model="$v.form.name.$model"
                                ></b-form-input>
                                <b-form-invalid-feedback id="name-feedback">This is a required field.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <b-form-group id="description-input-group" label="Description"
                                          label-for="description-input">
                                <b-form-textarea
                                        :state="validateState('description')"
                                        id="description-input"
                                        name="description-input"
                                        placeholder="How did it go?"
                                        v-model="$v.form.description.$model"
                                ></b-form-textarea>
                                <b-form-invalid-feedback id="name-feedback">This is a required field.
                                </b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                    </b-row>

                    <b-row>
                        <b-col>
                            <b-form-group id="location-input-group" label="Location" label-for="location-input">
                                <b-form-input
                                        :state="validateState('location')"
                                        id="location-input"
                                        name="location-input"
                                        placeholder="Your location.."
                                        v-model="$v.form.location.$model"
                                ></b-form-input>
                            </b-form-group>
                            <b-form-valid-feedback :state='activityUpdateMessage != ""' class="feedback">
                                {{activityUpdateMessage}}
                            </b-form-valid-feedback>
                            <b-form-invalid-feedback :state='activityErrorMessage == ""' class="feedback">
                                {{activityErrorMessage}}
                            </b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <b-button id="saveButton" type="submit" variant="primary">Save changes</b-button>
                </b-form>
            </b-container>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import {validationMixin} from "vuelidate";
    import {required} from 'vuelidate/lib/validators';

    export default {
        mixins: [validationMixin],
        components: {
            NavBar
        },
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                isContinuous: '',
                profileId: null,
                activityId: null,
                activityTypes: ["Hike", "Bike", "Run", "Walk", "Swim"],
                activityUpdateMessage: "",
                activityErrorMessage: "",
                form: {
                    name: null,
                    description: null,
                    selectedActivityType: 0,
                    selectedActivityTypes: [],
                    // These values will need to be converted to uppercase before axios request is sent
                    date: null,
                    location: 'Kaikoura, NZ'
                },
                durationForm: {
                    startDate: null,
                    endDate: null,
                    startTime: null,
                    endTime: null
                }
            }
        },
        validations: {
            form: {
                name: {
                    required
                },
                description: {},
                selectedActivityType: {
                    required
                },
                date: {},
                location: {}
            },
            durationForm: {
                startDate: {
                    validateDate() {
                        let startDate = new Date(this.durationForm.startDate);
                        let endDate = new Date(this.durationForm.endDate);
                        if (startDate > endDate) {
                            return false;
                        }
                        return true;
                    },
                    required
                },
                endDate: {
                    validateDate() {
                        let startDate = new Date(this.durationForm.startDate);
                        let endDate = new Date(this.durationForm.endDate);
                        if (startDate > endDate) {
                            return false;
                        }
                        return true;
                    },
                    required
                },
                startTime: {},
                endTime: {}
            }
        },
        methods: {
            getActivity: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                //TODO: change the get endpoint after it is being implemented in backend
                this.axios.get('http://localhost:9499/profiles/activity-types')
                    .then(function (response) {
                        currentObj.form.name = response.data.activity_name;
                        currentObj.form.description = response.data.description;
                        currentObj.activityTypes = response.data.activity_type;
                        if (response.data.continuous == false) {
                            currentObj.isContinuous = '1';
                            [currentObj.durationForm.startDate, currentObj.durationForm.startTime] = currentObj.convertISOtoDateTime(response.data.start_time);
                            [currentObj.durationForm.endDate, currentObj.durationForm.endTime] = currentObj.convertISOtoDateTime(response.data.end_time);
                        } else {
                            currentObj.isContinuous = '0';
                        }
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },
            validateState(name) {
                const {$dirty, $error} = this.$v.form[name];
                return $dirty ? !$error : null;
            },
            validateDurationState(name) {
                const {$dirty, $error} = this.$v.durationForm[name];
                return $dirty ? !$error : null;
            },
            addActivityType() {
                if (this.form.selectedActivityType == 0) {
                    return;
                }
                if (!this.form.selectedActivityTypes.includes(this.form.selectedActivityType)) {
                    this.form.selectedActivityTypes.push(this.form.selectedActivityType);
                }
            },
            deleteActivityType(index) {
                this.form.selectedActivityTypes.splice(index, 1);
            },
            onSubmit() {
                this.activityErrorMessage = "";
                this.activityUpdateMessage = "";
                this.$v.form.$touch();
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                if (this.isContinuous == '0') {
                    if (this.$v.form.$anyError) {
                        return;
                    }
                    this.axios.put("http://localhost:9499/profiles/" + this.profileId + "/activities/" + this.activityId, {
                        activity_name: this.form.name,
                        description: this.form.description,
                        activity_type: this.form.selectedActivityTypes,
                        continuous: true,
                        location: this.form.location
                    })
                        .then(function (response) {
                            console.log(response);
                            currentObj.activityUpdateMessage = "Successfully update activity: " + currentObj.form.name;
                            currentObj.activityErrorMessage = "";
                        })
                        .catch(function (error) {
                            console.log(error.response.data);
                            currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data + ". Please try again";
                            currentObj.activityUpdateMessage = "";
                        });

                } else {
                    this.$v.durationForm.$touch();
                    if (this.$v.durationForm.$anyError || this.$v.form.$anyError) {
                        return;
                    }
                    const isoDates = this.getISODates();
                    this.axios.put("http://localhost:9499/profiles/" + this.profileId + "/activities/" + this.activityId, {
                        activity_name: this.form.name,
                        description: this.form.description,
                        activity_type: this.form.selectedActivityTypes,
                        continuous: false,
                        start_time: isoDates[0],
                        end_time: isoDates[1],
                        location: this.form.location
                    })
                        .then(function (response) {
                            console.log(response);
                            currentObj.activityUpdateMessage = "Successfully update activity: " + currentObj.form.name;
                            currentObj.activityErrorMessage = "";
                        })
                        .catch(function (error) {
                            console.log(error);
                            currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data + ". Please try again";
                            currentObj.activityUpdateMessage = ""
                        });

                }
            },
            getISODates: function () {
                const startDate = new Date(this.durationForm.startDate + " " + this.durationForm.startTime);
                const endDate = new Date(this.durationForm.endDate + " " + this.durationForm.endTime);
                let startDateISO = startDate.toISOString().slice(0, -5);
                let endDateISO = endDate.toISOString().slice(0, -5);

                var currentTime = new Date();
                const offset = (currentTime.getTimezoneOffset());

                const currentTimezone = (offset / 60) * -1;
                if (currentTimezone !== 0) {
                    startDateISO += currentTimezone > 0 ? '+' : '';
                    endDateISO += currentTimezone > 0 ? '+' : '';
                }
                startDateISO += currentTimezone.toString() + "00";
                endDateISO += currentTimezone.toString() + "00";
                // alert(startDate + "\n" + startDateISO)
                return [startDateISO, endDateISO];
            },
            convertISOtoDateTime: function (ISODate) {
                const date = ISODate.substring(0, 10);
                const time = ISODate.substring(11, 16);
                return [date, time]
            },
            getUserId: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profileId = response.data;
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function () {
                    });
            },
            getPlaceHolderActivity: function () {
                this.form.name = "Tramping";
                this.form.description = "tramping iz fun";
                this.form.selectedActivityTypes = ["Hike", "Bike"];
                this.isContinuous = '1';
                [this.durationForm.startDate, this.durationForm.startTime] = this.convertISOtoDateTime("2020-02-20T08:00:00+1300");
                [this.durationForm.endDate, this.durationForm.endTime] = this.convertISOtoDateTime("2020-02-21T08:00:00+1300");
                this.form.location = "Kaikoura, NZ";
                this.profileId = 55;
            },
            goToActivity: function () {
                this.$router.push('/profiles/' + this.profileId + '/activities/' + this.activityId);
            }
        },
        mounted: function () {
            // this.getActivity();
            this.getPlaceHolderActivity();
            this.activityId = this.$route.params.activityId;
            // this.getUserId();
        },

    }
</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .container {
        background-color: #f2f2f2;
        padding: 20px 20px 20px 20px;
        border: 1px solid lightgrey;
        border-radius: 3px;
    }

    .invisible-btn {
        background-color: Transparent;
        background-repeat: no-repeat;
        border: none;
        cursor: pointer;
        overflow: hidden;
        outline: none;
        color: blue;
        font-size: 14px;
    }

    .feedback {
        padding-bottom: 10px;
    }

</style>