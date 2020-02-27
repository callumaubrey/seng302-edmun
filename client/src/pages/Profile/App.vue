

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
          <input type="text" id="fName" name="firstName" placeholder="John" required>
        </div>
        <div class="col-25">
          <label for="dob">Date of Birth:</label>
        </div>
        <div class="col-25">
          <input type="date" id="dob" required>
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="lname">Last Name:</label>
        </div>
        <div class="col-25">
          <input type="text" id="lname" name="lname" placeholder="Doe" required>
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
          <input type="text" id="nName" name="lastname" placeholder="Johnny" required>
        </div>
        <div class="col-25">
          <label for="fitnessLevel">Fitness Level:</label>
        </div>
        <div class="col-25">
          <select id="fitnessLevel">
            <option value="level0">Couch potato</option>
            <option value="level1">Coming back from injury</option>
            <option value="level2">Go for walks</option>
            <option value="level3">Play sport</option>
            <option value="level4">Marathon runner</option>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="col-50">
          <label for="bio">Bio:</label>
        </div>
        <div class="col-50">
          <input type="body" id="bio" name="bio" placeholder="Something interesting about you!">
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="emailInput">Email address:</label>
        </div>
        <div class="col-50">
          <input type="text" id="emailInput" name="email" placeholder="john@example.com" required>
        </div>
        <div id="emailAdd" class="col-25">
          <button v-on:click="addEmail" >Add Email</button>
        </div>
      </div>
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
      <a href="index.html">Home</a>
  </div>


</template>

<script>

  // app Vue instance

  const User = {
    el: '#emailAdd',
    data: {
      emails: [],
    },
    methods: {
      // button calls function properly but unsure how to add email input into list of emails
      addEmail: function () {
        var newEmail = document.getElementById("emailInput").value;
        console.log(newEmail);
        // this.emails.push(newEmail);
        // console.log(this.emails);
      }
    }
  };

  export default User

</script>

<style scoped>
  @import 'Profile.css';

  [v-cloak] { display: none; }


</style>