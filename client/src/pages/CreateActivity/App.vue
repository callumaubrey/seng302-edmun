<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>

        <b-container>
            <b-row>
                <h3>Add an Activity</h3>
                <hr>
            </b-row>
            <b-form @submit.stop.prevent="onSubmit">
                <b-row>
                    <b-col>
                        <b-form-radio v-model="isContinuous" name="duration_type" value="1">Continuous</b-form-radio>
                        <b-form-radio v-model="isContinuous" name="duration_type" value="0">Duration</b-form-radio>
                    </b-col>
                </b-row>

                <b-row v-if="isContinuous == '0'">
                    <b-col>
                        <b-form-group id="start-date-input-group" label="Start Date" label-for="date-input">
                            <b-form-input
                                    id="start-date-input"
                                    type="date"
                                    :state="validateState('startDate')"
                                    v-model="$v.form.startDate.$model"
                                    required
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                    <b-col>
                        <b-form-group id="end-date-input-group" label="End Date" label-for="date-input">
                            <b-form-input
                                    id="end-date-input"
                                    type="date"
                                    :state="validateState('endDate')"
                                    v-model="$v.form.endDate.$model"
                                    required
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row v-if="isContinuous == '0'" style="margin-bottom:10px;border-bottom:1px solid #ececec;">
                    <b-col>
                        <b-form-group id="start-time-input-group" label="Start Time" label-for="date-input">
                            <b-form-input
                                    id="start-date-input"
                                    type="time"
                                    :state="validateState('startTime')"
                                    v-model="$v.form.startTime.$model"
                                    required
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                    <b-col>
                        <b-form-group id="end-time-input-group" label="End Time" label-for="date-input">
                            <b-form-input
                                    id="end-time-input"
                                    type="time"
                                    :state="validateState('endTime')"
                                    v-model="$v.form.endTime.$model"
                                    required
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col>
                        <b-form-group id="activity-type-group" label="Type" label-for="activity-type">
                            <b-form-select
                                    id="activity-type"
                                    name="activity-type"
                                    v-model="$v.form.selectedActivityType.$model"
                                    :state="validateState('selectedActivityType')"
                                    :options="form.activityTypes"
                            ></b-form-select>
                        </b-form-group>
                    </b-col>

                    <b-col>
                        <b-form-group id="date-input-group" label="Date" label-for="date-input">
                            <b-form-input
                                    id="date-input"
                                    type="date"
                                    :state="validateState('date')"
                                    v-model="$v.form.date.$model"
                                    required
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col>
                        <b-form-group id="name-input-group" label="Name" label-for="name-input">
                            <b-form-input
                                    id="name-input"
                                    name="name-input"
                                    v-model="$v.form.name.$model"
                                    :state="validateState('name')"
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col>
                        <b-form-group id="description-input-group" label="Description" label-for="description-input">
                            <b-form-textarea
                                    id="description-input"
                                    name="description-input"
                                    v-model="$v.form.description.$model"
                                    :state="validateState('description')"
                                    placeholder="How did it go?"
                            ></b-form-textarea>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col>
                        <b-form-group id="location-input-group" label="Location" label-for="location-input">
                            <b-form-input
                                    id="location-input"
                                    name="location-input"
                                    v-model="$v.form.location.$model"
                                    :state="validateState('location')"
                                    placeholder="Your location.."
                            ></b-form-input>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-button type="submit" variant="primary">Submit</b-button>
            </b-form>
        </b-container>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import { validationMixin } from "vuelidate";
    import { required } from 'vuelidate/lib/validators';

    export default {
        mixins: [validationMixin],
        components: {
            NavBar
        },
        data() {
            return {
                isLoggedIn: true,
                userName: 'Callum Aubrey',
                isContinuous: null,
                form: {
                    name: null,
                    description: null,
                    selectedActivityType: null,
                    activityTypes: [
                        { value: 0, text: 'Run' }
                    ],
                    date: null,
                    startDate: null,
                    endDate: null,
                    startTime: null,
                    endTime: null,
                    location: ''
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
                startDate: {},
                endDate: {},
                startTime: {},
                endTime: {},
                location: {}
            }
        },
        methods: {
            validateState(name) {
                const { $dirty, $error } = this.$v.form[name];
                return $dirty ? !$error : null;
            },
            onSubmit() {
                this.$v.form.$touch();
                if (this.$v.form.$anyError) {
                    return;
                }
            }
        }
    }
</script>