<template>
  <div id="app">
    <NavBar></NavBar>
    <div class="container">

    <b-container fluid>
      <b-row>
        <b-col>
          <h1>Create a new account</h1>
          <hr>
        </b-col>
      </b-row>
      <b-form novalidate @submit="onSubmit">
      <b-row class="my-1">
        <b-col sm="4">
          <label>First Name</label>
          <b-form-input id="input-default" placeholder="Enter name" :state="fNameState" v-model ="firstName" required></b-form-input>
          <b-form-invalid-feedback> Invalid first name</b-form-invalid-feedback>
        </b-col>

        <b-col sm="4">
          <label>Middle Name</label>
          <b-form-input id="input-default" placeholder="Enter middle name" :state="mNameState" v-model ="middleName" required></b-form-input>
        </b-col>
        <b-col sm="4">
          <label>Last Name</label>
          <b-form-input id="input-default" placeholder="Enter last name" :state="lNameState" v-model ="lastName" required></b-form-input>
        </b-col>

      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Nickname</label>
          <b-form-input id="input-default" placeholder="Enter nickname" v-model ="nickName" required></b-form-input>
        </b-col>
      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Email address</label>
          <b-form-input id="email" placeholder="Enter email address" :state="emailState" v-model ="email" required></b-form-input>
          <b-form-invalid-feedback> Invalid email</b-form-invalid-feedback>
        </b-col>
      </b-row>
      <b-row class="my-1">
        <b-col sm="6">
          <label>Password</label>
          <b-form-input type="password" id="input-default" placeholder="Enter password" :state="passwordState" v-model ="password" required></b-form-input>
          <b-form-invalid-feedback> Password should contain at least 8 characters with at least one digit, one lower case, one upper case</b-form-invalid-feedback>


        </b-col>

        <b-col sm="6">
          <label>Repeat Password</label>
          <b-form-input id="input-default" type="password" placeholder="Enter password again" :state="rPasswordState" v-model ="passwordRepeat" required></b-form-input>
          <b-form-invalid-feedback> Passwords should be the same</b-form-invalid-feedback>
        </b-col>

      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Date of birth</label>
          <b-form-input id="input-default" type="date" placeholder="Enter date of birth" :state="dateOfBirthState" v-model ="date" required></b-form-input>
        </b-col>
      </b-row>
      <b-row>
        <b-col sm="12">
        <label>Gender</label>
        </b-col>
      </b-row>
      <b-row>
        <b-col sm="12">
          <b-form-select class="mb-3" required >

            <b-form-select-option value="C">Male</b-form-select-option>
            <b-form-select-option value="D">Female</b-form-select-option>
            <b-form-select-option value="D">Non-Binary</b-form-select-option>
          </b-form-select>



        </b-col>


      </b-row>
      <b-button type="submit" variant="primary" >Submit</b-button>



</b-form>
    </b-container>
  </div>
  </div>

</template>

