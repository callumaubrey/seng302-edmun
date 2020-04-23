<template>
    <div id="app">
        <NavBar isLoggedIn=true v-bind:userName="userName"></NavBar>
        <b-container>
            <b-row>
                <b-col>
                    <h1>Account settings</h1>
                </b-col>
            </b-row>
            <hr>
            <div  class="clickable" v-b-toggle="'collapse-2'">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Profile Info</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Edit basic profile information</b-col>
                        <b-col><p align="right"></p></b-col>
                    </b-row>
                </b-container>
            </div>
            <b-collapse id="collapse-2">
                <hr>
                <b-container>
                    <b-row>
                        <b-col sm="4">
                            <label>First Name</label>
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter name" :state="validateProfile('firstname')"  v-model ="$v.profileForm.firstname.$model" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid first name</b-form-invalid-feedback>
                        </b-col>

                        <b-col sm="4">
                            <label>Middle Name</label>
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter middle name" :state="validateProfile('middlename')" v-model ="$v.profileForm.middlename.$model" ></b-form-input>
                            <b-form-invalid-feedback>Invalid middle name</b-form-invalid-feedback>
                        </b-col>
                        <b-col sm="4">
                            <label>Last Name</label>
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter last name" :state="validateProfile('lastname')" :max="5" v-model ="$v.profileForm.lastname.$model" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid last name</b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Nickname"
                            >
                                <b-form-input v-on:input="resetProfileMessage()" placeholder="Enter nickname" :state="validateProfile('nickname')" v-model ="$v.profileForm.nickname.$model" ></b-form-input>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group
                                    label="Date of birth"

                            >
                                <b-form-input v-on:input="resetProfileMessage()" type="date" :state="validateProfile('date_of_birth')" v-model ="$v.profileForm.date_of_birth.$model"></b-form-input>
                                <b-form-invalid-feedback>Invalid date of birth</b-form-invalid-feedback>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group
                                    label="Gender"
                            >
                                <b-form-select v-on:change="resetProfileMessage()" :options="genderOptions" v-model="$v.profileForm.gender.$model"></b-form-select>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Fitness level"
                                    description="How fit are you?"
                            >
                                <b-form-select v-on:change="resetProfileMessage()" :options="fitnessOptions"  v-model="profileForm.fitness"></b-form-select>
                                {{profileForm.fitness}} Fitness here
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Bio"
                            >
                                <b-form-textarea v-on:input="resetProfileMessage()" placeholder="Let others know more about you" :state="validateProfile('bio')" v-model="$v.profileForm.bio.$model"></b-form-textarea>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-button v-on:click="saveProfileInfo()" >Save</b-button>
                            <b-form-valid-feedback :state='profileUpdateMessage != ""'>
                                {{profileUpdateMessage}}
                            </b-form-valid-feedback>
                            <b-form-invalid-feedback :state='profileErrorMessage == ""'>
                                {{profileErrorMessage}}
                            </b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                </b-container>
            </b-collapse>
            <hr>

            <div v-b-toggle="'collapse-1'" class="clickable">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Email Address</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Add or remove email addresses from your account</b-col>
                        <b-col><h6 align="right" >{{totalEmails()}} email address</h6></b-col>
                    </b-row>
                </b-container>
            </div>
            <b-collapse id="collapse-1">
                <b-container>
                    <hr>
                    <div id="emailBody">
                        <div v-for="(email, index) in primaryEmail" :key="index">
                            <b-row>
                                <b-col><label style="font-weight: bold">{{email}}</label></b-col>
                                <b-col><h6 align="right">Primary</h6></b-col>
                            </b-row>
                            <hr>
                        </div>
                        <div v-for="(email, index) in emails" :key="index" >
                            <b-row>
                                <b-col><label>{{email}}</label></b-col>
                                <b-col>
                                    <b-button class="invisible-btn" style="float: right;" @click="deleteEmail(index)">Remove</b-button>
                                    <b-button class="invisible-btn" style="float: right;" @click="makePrimary(index)">Make Primary</b-button>
                                </b-col>
                            </b-row>
                            <hr>
                        </div>
                        <b-row>
                            <b-col >
                                <b-form-group style="font-weight: bold" for="emailInput" description="Maximum of 5 emails allowed">
                                    <b-input v-if="this.emails.length + this.primaryEmail.length < 5" type="email" name="email"
                                             placeholder="john@example.com" :state="validateEmail('emailInput')" v-model="$v.emailForm.emailInput.$model"></b-input>
                                </b-form-group>
                                <b-form-valid-feedback :state='emailUpdateMessage != ""'>
                                    {{emailUpdateMessage}}
                                </b-form-valid-feedback>
                                <b-form-invalid-feedback :state='emailErrorMessage == ""'>
                                    {{emailErrorMessage}}
                                </b-form-invalid-feedback>
                            </b-col>

                            <b-col id="emailAdd" class="col-25">
                                <b-button v-if="this.emails.length + this.primaryEmail.length < 5" class="invisible-btn" style="float: right;" v-on:click="createEmail">Add</b-button>
                            </b-col>
                        </b-row>
                        <b-row>

                        </b-row>
                    </div>
                </b-container>
            </b-collapse>
            <hr>

            <div v-b-toggle="'collapse-3'" class = "clickable">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Passports</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Add or remove passports from your account</b-col>
                        <b-col><h6 align="right">{{totalPassports()}} passport</h6></b-col>
                    </b-row>
                </b-container>
            </div>
            <b-collapse id="collapse-3">
                <b-container>
                    <hr>
                    <div v-for="(country, index) in yourCountries" :key="index">
                        <b-row>
                            <b-col>
                                <label>{{country[0]}}</label>
                            </b-col>
                            <b-col>
                                <b-button class="invisible-btn" style="float: right;" @click="deletePassport(index)">Remove</b-button>
                            </b-col>
                        </b-row>
                        <hr>
                    </div>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    description="Add a new passport"
                            >
                                <b-form-select v-model="selectedCountry" :options="availCountries"></b-form-select>
                            </b-form-group>
                            <b-form-valid-feedback :state='passportsUpdateMessage != ""'>
                                {{passportsUpdateMessage}}
                            </b-form-valid-feedback>
                            <b-form-invalid-feedback :state='passportsErrorMessage == ""'>
                                {{passportsErrorMessage}}
                            </b-form-invalid-feedback>
                        </b-col>
                        <b-col>
                            <b-button class="invisible-btn" style="float: right;" v-on:click="addPassport">Add</b-button>
                        </b-col>
                    </b-row>

                </b-container>
            </b-collapse>
            <hr>
            <div v-b-toggle="'collapse-4'" class = "clickable">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Activities</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Add or remove activities from your account</b-col>
                        <b-col><h6 align="right">{{totalActivitys()}} activity </h6></b-col>
                    </b-row>
                </b-container>
            </div>

            <b-collapse id="collapse-4">
                <b-container>
                    <hr>
                    <div v-for="(activites, index) in yourActivites" :key="index">
                        <b-row>
                            <b-col>
                                <label>{{activites}}</label>
                            </b-col>
                            <b-col>
                                <b-button class="invisible-btn" style="float: right;" @click="deleteActivity(index)">Remove</b-button>
                            </b-col>
                        </b-row>
                        <hr>
                    </div>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    description="Add a new activity"
                            >
                                <b-form-select v-model="selectedActivity" :options="availActivitys"></b-form-select>
                            </b-form-group>
                            <b-form-valid-feedback :state='activityUpdateMessage != ""'>
                                {{activityUpdateMessage}}
                            </b-form-valid-feedback>
                            <b-form-invalid-feedback :state='activityErrorMessage == ""'>
                                {{activityErrorMessage}}
                            </b-form-invalid-feedback>

                        </b-col>
                        <b-col>
                            <b-button class="invisible-btn" style="float: right;" v-on:click="addActivity">Add</b-button>
                        </b-col>
                    </b-row>

                </b-container>
            </b-collapse>
            <hr>
            <div v-b-toggle="'collapse-5'" class="clickable">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Password</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Change your password</b-col>
                    </b-row>
                </b-container>
            </div>
            <b-collapse id = "collapse-5">
                <b-container>
                    <hr>
                    <b-row>
                        <b-col sm="6">
                            <label>Current Password</label>
                            <b-form-input type="password" id="input-default" placeholder="Enter current password" v-model ="passwordForm.oldPassword"></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row class="my-1">
                        <b-col sm="6">
                            <label>New Password</label>
                            <b-form-input type="password" id="password" placeholder="Enter new password" :state="validatePassword('password')" v-model="$v.passwordForm.password.$model"></b-form-input>
                            <b-form-invalid-feedback> Password should contain at least 8 characters with at least one digit, one lower case and one upper case</b-form-invalid-feedback>
                        </b-col>
                        <b-col sm="6">
                            <label>Repeat New Password</label>
                            <b-form-input id="repeatPassword" type="password" placeholder="Enter new password again" :state="validatePassword('passwordRepeat')" v-model ="$v.passwordForm.passwordRepeat.$model"></b-form-input>
                            <b-form-invalid-feedback id="email-error"> Passwords must be the same</b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col sm="1">
                            <b-button align="left" v-on:click="savePassword" >Save</b-button>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col sm="3">
                            <b-label id="passwordMessage"></b-label>
                        </b-col>
                    </b-row>
                </b-container>
            </b-collapse>
            <hr>
            <div v-b-toggle="'collapse-6'">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Location</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Change your current location</b-col>
                    </b-row>
                </b-container>
            </div>
            <b-collapse id = "collapse-6">
                <b-container>
                    <hr>
                    <b-row>
                        <b-col>
                            <p>Select a location from the search drop down:</p>
                            <b-input autocomplete="off" id="locationInput" class="form-control" type="text" v-model="location.display_name" @keyup.native="getLocationData"></b-input>
                            <div v-for="i in locations" :key="i.place_id">
                                <b-input v-on:click="setLocationInput(i)" type="button" :value=i.display_name></b-input>
                            </div>
                        </b-col>
                    </b-row>
                </b-container>
            </b-collapse>

            <hr>
        </b-container>
    </div>
