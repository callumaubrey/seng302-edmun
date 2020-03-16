<template>
    <div id="app">
        <NavBar isLoggedIn=true></NavBar>
        <b-container>
            <b-row>
                <b-col>
                    <h1>Account settings</h1>
                </b-col>
            </b-row>
            <hr>
            <div v-b-toggle="'collapse-2'">
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
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter name" :state="validateState('firstname')"  v-model ="$v.profileForm.firstname.$model" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid first name</b-form-invalid-feedback>
                        </b-col>

                        <b-col sm="4">
                            <label>Middle Name</label>
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter middle name" :state="validateState('middlename')" v-model ="$v.profileForm.middlename.$model" ></b-form-input>
                            <b-form-invalid-feedback>Invalid middle name</b-form-invalid-feedback>
                        </b-col>
                        <b-col sm="4">
                            <label>Last Name</label>
                            <b-form-input v-on:input="resetProfileMessage()" id="input-default" placeholder="Enter last name" :state="validateState('lastname')" :max="5" v-model ="$v.profileForm.lastname.$model" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid last name</b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Nickname"
                            >
                                <b-form-input v-on:input="resetProfileMessage()" placeholder="Enter nickname" :state="validateState('nickname')" v-model ="$v.profileForm.nickname.$model" ></b-form-input>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group
                                    label="Date of birth"

                            >
                                <b-form-input v-on:input="resetProfileMessage()" type="date" :state="validateState('date_of_birth')" v-model ="$v.profileForm.date_of_birth.$model"></b-form-input>
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
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Bio"
                            >
                                <b-form-textarea v-on:input="resetProfileMessage()" placeholder="Let others know more about you" :state="validateState('bio')" v-model="$v.profileForm.bio.$model"></b-form-textarea>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-button v-on:click="saveProfileInfo()" >Save</b-button>
                            <b-form-valid-feedback :state='profileUpdateMessage != ""'>
                                {{profileUpdateMessage}}
                            </b-form-valid-feedback>
                            <b-form-invalid-feedback :state='profileErrorMessage != ""'>
                                {{profileErrorMessage}}
                            </b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                </b-container>
            </b-collapse>
            <hr>

            <div v-b-toggle="'collapse-1'">
                <b-container>
                    <b-row>
                        <b-col><h3 class=edit-title>Email Address</h3></b-col>
                        <b-col><h5 align="right">Change</h5></b-col>
                    </b-row>
                    <b-row>
                        <b-col>Add or remove email addresses from your account</b-col>
                        <b-col><h6 align="right">{{totalEmails()}} email address</h6></b-col>
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
                                <b-form-group style="font-weight: bold" for="emailInput" description="Add a new email (Max 5)">
                                    <b-input type="email" name="email"
                                             placeholder="john@example.com" :state="validateEmail('emailInput')" v-model="$v.emailForm.emailInput.$model"></b-input>
                                </b-form-group>
                                <b-form-valid-feedback :state='emailUpdateMessage != ""'>
                                    {{emailUpdateMessage}}
                                </b-form-valid-feedback>
                                <b-form-invalid-feedback :state='emailErrorMessage != ""'>
                                    {{emailErrorMessage}}
                                </b-form-invalid-feedback>
                            </b-col>
                            <b-col id="emailAdd" class="col-25">
                                <b-button class="invisible-btn" style="float: right;" v-on:click="createEmail">Add</b-button>
                            </b-col>
                        </b-row>
                        <b-row>

                        </b-row>
                    </div>

                </b-container>
            </b-collapse>
            <hr>

            <div v-b-toggle="'collapse-3'">
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
                            <b-form-invalid-feedback :state='passportsErrorMessage != ""'>
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
        </b-container>
    </div>
</template>


