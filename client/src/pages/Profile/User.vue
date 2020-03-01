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
                        <input type="text" id="emailInput" name="email"
                               placeholder="john@example.com" required :disabled=false>
                    </div>
                    <div id="emailAdd" class="col-25">
                        <input class="button" type="button" v-on:click="createEmail" value="Add Email"
                               :hidden="disabled">
                    </div>
                </div>
                <div v-for="(email, index) in primaryEmail" :key="index" class="row">
                  <label class="col-50">Your Primary Email: {{email}}</label>
                </div>
                <div v-for="(email, index) in emails" :key="index" class="row">
                  <label class="col-50">{{email}}</label>
                  <button @click="makePrimary(index)" class="col-25">Make Primary Email</button>
                  <button @click="deleteEmail(index)" class="col-25">Delete Email</button>
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
                            <button @click="addLine(index,false)">{{country}}</button>
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
                primaryEmail: ["martin@email.com"],
                emails: ["poly@pocket.com", "yeet@email.com", "awesome@nice.com"],
                bio: "Poly Pocket is so tiny.",
                date_of_birth: "2000-11-11",
                gender: "female",
                fitness: "3",
                yourCountries: ['China', 'France', 'Germany'],
                availCountries: ['New Zealand', 'Australia', 'Chile', 'CHINA'],
                passport: [
                    "United States of America",
                    "Thailand"
                ],
                blockRemoval: true,
            }
        },

        watch: {
            lines () {
                this.blockRemoval = this.lines.length <= 1
            }
        },

        methods: {


            getCountryData: function() {
                "GET https://restcountries.eu/rest/v2/name/all"
            },
            //isAvailable adding country from available to your countries
            addLine (index, isAvailable) {
                if (isAvailable) {
                    this.yourCountries.push(this.availCountries[index]);
                    this.availCountries.splice(index, 1);
                } else {
                    this.availCountries.push(this.yourCountries[index]);
                    this.yourCountries.splice(index, 1);
                }
            },

            makePrimary(index) {
              var oldPrimary = this.primaryEmail[0];
              this.primaryEmail.splice(0,1);
              this.primaryEmail.push(this.emails[index])
              this.emails.splice(index,1);
              this.emails.push(oldPrimary);
            },

            deleteEmail(index) {
              this.emails.splice(index, 1);
            },

            createAvailTable: function () {
                var table = document.createElement('table');
                table.setAttribute('id', 'availCountries');

                var tr = table.insertRow(-1);

                var th = document.createElement('th');
                th.innerHTML = 'Available Countries';
                tr.appendChild(th);

                for (var c = 0; c < this.availCountries.length; c++) {
                    tr = table.insertRow(-1);
                    var td = document.createElement('td');
                    var btn = document.createElement('button')
                    btn.innerText = this.availCountries[c];
                    btn.setAttribute('name', this.availCountries[c]);
                    btn.setAttribute('v-on:click', function () {
                        this.yourCountries.push(btn.innerText);
                        console.log(this.yourCountries);
                        this.createYourTable();
                    })
                    // btn function is not getting called properly  ^^
                    // need to make each of these cells selectable and when selected add the country to your countries
                    // and then maybe call the createYourTable function to re create the your countries table, might not be optimal but only way i can think of at the moment
                    td = tr.insertCell(-1);
                    td.appendChild(btn);
                }

                var element = document.getElementById('availCountriesBody');

                element.appendChild(table);
            },

            // might be called every time a user adds a country from available countries.
            createYourTable: function () {
                console.log("Create Table")
                var table = document.createElement('table');
                table.setAttribute('id', 'yourCountries');

                var tr = table.insertRow(-1);

                var th = document.createElement('th');
                th.innerHTML = 'Your Countries';
                tr.appendChild(th);

                for (var c = 0; c < this.yourCountries.length; c++) {
                    tr = table.insertRow(-1);
                    var td = document.createElement('td');
                    // need to make these cells selectable and when selected remove it from your countries
                    td = tr.insertCell(-1);
                    td.innerHTML = this.yourCountries[c];
                }

                var element = document.getElementById('yourCountriesBody');
                element.appendChild(table);
            },

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

            toggleEdits: function () {
                this.disabled = !this.disabled
                if (this.disabled) {
                    //this.updateUser();
                    this.editButtonText = "Edit Profile"
                } else {
                    this.editButtonText = "Save Changes"
                }
            },
        }

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