</template>


<script>
    // import api from '../Api';
    import axios from 'axios'
    import NavBar from "@/components/NavBar.vue"
    import {email, helpers, maxLength, required, sameAs} from 'vuelidate/lib/validators'
    //const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));
    const nameValidate = helpers.regex('nameValidate', /^[a-zA-Z]+(([' -][a-zA-Z ])?[a-zA-Z]*)*$/); // Some names have ' or - or spaces so can't use alpha
    const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));

    const countryData = axios.create({
        baseURL: "https://restcountries.eu/rest/v2/all",
        timeout: 1000
    });

    const User = {
        name: 'User',

        components: {
            NavBar
        },
        data: function () {
            return {
                profileForm: {
                    lastname: "",
                    middlename: null,
                    firstname: "",
                    nickname: "",
                    bio: "",
                    date_of_birth: "",
                    gender: null,
                    fitness: null
                },
                passwordForm: {
                    oldPassword: null,
                    password: null,
                    passwordRepeat: null,
                },
                emailForm: {
                    emailInput : ""
                },
                profile_id: null,
                disabled: true,
                primaryEmail: [],
                locations: [],
                location: '',
                emails: [],
                yourCountries: [],
                yourActivites: [],
                passportsCode: [],
                availCountries: [],
                availActivitys: ["Hike", "Run", "Bike", "Swim", "Walk"],
                isLoggedIn: false,
                userName: "",
                selectedCountry: null,
                selectedActivity: null,
                fitnessOptions: [
                    {text: "Couch potato", value: 0},
                    {text: "Coming back from injury", value: 1},
                    {text: "Go for walks", value: 2},
                    {text: "Play sport", value: 3},
                    {text: "Marathon runner", value: 4}
                ],
                genderOptions: [
                    "Male",
                    "Female",
                    "Non-binary"
                ],
                userData: null,
                passportsUpdateMessage: "",
                passportsErrorMessage: "",
                profileUpdateMessage: "",
                profileErrorMessage: "",
                emailUpdateMessage: "",
                emailErrorMessage: "",

                passwordErrorMessage: "",
                passwordUpdateMessage: "",
                activityUpdateMessage: "",
                activityErrorMessage: ""
            }
        },
        validations: {
            profileForm: {
                firstname: {
                    required,
                    nameValidate,
                    maxLength: maxLength(40)
                },
                middlename: {
                    nameValidate,
                    maxLength: maxLength(40)
                },
                lastname: {
                    required,
                    nameValidate,
                    maxLength: maxLength(40)
                },
                nickname: {
                    maxLength: maxLength(40)
                },
                date_of_birth: {
                    required,
                    dateValidate(value) {
                        const oldestDate = new Date(1900, 0, 1); // JavaScript months start at 0
                        const date = new Date(value);
                        const today = new Date();
                        return date <= today && date >= oldestDate;
                    },
                },
                bio: {
                    maxLength: maxLength(200)
                },
                gender: {
                    required
                },
            },
            emailForm: {
                emailInput : {
                    required,
                    email,
                    uniqueEmail() {
                        if (this.emails.includes(this.emailForm.emailInput) || this.primaryEmail.includes(this.emailForm.emailInput)) {
                            return false
                        } else {
                            return true;
                        }
                    }
                }
            },
            passwordForm: {
                password: {
                    required,
                    passwordValidate
                },
                passwordRepeat: {
                    required,
                    sameAsPassword: sameAs('password')
                }
            }
        },



        methods: {
            validateState: function(name) {
                const { $dirty, $error } = this.$v[name];
                return $dirty ? !$error : null;
            },
            validateProfile: function(name) {
                const { $dirty, $error } = this.$v['profileForm'][name];
                return $dirty ? !$error : null;
            },
            validateEmail: function(name) {
                const { $dirty, $error } = this.$v['emailForm'][name];
                return $dirty ? !$error : null;
            },
            resetProfileMessage() {
                this.profileUpdateMessage = "";
                this.profileErrorMessage = "";
            },
            validatePassword: function(name){
                const { $dirty, $error } = this.$v['passwordForm'][name];
                return $dirty ? !$error : null;
            },
            saveProfileInfo() {
                this.$v.profileForm.$touch();
                if (this.$v.profileForm.$anyError) {
                    return;
                }
                const vueObj = this;
                this.axios.defaults.withCredentials = true;
                console.log(this.profileForm.fitness);
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id,{
                    firstname: this.profileForm.firstname,
                    middlename: this.profileForm.middlename,
                    lastname: this.profileForm.lastname,
                    nickname: this.profileForm.nickname,
                    primary_email: this.primaryEmail[0],
                    date_of_birth: this.profileForm.date_of_birth,
                    gender: this.profileForm.gender.toLowerCase(),
                    fitness: this.profileForm.fitness,
                    bio: this.profileForm.bio,
                    activities: this.yourActivites,
                    passports: this.passportsCode
                }).then(function (response) {
                    vueObj.emailErrorMessage = "";
                    vueObj.profileUpdateMessage = response.data;
                }).catch(function (error) {
                    vueObj.profileUpdateMessage = "";
                    vueObj.profileErrorMessage = error.response.data;
                });
            },
            totalPassports() {
                return this.yourCountries.length;
            },
            totalEmails() {
                return this.primaryEmail.length + this.emails.length;
            },
            totalActivitys() {
                return this.yourActivites.length;
            },
            addPassport() {
                if (this.selectedCountry && !this.passportsCode.includes(this.selectedCountry[1])) {
                    const tempPassports = this.yourCountries.slice();
                    const tempCodes = this.passportsCode.slice();
                    const vueObj = this;
                    tempPassports.push(this.selectedCountry);
                    const addedPassport = this.selectedCountry;
                    tempCodes.push(this.selectedCountry[1]);
                    this.axios.put("http://localhost:9499/profiles/" + this.profile_id, {
                        firstname: this.profileForm.firstname,
                        middlename: this.profileForm.middlename,
                        lastname: this.profileForm.lastname,
                        nickname: this.profileForm.nickname,
                        primary_email: this.primaryEmail[0],
                        date_of_birth: this.profileForm.date_of_birth,
                        gender: this.profileForm.gender.toLowerCase(),
                        fitness: this.profileForm.fitness,
                        bio: this.profileForm.bio,
                        activities: this.yourActivites,
                        passports: tempCodes
                    }).then(function (response) {
                        if (response.status == 200) {
                            vueObj.passportsUpdateMessage = addedPassport[0] + " was successfully added to passports";
                            vueObj.yourCountries = tempPassports;
                            vueObj.passportsCode = tempCodes;
                        } else {
                            vueObj.passportsUpdateMessage = "";
                            vueObj.passportsErrorMessage = "Failed to add " + addedPassport + " to passports, please try again later";
                        }
                    }).catch(function () {
                            vueObj.passportsUpdateMessage = "";
                            vueObj.passportsErrorMessage = "Failed to add " + addedPassport + " to passports, please try again later";
                    });
                }
            },
            addActivity() {
                if (this.selectedActivity && !this.yourActivites.includes(this.selectedActivity)) {
                    this.yourActivites.push(this.selectedActivity);
                    const vueObj = this;
                    const addedActivity = this.selectedActivity;
                    this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/activity-types", {
                        activities: this.yourActivites
                    }).then(function (response) {
                        console.log(vueObj.yourActivites);
                        if (response.status == 200) {
                            vueObj.activityUpdateMessage = addedActivity + " was successfully added to activity's"
                        }
                    }).catch(function (error) {
                        if (error.response.status == 400) {
                            vueObj.activityUpdateMessage = "";
                            vueObj.activityUpdateMessage = "Failed to add " + addedActivity + " to activitys, please try again later";
                        }
                    });
                }
            },
            deletePassport(index) {
                const vueObj = this;
                const tempPassports = this.yourCountries.slice();
                const tempCodes = this.passportsCode.slice();
                const removedPassport = (tempPassports.splice(index, 1))[0];
                tempCodes.splice(index, 1);
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id, {
                    firstname: this.profileForm.firstname,
                    middlename: this.profileForm.middlename,
                    lastname: this.profileForm.lastname,
                    nickname: this.profileForm.nickname,
                    primary_email: this.primaryEmail[0],
                    date_of_birth: this.profileForm.date_of_birth,
                    gender: this.profileForm.gender.toLowerCase(),
                    fitness: this.profileForm.fitness,
                    bio: this.profileForm.bio,
                    activities: this.yourActivites,
                    passports: tempCodes
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.passportsUpdateMessage = removedPassport[0] + " has been successfully removed from passports";
                        vueObj.yourCountries = tempPassports;
                        vueObj.passportsCode = tempCodes;
                    } else {
                        vueObj.passportsUpdateMessage = "";
                        vueObj.passportsErrorMessage = "Failed to remove " + removedPassport + " from passports, please try again later";
                    }
                }).catch(function () {
                        vueObj.passportsUpdateMessage = "";
                        vueObj.passportsErrorMessage = "Failed to remove " + removedPassport + " from passports, please try again later";
                });
            },
            deleteActivity(index) {
                const vueObj = this;
                const deletedActivity = (this.yourActivites.splice(index, 1));
                // Need to change to the new activities api
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/activity-types", {
                    activities: this.yourActivites
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.activityUpdateMessage = deletedActivity + " was successfully deleted from activities"
                    }
                }).catch(function (error) {
                    if (error.response.status == 400) {
                        vueObj.activityUpdateMessage = "";
                        vueObj.activityUpdateMessage = "Failed to delete " + deletedActivity + " from activities";
                    }
                });
            },
            setLocationInput: function (location) {
                document.getElementById("locationInput").value = location.display_name;
                const vueObj = this;
                vueObj.locations = []
                vueObj.location = location
                console.log(this.location.place_id, this.location.type.toUpperCase())

                if (this.location != null){
                    let data = {
                        id: this.location.place_id,
                        type: "RELATION"
                    }
                    console.log(data)
                    this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/location", data).then(function (response) {
                        console.log(response)
                    }).catch(function (error) {
                        console.log(error)
                    });
                }
                else {
                    this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/location", {
                    }).then(function (response) {
                        console.log(response)
                    }).catch(function (error) {
                        console.log(error)
                    });
                }
            },
            getLocationData: async function () {
                var locationText = document.getElementById("locationInput").value;
                if (locationText == ''){
                    return
                }
                var locationData = this.axios.create({
                    baseURL: 'https://nominatim.openstreetmap.org/search/city/?q="' + locationText + '"&format=json&limit=5',
                    timeout: 1000,
                    withCredentials: false,
                });
                console.log('https://nominatim.openstreetmap.org/search?q="' + locationText + '"&format=json&limit=5');
                var data = await (locationData.get());
                this.locations = data.data
            },
            getCountryData: async function() {
                var data = await (countryData.get());
                var countriesLen = data.data.length;
                for (var i = 0; i < countriesLen; i++) {
                    this.availCountries.push({ text: data.data[i].name, value: [data.data[i].name, data.data[i].alpha3Code]})
                }
            },
            makePrimary(index) {
                const tempEmails = this.emails.slice();
                const tempPrimary = this.primaryEmail.slice();
                let oldPrimary = tempPrimary[0];
                tempPrimary.splice(0, 1);
                let newPrimary = tempEmails[index];
                tempPrimary.push(newPrimary);
                tempEmails.splice(index, 1);
                tempEmails.push(oldPrimary);
                const vueObj = this;
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/emails", {
                    primary_email: tempPrimary[0],
                    additional_email: tempEmails
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.emailErrorMessage = "";
                        vueObj.emailUpdateMessage = newPrimary + " was successfully updated as primary";
                        vueObj.emails = tempEmails;
                        vueObj.primaryEmail = tempPrimary;
                    } else {
                        vueObj.emailUpdateMessage = "";
                        vueObj.emailErrorMessage = "Failed to update " + newPrimary + " as primary, please try again later";
                    }
                }).catch(function () {
                    vueObj.emailUpdateMessage = "";
                    vueObj.emailErrorMessage = "Failed to update " + newPrimary + " as primary, please try again later";
                });
            },
            deleteEmail(index) {
                const tempEmails= this.emails.slice();
                const removedEmail = tempEmails.splice(index, 1)[0];
                const vueObj = this;
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/emails", {
                    primary_email: this.primaryEmail[0],
                    additional_email: tempEmails
                }).then(function (response) {
                    if (response.status == "200") {
                        vueObj.emailErrorMessage = "";
                        vueObj.emailUpdateMessage = removedEmail + " was successfully removed from your emails";
                        vueObj.emails = tempEmails;
                    } else {
                        vueObj.emailUpdateMessage = "";
                        vueObj.emailErrorMessage = "Failed to remove " + removedEmail + " from your emails, please try again later"
                    }

                }).catch(function () {
                    vueObj.emailUpdateMessage = "";
                    vueObj.emailErrorMessage = "Failed to remove " + removedEmail + " from your emails, please try again later"
                });

            },
            createEmail: function () {
                this.$v.emailForm.$touch();
                if (this.$v.emailForm.$anyError) {
                    return;
                }

                var newEmail = this.emailForm.emailInput;
                let newEmails = this.emails.slice();
                newEmails.push(newEmail);
                const vueObj = this;
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/emails", {
                    primary_email: this.primaryEmail[0],
                    additional_email: newEmails
                }).then(function (response) {
                    if (response.status == "200") {
                        console.log("hello");
                        vueObj.emailErrorMessage = "";
                        vueObj.emailUpdateMessage = newEmail + " was successfully added to your emails";
                        vueObj.emails = newEmails;
                        vueObj.$v.emailForm.$reset();
                    } else {
                        vueObj.emailUpdateMessage = "";
                        vueObj.emailErrorMessage = "Failed to add " + newEmail + " to your emails, please try again later";
                    }
                }).catch(function () {
                    vueObj.emailUpdateMessage = "";
                    vueObj.emailErrorMessage = "Failed to add " + newEmail + " to your emails, please try again later";
                });
            },
            // (R)ead
            getAll: () => countryData.get('students', {
                transformResponse: [function (data) {
                    return data ? JSON.parse(data).embedded.students : data;
                }]
            }),
            getProfileData: async function () {
                let vueObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/user')
                    .then(function (response) {
                        console.log(response.data);
                        for (let i = 0; i < response.data.passports.length; i++) {
                            vueObj.passportsCode.push(response.data.passports[i].isoCode);
                            vueObj.yourCountries.push([response.data.passports[i].countryName, response.data.passports[i].isoCode]);
                        }
                        for (let j = 0; j < response.data.additional_email.length; j++) {
                            vueObj.emails.push(response.data.additional_email[j].address);
                        }
                        console.log(response.data);
                        vueObj.profileForm.firstname = response.data.firstname;
                        vueObj.profileForm.middlename = response.data.middlename;
                        vueObj.profileForm.lastname = response.data.lastname;
                        vueObj.profileForm.nickname = response.data.nickname;
                        vueObj.profileForm.gender = response.data.gender;
                        if (vueObj.profileForm.gender) {
                            vueObj.profileForm.gender = vueObj.profileForm.gender.charAt(0).toUpperCase() + vueObj.profileForm.gender.slice(1);
                        }
                        vueObj.profileForm.date_of_birth = response.data.date_of_birth;
                        vueObj.primaryEmail = [response.data.primary_email.address];
                        vueObj.profileForm.fitness = response.data.fitness;
                        vueObj.profileForm.bio = response.data.bio;
                        vueObj.isLoggedIn = true;
                        vueObj.userName = response.data.firstname;
                        vueObj.yourActivites = response.data.activities;
                        // vueObj.location = response.data.location.osmID.id;
                    })
                    .catch(function () {
                        vueObj.isLoggedIn = false;
                        vueObj.$router.push('/login');
                    });
            },
            getUserId: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profile_id = response.data;
                    })
                    .catch(function () {
                    });
            },
            savePassword: function () {
                console.log(this.passwordForm.oldPassword);
                console.log(this.passwordForm.password);
                console.log(this.passwordForm.passwordRepeat);
                let currentObj = this;
                this.$v.passwordForm.$touch();
                if (this.$v.passwordForm.$anyError) {
                    return;
                }
                this.axios.defaults.withCredentials = true;
                this.axios.put("http://localhost:9499/profiles/" + this.profile_id + "/password",{
                    old_password: this.passwordForm.oldPassword,
                    new_password: this.passwordForm.password,
                    repeat_password: this.passwordForm.passwordRepeat
                }).then(function (response) {
                    if (response.status == 200) {
                        console.log("anything");
                        currentObj.output = response.data;
                        currentObj.passwordForm.oldPassword = null;
                        currentObj.passwordForm.password = null;
                        currentObj.passwordForm.passwordRepeat = null;
                        currentObj.$v.passwordForm.$reset();
                        document.getElementById("passwordMessage").textContent = response.data;
                        document.getElementById("passwordMessage").style.color = "green";
                    }
                }).catch(function (error) {
                    currentObj.output = error.response.data;
                    currentObj.passwordForm.oldPassword = null;
                    currentObj.passwordForm.password = null;
                    currentObj.passwordForm.passwordRepeat = null;
                    currentObj.$v.passwordForm.$reset();
                    document.getElementById("passwordMessage").textContent = error.response.data;
                    document.getElementById("passwordMessage").style.color = "red";
                })
            }
        },

        mounted: function () {
            this.getUserId();
            this.getProfileData();
        },
        beforeMount() {
            this.getCountryData();
        }

        // need to create a API
    };

    export default User
</script>

<style scoped>

    [v-cloak] {
        display: none;
    }

    .edit-title {
        font-weight: bold;
    }

    .invisible-btn {
        background-color: Transparent;
        background-repeat:no-repeat;
        border: none;
        cursor:pointer;
        overflow: hidden;
        outline:none;
        color: blue;
        padding-right: 0;
        font-size: 14px;
    }
    .invisible-btn:hover {
        background-color: lightblue;
    }
    .update-message {
        font-size: 13px;
    }
    .clickable {
        cursor: pointer;
    }
    h5 {
        color: #1278FD;
    }
</style>