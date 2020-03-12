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
                            <b-form-input id="input-default" placeholder="Enter name"  v-model ="firstname" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid first name</b-form-invalid-feedback>
                        </b-col>

                        <b-col sm="4">
                            <label>Middle Name</label>
                            <b-form-input id="input-default" placeholder="Enter middle name" v-model ="middlename" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid middle name</b-form-invalid-feedback>
                        </b-col>
                        <b-col sm="4">
                            <label>Last Name</label>
                            <b-form-input id="input-default" placeholder="Enter last name" v-model ="lastname" required trim></b-form-input>
                            <b-form-invalid-feedback>Invalid last name</b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group
                                label="Nickname"
                            >
                                <b-form-input v-model="nickName"></b-form-input>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group
                                    label="Date of birth"

                            >
                                <b-form-input type="date" v-model="date_of_birth"></b-form-input>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group
                                    label="Gender"
                            >
                                <b-form-select :options="genderOptions" v-model="gender"></b-form-select>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <hr>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Fitness level"
                            >
                                <b-form-select :options="fitnessOptions" v-model="fitness"></b-form-select>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-form-group
                                    label="Bio"
                            >
                                <b-form-textarea v-model="bio"></b-form-textarea>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-button v-on:click="saveProfileInfo()" >Save</b-button>
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
                                    <b-input type="email" id="emailInput" name="email"
                                             placeholder="john@example.com" required></b-input>
                                    </b-form-group>
                                </b-col>
                                <b-col id="emailAdd" class="col-25">
                                    <b-button class="invisible-btn" style="float: right;" v-on:click="createEmail">Add</b-button>
                                </b-col>
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
                profile_id: null,
                disabled: true,
                lastname: "",
                middlename: null,
                firstname: "",
                nickName: "",
                primaryEmail: [],
                emails: [],
                bio: "",
                date_of_birth: "",
                gender: null,
                fitness: "",
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
            }
        },


        methods: {
            saveProfileInfo() {
                this.axios.defaults.withCredentials = true;
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id,{
                      firstname: this.firstname,
                      middlename: this.middlename,
                      lastname: this.lastname,
                      nickname: this.nickName,
                      dob: this.date_of_birth,
                      gender: this.gender.toLowerCase(),
                      fitness: this.fitness,
                      bio: this.bio
                  }).then(function (response) {
                      console.log(response);
                  }).catch(function (error) {
                      console.log(error);
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
                    this.yourCountries.push(this.selectedCountry);
                    this.passportsCode.push(this.selectedCountry[1]);
                    this.passportPatchRequest();
                }
            },
            deletePassport(index) {
                this.yourCountries.splice(index, 1);
                this.passportsCode.splice(index, 1);
                this.passportPatchRequest();
            },
            passportPatchRequest() {
                this.axios.patch("http://localhost:9499/profile/" + this.profile_id, {
                    passports: this.passportsCode
                }).then(function (response) {
                    console.log(response);
                }).catch(function (error) {
                    console.log(error);
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
                var oldPrimary = this.primaryEmail[0];
                this.primaryEmail.splice(0, 1);
                this.primaryEmail.push(this.emails[index]);
                this.emails.splice(index, 1);
                this.emails.push(oldPrimary);
            },
            deleteEmail(index) {
              this.emails.splice(index, 1);
            },
            createEmail: function () {
                var numEmails = this.emails.length;
                if (numEmails >= 4){
                    console.log("Max Emails (limit is 5)");
                    return
                }
                var newEmail = document.getElementById("emailInput").value;
                if (!newEmail.includes("@")){
                    return
                }
                this.emails.push(newEmail);
            },
            // (R)ead
            getAll: () => countryData.get('students', {
                transformResponse: [function (data) {
                    return data ? JSON.parse(data).embedded.students : data;
                }]
            }),
            getProfileData: async function () {
                this.axios.defaults.withCredentials = true;
                var data = await (profileData.get());
                for (var i = 0; i < data.data.passports.length; i++) {
                    this.passportsCode.push(data.data.passports[i].isoCode);
                    this.yourCountries.push([data.data.passports[i].countryName, data.data.passports[i].isoCode]);
                }
                this.userData = data.data;
                console.log(this.userData);
                this.firstname = this.userData.firstname;
                this.middlename = this.userData.middlename;
                this.lastname = this.userData.lastname;
                this.nickName = this.userData.nickname;
                this.gender = this.userData.gender;
                this.gender = this.gender.charAt(0).toUpperCase() + this.gender.slice(1);
                this.date_of_birth = this.userData.dob;
                this.primaryEmail = [this.userData.email];
                this.fitness = this.userData.fitness;
                this.bio = this.userData.bio;
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
</style>
>