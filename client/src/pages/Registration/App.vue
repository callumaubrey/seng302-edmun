<template>
  <div id="app">
    <div class="container">
      <h1> Create a new account </h1>
      <hr>
      <form method="post">
        <div class="row">
          <div class="col-33">
            <label class="field" for="fname"> First name</label>
            <input id="fname" name="firstname" placeholder="John" required type="text">
          </div>
          <div class="col-33">
            <label class="field" for="mname"> Middle name</label>
            <input id="mname" name="middlename" placeholder="Middle" type="text">
          </div>
          <div class="col-33">
            <label class="field" for="lname"> Last name</label>
            <input id="lname" name="lastname" placeholder="Doe" required type="text">
          </div>
        </div>

        <div class="row">
          <div class="col-100">
            <label class="field" for="nname"> Nickname</label>
          </div>
        </div>
        <div class="row">
          <div class="col-100">
            <input id="nname" name="nickname" placeholder="Johnny" type="text">
          </div>
        </div>


        <div class="row">
          <div class="col-100">
            <label class="field" for="email">Email address</label>
          </div>
        </div>
        <div class="row">
          <div class="col-100">
            <input id="email" name="email" placeholder="john@example.com" required type="email">
          </div>
        </div>

        <div class="row">
          <div class="col-50">
            <label class="field" for="psw">Password</label>
            <input id="psw" name="password" placeholder="Password" required type="password">
          </div>
          <div class="col-50">
            <label class="field" for="psw-repeat">Repeat Password</label>
            <input id="psw-repeat" name="psw-repeat" placeholder="Repeat Password" required type="password">
          </div>
        </div>
        <div class="col-100">
          <label class="field" for="dob">Date of birth</label>
        </div>
        <div class="row">
          <div class="col-100">
            <date-dropdown
                    :max="currentYear"
                    id="dob"
                    min="1900"
                    months-names="Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec"
                    v-model="selectedDate">
            </date-dropdown>
          </div>
        </div>

        <div class="row">
          <div class="col-100">
            <label class="field">Gender</label>
          </div>
        </div>

        <div class="row">
          <div class="col-33">
            <label for="male"><input id="male" name="gender" type="radio" value="male">Female</label><br>
          </div>
          <div class="col-33">
            <label for="female"><input id="female" name="gender" type="radio" value="female">Male</label><br>
          </div>
          <div class="col-33">
            <label for="other"><input id="other" name="gender" type="radio" value="other">Non-binary</label>
          </div>
        </div>

        <div class="row">
          <div class="col-100">
            <button class="sign-up-btn" type="button">Sign Up</button>
          </div>
        </div>
        <input :onClick="this.validateFields()" type="submit" value="Submit">
      </form>

    </div>
  </div>
</template>

<script>
  import DateDropdown from 'vue-date-dropdown'

  export default {
    components: {
      DateDropdown
    },
    data() {
      return {
        selectedDate: '',
        currentYear: (new Date).getFullYear().toString(),
      }
    },
    methods: {
      validateFields() {
        var elements = document.getElementsByTagName("INPUT");
        for (var i = 0; i < elements.length; i++) {
          elements[i].onclick = function (e) {
            e.target.style.borderColor = "white";
          };
          elements[i].oninvalid = function (e) {
            e.target.setCustomValidity("");
            if (!e.target.validity.valid) {
              e.target.setCustomValidity("This field cannot be left blank");
              e.target.style.borderColor = "red";
            }
          };
          elements[i].oninput = function (e) {
            e.target.setCustomValidity("");

          };
        }

      }
    }
  }



</script>

<style scoped>
  [v-cloak] {
    display: none;
  }

  .row {
    display: -ms-flexbox; /* IE10 */
    display: flex;
  }

  .col-50 {
    -ms-flex: 50%; /* IE10 */
    flex: 50%;
  }

  .col-33 {
    -ms-flex: 33%; /* IE10 */
    flex: 33%;
  }

  .col-100 {
    -ms-flex: 100%;
    flex: 100%;
  }

  .col-33,
  .col-50,
  .col-100 {
    padding: 0 16px;
  }

  input[type=text] {
    width: 100%;
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=password] {
    width: 100%;
    padding: 12px 12px 12px 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=radio] {
    padding: 0;
    margin: 0 10px 0 0;
    border: 0;
  }

  label {
    display: block;
  }

  .field {
    font-weight: bold;
    margin-top: 10px;
    margin-bottom: 10px;
  }

  .container {
    background-color: #f2f2f2;
    padding: 5px 40px 15px 20px;
    border: 1px solid lightgrey;
    border-radius: 3px;
  }

  .sign-up-btn {
    background-color: #000000; /* Green */
    border: none;
    color: white;
    padding: 15px 32px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
  }
</style>