<script>
  import NavBar from '@/components/NavBar.vue';

  export default {
  components: {
    NavBar
  },
    computed: {
      emailState() {
        let state = false;
        if (this.email) {
          let pattern = new RegExp("^($|[a-zA-Z0-9_\\.\\+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-\\.]+)$");
          state = pattern.test(this.email);
        } else {
          state = null;
        }
        return state;
      },
      fNameState() {
        let state = false;
        if (this.firstName) {
          let pattern = new RegExp("^[A-Za-z]+$");
          state = pattern.test(this.firstName);
        } else {
          state = null;
        }
        return state;
      },
      mNameState() {
        let state = false;
        if (this.middleName) {
          let pattern = new RegExp("^[A-Za-z]+$");
          state = pattern.test(this.middleName);
        } else {
          state = null;
        }
        return state;
      },
      lNameState() {
        let state = false;
        if (this.lastName) {
          let pattern = new RegExp("^[A-Za-z]+$");
          state = pattern.test(this.lastName);
        } else {
          state = null;
        }
        return state;
      },
      passwordState() {
        let state = false;
        if (this.password) {
          let pattern = new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$");
          state = pattern.test(this.password);
        } else {
          state = null;
        }
        return state;
      },
      rPasswordState() {
        let state = false;
        if (this.passwordRepeat) {
          if (this.password !== this.passwordRepeat) {
            return state;
          } else {
            state = true;
          }
        } else {
          state = null;
        }
        return state;
      }
    },

    data() {
      return {
        SERVER: "https://69077def-475c-4406-ba02-dfc190024a7f.mock.pstmn.io",
        selectedDay: "1",
        selectedMonth: "1",
        selectedYear: "2020",
        date:"",
        firstName: "",
        middleName: "",
        lastName: "",
        nickName: "",
        password: "",
        email: "",
        passwordRepeat: "",
        selectedGender: "",
      }
    },
    methods: {
      onSubmit: function (event) {
        if (this.emailState && this.fNameState) {
          return true;
        }
        event.preventDefault();

      },
      validateFields: function (event) {
        let elements = document.getElementsByTagName("INPUT");
        let isValid = true;
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
          if (!elements[i].validity.valid) {
            elements[i].style.borderColor = "red";
          }
          elements[i].oninvalid = function (e) {
            if (!e.target.validity.valid) {
              e.target.setCustomValidity(msg);
              e.target.style.borderColor = "red";
              isValid = false
            } else {
              e.target.setCustomValidity('');
              e.target.style.borderColor = "#ccc";
            }
          };

          elements[i].oninput = function (e) {
            e.target.setCustomValidity(' ');
            e.target.style.borderColor = "#ccc";
          };

          elements[i].onchange = function (e) {
            e.target.setCustomValidity('');
            e.target.style.borderColor = "#ccc";
          };

          elements[i].onclick = function (e) {
            e.target.setCustomValidity('');
            e.target.reportValidity();
          };

          elements[i].onblur = function (e) {
            e.target.setCustomValidity('');
            if (!e.target.checkValidity()) {
              isValid = false;
              e.target.style.borderColor = "red";
            }
          }
        }

        let passwordValid = this.validatePassword();
        let genderValid = this.validateGender();
        let DOBValid = this.validateDOB();

        if (!isValid || !genderValid || !passwordValid || !DOBValid) {
          console.log("incorrect");
          event.preventDefault();
        } else {
          console.log("correct");
          event.preventDefault();
          let currentObj = this;
          this.axios.post(this.SERVER + '/registration', {
            firstname: this.firstName,
            middlename: this.middleName,
            lastname: this.lastName,
            nickname: this.nickName,
            email: this.email,
            password: this.password,
            dayOfBirth: this.selectedDay,
            monthOfBirth: this.selectedMonth,
            yearOfBirth: this.selectedYear,
            gender: this.selectedGender
          })
                  .then(function (response) {
                    currentObj.output = response.data;
                    location.href = "/profile";
                  })
                  .catch(function (error) {
                    currentObj.output = error;
                  });
        }
      },
      validatePassword: function () {
        let rpassword = document.getElementById("psw-repeat");
        let password = document.getElementById("psw");
        let passwordValid = true;

        function check() {
          if (password.value == rpassword.value && password.checkValidity()) {
            password.style.borderColor = "#ccc";
            rpassword.style.borderColor = "#ccc"
          } else {
            password.style.borderColor = "red";
            rpassword.style.borderColor = "red";
            passwordValid = false;
          }
        }

        check();
        rpassword.onkeyup = check;
        password.onkeyup = check;
        return passwordValid;
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
        return genderValid;
      },
      validateDOB() {

        let day = document.getElementById("day");
        let month = document.getElementById("month");
        let year = document.getElementById("year");

        let validDate;
        let self = this;
        function testDate() {
          let now = new Date();
          let selected = new Date(parseInt(self.selectedYear), parseInt(self.selectedMonth)-1, parseInt(self.selectedDay));
          if (selected.getMonth() == parseInt(self.selectedMonth)-1 && selected < now) {
            validDate = true;
            day.style.borderColor = "#ccc";
            month.style.borderColor = "#ccc";
            year.style.borderColor = "#ccc";
          } else {
            validDate = false;
            day.style.borderColor = "red";
            month.style.borderColor = "red";
            year.style.borderColor = "red";
          }
        }
        testDate();
        day.onchange = testDate;
        month.onchange = testDate;
        year.onchange = testDate;
        return validDate;
      },
      setUpDOB() {
        let today = new Date();
        let currentYear = today.getFullYear();

        let day = document.getElementById("day");
        for (let i = 1; i <= 31; i++) {
          let element = document.createElement("option");
          element.textContent = i.toString();
          element.value = i.toString();
          day.appendChild(element);
        }

        let month = document.getElementById("month");
        let months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        for(let i = 0; i < months.length; i++) {
          let option = months[i];
          let element = document.createElement("option");
          element.textContent = option;
          let number = (i+1);
          element.value = number.toString();
          month.appendChild(element);
        }

        let year = document.getElementById("year");
        let startYear = 1900;
        for (let i = startYear; i <= currentYear; i++) {
          let element = document.createElement("option");
          element.textContent = i.toString();
          element.value = i.toString();
          year.appendChild(element);
        }
        year.selectedIndex = currentYear - startYear;
      },
      captureGenderSelection() {

      }
    },
    mounted() {
      this.setUpDOB();
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
    outline-width: 0;
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
  }

  input:focus {
    box-shadow: 0 0 5px rgba(81, 203, 238, 1);
    border: 1px solid rgba(81, 203, 238, 1);
  }

  select[class="dob"] {
    padding: 10px;
    border: 1px solid #ccc;
    margin-right: 6px;
  }

  select[class="dob"]:focus {
    box-shadow: 0 0 5px rgba(81, 203, 238, 1);
    border: 1px solid rgba(81, 203, 238, 1);
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
    padding: 20px 20px 20px 20px;
    border: 1px solid lightgrey;
    border-radius: 3px;
    width:65%;
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
