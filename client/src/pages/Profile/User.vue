<template>
  <div id="app">
    <div class="container">
      <div class="row" style="padding: 1em">
        <h1 class="col-50"> Here is your account!!!! </h1>
        <custom-button class="button">Edit Profile</custom-button>
      </div>
    <hr>
      <div class ="row">
        <div class="col-25">
          <label for="fName"> First name:</label>
        </div>
        <div class="col-25">
          <input type="text" :value="firstname" :disabled="disabled">
        </div>
        <div class="col-25">
          <label for="dob">Date of Birth:</label>
        </div>
        <div class="col-25">
          <input type="date" :value="date_of_birth" :disabled="disabled">
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="lname">Last Name:</label>
        </div>
        <div class="col-25">
          <input type="text" :value="lastname" :disabled="disabled">
        </div>
        <div class="col-25">
          <label>Gender:</label>
        </div>
        <div class="col-25">
            <div class="row">
            <div class="col-25">
              <label for="male"><input type="radio" id="male" name="gender" value="male">F</label><br>
            </div>
            <div class="col-25">
              <label for="female"><input type="radio" id="female" name="gender" value="female">M</label><br>
            </div>
            <div class="col-25">
              <label for="other"><input type="radio" id="other" name="gender" value="other">N/A</label>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="nName"> Nickname:</label>
        </div>
        <div class="col-25">
          <input type="text" :value="nickName" :disabled="disabled">
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
          <input type="body" id="bio" :value="bio" :disabled="disabled" placeholder="Something interesting about you!">
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="emailInput">Email address:</label>
        </div>
        <div class="col-50">
          <input type="text" id="emailInput" name="email" :value="emails[0]" :disabled="disabled" placeholder="john@example.com" required>
        </div>
        <div id="emailAdd" class="col-25">
          <custom-button class="button">Add Email</custom-button>
        </div>
      </div>
        <div class="row" id = "emailBody"></div>
      <div class="row">
        <div class="col-50">
          <table>
            <th>
              Available Passport countries
            </th>
            <th>
              Your Passport countries
            </th>
            <tr>
              <td>New Zealand</td>
              <td>Australia</td>
            </tr>
            <tr>
              <td>Chile</td>
              <td>China</td>
            </tr>
          </table>
        </div>
      </div>
    </div>
      <a class="button" href="index.html">Log out</a>
  </div>


</template>

<<<<<<< Updated upstream:client/src/pages/Profile/App.vue

=======
<script>

  // import api from '../Api';
  // app Vue instance
>>>>>>> Stashed changes:client/src/pages/Profile/User.vue

<script>
  var User = {
    name: 'User',

    data: function () {
      return {
        profile_id: 17,
        disabled: true,
        lastname: "Pocket",
        firstname: "Poly",
        nickName:"",
        emails: ["poly@pocket.com"],
        bio: "Poly Pocket is so tiny.",
        date_of_birth: "2000-11-11",
        gender: "female",
        fitness: "3",
        passport: [
          "United States of America",
          "Thailand"
        ]
      }
    }
  }


  import Vue from "vue";
  //Custom button
  Vue.component('custom-button', {
    template: '\
    <button class="normal"\
      :class="type"\
      :disabled="disabled"\
      @click="callback($event)"\
      >\
        <slot></slot>\
    </button>\
  ',
    props: {
      type: String,
      disabled: Boolean,
    },

    methods: {
<<<<<<< Updated upstream:client/src/pages/Profile/App.vue
      callback: function(e) {
        this.$emit('click', e);
        User.disabled = !User.disabled
        console.log("Disable Inputs:",User.disabled)
      }
    }
  });
  // app Vue instance

=======

      // functions adds email to email list, need to start on creating email input fields on screen for each email
      addEmail: function () {
        var newEmail = document.getElementById("emailInput").value;
        console.log(newEmail);
        this.emails.push(newEmail);
        console.log(this.emails);
      },

      // should only be called when page loads up
      createAvailTable: function () {
        console.log('create AvailTable is called')
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
          btn.setAttribute('onclick', function () {
            this.yourCountries.push(this.availCountries[c]);
            this.createYourTable();
          });

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

      addCountry: function (newCountry) {
        this.yourCountries.push(newCountry);
        this.createYourTable();
      }
    },

    // need to create a API

    mounted() {
      this.createAvailTable();
      this.createYourTable();
    }
  };
>>>>>>> Stashed changes:client/src/pages/Profile/User.vue

  // const User = {
  //   el: '#emailAdd',
  //   data: {
  //     emails: [],
  //   },
  //   methods: {
  //     // button calls function properly but unsure how to add email input into list of emails
  //     addEmail: function () {
  //       var newEmail = document.getElementById("emailInput").value;
  //       console.log(newEmail);
  //       // this.emails.push(newEmail);
  //       // console.log(this.emails);
  //     }
  //   }
  // };

  export default User
</script>

<style scoped>
  @import 'Profile.css';
  [v-cloak] { display: none; }
</style>