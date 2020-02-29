<template>
  <div id="app">
    <div class="container">
      <h1> Create a new account </h1>
      <hr>
      <form method="post">
        <div class="row">
          <div class="col-33">
            <label class="field" for="fname"> First name</label>
            <input class="name" id="fname" name="firstname" placeholder="John" required type="text" v-model="fname">
          </div>
          <div class="col-33">
            <label class="field" for="mname"> Middle name</label>
            <input id="mname" name="middlename" placeholder="Middle" type="text">
          </div>
          <div class="col-33">
            <label class="field" for="lname"> Last name</label>
            <input class="name" id="lname" name="lastname" placeholder="Doe" required type="text">
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
                    default="01-01-1995"
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
            <label class="gender-label" for="female"><input id="female" name="gender" type="radio">Female</label><br>
          </div>
          <div class="col-33">
            <label class="gender-label" for="male"><input id="male" name="gender" type="radio">Male</label><br>
          </div>
          <div class="col-33">
            <label class="gender-label" for="other"><input id="other" name="gender" type="radio">Non-binary</label>
          </div>
        </div>

        <div class="row">
          <div class="col-100">
            <input class="sign-up-btn" type="submit" v-on:click="validateFields" value="Sign Up">
          </div>
        </div>

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
        fname: null,
      }
    },
    methods: {
      validateFields: function () {
        let elements = document.getElementsByTagName("INPUT");
        for (let i = 0; i < elements.length; i++) {
          let elementType = elements[i].type;
          let msg = " ";
          switch (elementType) {
            case "text":
              if (elements[i].className === "name") {
                elements[i].pattern = "^[A-Za-z]+$";
                msg = "What's your name?"
              }
              break;
            case "email":
              elements[i].pattern = "^($|[a-zA-Z0-9_\\.\\+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-\\.]+)$";
              msg = "This will be your primary email to log in to your account";
              break;
            case "password":
              elements[i].pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$";
              msg = "Password should contain at least 8 characters with at least\t\n" +
                      "- one digit\t\n" +
                      "- one lower case\t\n" +
                      "- one upper case";
              break;
          }
          elements[i].oninvalid = function (e) {
            if (!e.target.validity.valid) {
              e.target.setCustomValidity(msg);
              e.target.style.borderColor = "red";
            } else {
              e.target.setCustomValidity('');
              e.target.style.borderColor = "white";
            }
          };

          elements[i].oninput = function (e) {
            e.target.setCustomValidity(' ');
            e.target.style.borderColor = "white";
          };

          elements[i].onchange = function (e) {
            e.target.setCustomValidity('');
            e.target.style.borderColor = "white";
          };

          elements[i].onclick = function (e) {
            e.target.setCustomValidity('');
            console.log(e.target.validity.valid);
            e.target.reportValidity();
          };

          elements[i].onblur = function (e) {
            console.log("onblur");
            e.target.setCustomValidity('');
            if (!e.target.checkValidity()) {
              e.target.style.borderColor = "red";
            }
          }
        }
        this.validatePassword();
        this.validateGender()
      },
      validatePassword: function () {
        let rpassword = document.getElementById("psw-repeat");
        let password = document.getElementById("psw");
        function check() {
          console.log(password.value);
          console.log(rpassword.value);
          console.log(password.pattern);
          if (password.value == rpassword.value && password.checkValidity()) {
            password.style.borderColor = "white";
            rpassword.style.borderColor = "white";
          } else {
            password.style.borderColor = "red";
            rpassword.style.borderColor = "red";
          }
        }
        rpassword.onkeyup = check;
        password.onkeyup = check;
      },
      validateGender: function () {
        let radios = document.getElementsByName("gender");
        let genderValid = false;

        for (let i=0; i < radios.length; i++) {
          radios[i].onclick = function() {
            let radioLabels = document.getElementsByClassName("gender-label");
            for (let i=0; i < radioLabels.length; i++) {
              radioLabels[i].style.border = "";
            }
          };
          if (radios[i].checked) {
            genderValid= true;
          }
        }
        let radioLabels = document.getElementsByClassName("gender-label");
        if (!genderValid) {
          for (let i=0; i < radioLabels.length; i++) {
            radioLabels[i].style.border = "1px solid red";
          }
        }
        console.log(genderValid);
        return genderValid;
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

  input[type=email] {
    width: 100%;
    padding: 12px 12px 12px 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=radio] {
    padding: 20px;
    margin: 0 10px 0 0;
    border: 100px solid red;
    border-radius: 3px;
    outline: #75d378;
    display: inline-block;

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