<script>
    // import api from '../Api';
    import axios from 'axios'
    import NavBar from "@/components/NavBar.vue"
    import {required, helpers, maxLength, email} from 'vuelidate/lib/validators'
    //const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));
    const nameValidate = helpers.regex('nameValidate', /^[a-zA-Z]+(([' -][a-zA-Z ])?[a-zA-Z]*)*$/); // Some names have ' or - or spaces so can't use alpha

    const profileData = axios.create({
        baseURL: "http://localhost:9499/profile/2",
        timeout: 1000
    });

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
                    nickName: "",
                    bio: "",
                    date_of_birth: "",
                    gender: null,
                    fitness: ""
                },
                emailForm: {
                    emailInput : ""
                },
                profile_id: null,
                disabled: true,
                primaryEmail: [],
                emails: [],

                yourCountries: [],
                passportsCode: [],
                availCountries: [],
                isLoggedIn: false,
                selectedCountry: null,
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
                emailErrorMessage: ""
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
                    maxLength: maxLength(3),
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
            }
        },



        methods: {
            validateState: function(name) {
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
            saveProfileInfo() {
                this.$v.profileForm.$touch();
                if (this.$v.profileForm.$anyError) {
                    return;
                }
                const vueObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id,{
                    firstname: this.profileForm.firstname,
                    middlename: this.profileForm.middlename,
                    lastname: this.profileForm.lastname,
                    nickname: this.profileForm.nickName,
                    dob: this.profileForm.date_of_birth,
                    gender: this.profileForm.gender.toLowerCase(),
                    fitness: this.profileForm.fitness,
                    bio: this.profileForm.bio
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.profileErrorMessage = "";
                        vueObj.profileUpdateMessage = "Profile successfully updated";
                    }
                }).catch(function (error) {
                    if (error.response.status == 400) {
                        vueObj.profileUpdateMessage = "";
                        vueObj.profileErrorMessage = "Failed to update profile, please try again later";
                    }
                });
            },
            totalPassports() {
                return this.yourCountries.length;
            },
            totalEmails() {
                return this.primaryEmail.length + this.emails.length;
            },
            addPassport() {
                if (this.selectedCountry && !this.passportsCode.includes(this.selectedCountry[1])) {
                    console.log(this.selectedCountry);
                    const vueObj = this;
                    this.yourCountries.push(this.selectedCountry);
                    const addedPassport = this.selectedCountry;
                    this.passportsCode.push(this.selectedCountry[1]);
                    this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                        passports: this.passportsCode
                    }).then(function (response) {
                        console.log(response);
                        console.log(response.status)
                        if (response.status == 200) {
                            vueObj.passportsUpdateMessage = addedPassport[0] + " was successfully added to passports"
                        }
                    }).catch(function (error) {
                        if (error.response.status == 400) {
                            vueObj.passportsUpdateMessage = "";
                            vueObj.passportsErrorMessage = "Failed to add " + addedPassport + " to passports, please try again later";
                            vueObj.yourCountries.push(addedPassport);
                            vueObj.passportsCode.push(addedPassport[1]);
                        }
                    });
                }
            },
            deletePassport(index) {
                const vueObj = this;
                const removedPassport = (this.yourCountries.splice(index, 1))[0];
                this.passportsCode.splice(index, 1);
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                    passports: this.passportsCode
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.passportsUpdateMessage = removedPassport[0] + " has been successfully removed from passports"
                    }
                }).catch(function (error) {
                    if (error.response.status == 400) {
                        vueObj.passportsUpdateMessage = "";
                        vueObj.passportsErrorMessage = "Failed to remove " + removedPassport + " from passports, please try again later";
                        vueObj.yourCountries.push(removedPassport);
                        vueObj.passportsCode.push(removedPassport[1]);
                    }
                });
            },
            getCountryData: async function() {
                var data = await (countryData.get());
                var countriesLen = data.data.length;
                for (var i = 0; i < countriesLen; i++) {
                    this.availCountries.push({ text: data.data[i].name, value: [data.data[i].name, data.data[i].alpha3Code]})
                }
                console.log(data.data.length)
                console.log(this.availCountries);
            },
            makePrimary(index) {
                let oldPrimary = this.primaryEmail[0];
                this.primaryEmail.splice(0, 1);
                let newPrimary = this.emails[index];
                this.primaryEmail.push(newPrimary);
                this.emails.splice(index, 1);
                this.emails.push(oldPrimary);
                const vueObj = this;
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                    primaryemail: newPrimary,
                    additionalemail: this.emails
                }).then(function (response) {
                    if (response.status == 200) {
                        vueObj.emailUpdateMessage = newPrimary + " was successfully updated as primary"
                    }
                }).catch(function (error) {
                    console.log(error);
                    if (error.response.status == 400) {
                        vueObj.emailErrorMessage = "Failed to update " + newPrimary + " as primary, please try again later"
                    }
                });
            },
            deleteEmail(index) {
                const removedEmail = this.emails.splice(index, 1)[0];
                const vueObj = this;
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                    additionalemail: this.emails
                }).then(function (response) {
                    if (response.status == "200") {
                        vueObj.emailUpdateMessage = removedEmail + " was successfully removed from your emails"
                    }

                }).catch(function (error) {
                    console.log(error);
                    if (error.response.status == 400) {
                        vueObj.emailErrorMessage = "Failed to remove " + removedEmail + " from your emails, please try again later"
                    }
                });

            },
            createEmail: function () {
                this.$v.emailForm.$touch();
                if (this.$v.emailForm.$anyError) {
                    return;
                }
                var numEmails = this.emails.length;
                if (numEmails >= 4){
                    console.log("Max Emails (limit is 5)");
                    return
                }
                var newEmail = this.emailForm.emailInput;
                this.emails.push(newEmail);
                console.log(this.emails);
                console.log(this.profile_id);
                const vueObj = this;
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                    additionalemail: this.emails
                }).then(function (response) {
                    if (response.status == "200") {
                        vueObj.emailUpdateMessage = newEmail + " was successfully added to your emails"
                    }
                    console.log(response);
                    console.log(response.status)
                }).catch(function (error) {
                    console.log(error);
                    if (error.response.status == 400) {
                        console.log(error);
                        vueObj.emailErrorMessage = "Failed to add " + newEmail + " to your emails, please try again later"
                    }
                });
            },
            // (R)ead
            getAll: () => countryData.get('students', {
                transformResponse: [function (data) {
                    return data ? JSON.parse(data).embedded.students : data;
                }]
            }),
            getProfileData: async function () {
                this.axios.defaults.withCredentials = true;
                let data = await (profileData.get());
                for (let i = 0; i < data.data.passports.length; i++) {
                    this.passportsCode.push(data.data.passports[i].isoCode);
                    this.yourCountries.push([data.data.passports[i].countryName, data.data.passports[i].isoCode]);
                }
                console.log(data.data.additionalemail);
                for (let j = 0; j < data.data.additionalemail.length; j++) {
                    this.emails.push(data.data.additionalemail[j].address);
                }
                console.log(this.emails);
                this.userData = data.data;
                console.log(this.userData);
                this.profileForm.firstname = this.userData.firstname;
                this.profileForm.middlename = this.userData.middlename;
                this.profileForm.lastname = this.userData.lastname;
                this.profileForm.nickName = this.userData.nickname;
                this.profileForm.gender = this.userData.gender;
                this.profileForm.gender = this.profileForm.gender.charAt(0).toUpperCase() + this.profileForm.gender.slice(1);
                this.profileForm.date_of_birth = this.userData.dob;
                this.primaryEmail = [this.userData.email.address];
                this.profileForm.fitness = this.userData.fitness;
                this.profileForm.bio = this.userData.bio;
            },
            getUserSession: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profile/user')
                    .then(function (response) {
                        console.log(response.data);
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        currentObj.isLoggedIn = false;
                    });
            },
            getUserId: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profile/id')
                    .then(function (response) {
                        currentObj.profile_id = response.data;
                        console.log(currentObj.profile_id);
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            }
        },

        mounted: function () {
            this.getUserId();
            this.getProfileData();
            this.getUserSession();
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
</style>
>