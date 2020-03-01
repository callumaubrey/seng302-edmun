<template>
    <div id="app">
        <div class="container">
            <div class="row" style="padding: 1em">
                <h1 class="col-50">Your Profile.</h1>
                <input class="button" type="button" v-on:click="toggleEdits" :value="editButtonText">
            </div>
            <hr>
            <div class="row">
                <div class="col-25">
                    <label style="font-weight: bold" for="fName">First name:</label>
                </div>
                <div class="col-25">
                    <input type="text" id="fName" :value="firstname" :disabled="disabled" required>
                </div>
                <div class="col-25">
                    <label style="font-weight: bold" for="dob">Date of Birth:</label>
                </div>
                <div class="col-25">
                    <input type="date" id="dob" :value="date_of_birth" :disabled="disabled" required>
                </div>
            </div>
            <div class="row">
                <div class="col-25">
                    <label style="font-weight: bold" for="lname">Last Name:</label>
                </div>
                <div class="col-25">
                    <input type="text" id="lname" :value="lastname" :disabled="disabled" required>
                </div>
                <div class="col-25">
                    <label style="font-weight: bold">Gender:</label>
                </div>
                <div class="col-25">
                    <div class="row">
                        <div class="col-33">
                            <label class="gender-label" for="female"><input id="female" name="gender" type="radio">F</label><br>
                        </div>
                        <div class="col-33">
                            <label class="gender-label" for="male"><input id="male" name="gender" type="radio">M</label><br>
                        </div>
                        <div class="col-33">
                            <label class="gender-label" for="other"><input id="other" name="gender" type="radio">NB</label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-25">
                    <label for="nName">Nickname:</label>
                </div>
                <div class="col-25">
                    <input type="text" id="nName" :value="nickName" :disabled="disabled">
                </div>
                <div class="col-25">
                    <label for="fitnessLevel">Fitness Level:</label>
                </div>
                <div class="col-25">
                    <select id="fitnessLevel" :value="fitness" :disabled="disabled">
                        <option value="1">Couch potato</option>
                        <option value="2">Coming back from injury</option>
                        <option value="3">Go for walks</option>
                        <option value="4">Play sport</option>
                        <option value="5">Marathon runner</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-50">
                    <label for="bio">Bio:</label>
                </div>
                <div class="col-50">
                    <input type="body" id="bio" :value="bio" :disabled="disabled"
                           placeholder="Something interesting about you!">
                </div>
            </div>
            <div id="emailBody">
                <div class="row">
                    <div class="col-25">
                        <label style="font-weight: bold" for="emailInput">Email address:</label>
                    </div>
                    <div class="col-50">
                        <input type="text" id="emailInput" name="email" :value="emails[0]"
                               placeholder="john@example.com" required :disabled="true">
                    </div>
                    <div id="emailAdd" class="col-25">
                        <input class="button" type="button" v-on:click="createEmail" value="Add Email"
                               :hidden="disabled">
                    </div>
                </div>
            </div>
            <div class="row" id="countriesBody"></div>
            <div class="row">
                <div class="col-50">
                    <div id="availCountriesBody"></div>
                      <table>
                        <tr>
                          <th>Available Countries</th>
                        </tr>
                        <div v-for="(country, index) in availCountries" :key="index" class="tr">
                          <button @click="addLine(index,true)">{{country}}</button>
                        </div>
                      </table>
                </div>
                <div class="col-50">
                    <div id="yourCountriesBody"></div>
                        <table>
                          <tr>
                            <th>Your Countries</th>
                          </tr>
                          <div v-for="(country, index) in yourCountries" :key="index" class="tr">
                            <button @click="removeLine(index,false)">{{country}}</button>
                          </div>
                        </table>
                </div>
            </div>
        </div>
        <a class="button" href="index.html">Log out</a>
    </div>


</template>


<script>
    // import api from '../Api';


    const User = {
        name: 'User',

        data: function () {
            return {
                editButtonText: "Edit Profile",
                profile_id: 17,
                disabled: true,
                lastname: "Pocket",
                firstname: "Poly",
                nickName: "",
                emails: ["poly@pocket.com"],
                bio: "Poly Pocket is so tiny.",
                date_of_birth: "2000-11-11",
                gender: "female",
                fitness: "3",
                yourCountries: ['China', 'France', 'Germany'],
                availCountries: ['New Zealand', 'Australia', 'Chile', 'CHINA'],
                passport: [
                    "United States of America",
                    "Thailand"
                ]
            }
        },

        methods: {
          
            createEmail: function () {
                var emailBody = document.getElementById('emailBody');

                var newEmail = document.getElementById("emailInput").value;
                this.emails.push(newEmail);

                var row = document.createElement('row');
                row.setAttribute('id', newEmail);

                var label = document.createElement("label");
                label.innerText = newEmail;
                var delBtn = document.createElement("button");
                delBtn.setAttribute('v-on:click')


                row.appendChild(label);
                row.appendChild(delBtn);
                emailBody.appendChild(row);
            },

            deleteEmail: function (email) {
              var emailBody = document.getElementById('emailBody');

              emailBody.removeChild(document.getElementById(email));
              // need to delete email from this.emails list

            },

            toggleEdits: function () {
                this.disabled = !this.disabled
                if (this.disabled) {
                    //this.updateUser();
                    this.editButtonText = "Edit Profile"
                } else {
                    this.editButtonText = "Save Changes"
                }
            },
        },

    // need to create a API


    };

    export default User
</script>

<style scoped>
    @import 'Profile.css';

    [v-cloak] {
        display: none;
    }
</style>