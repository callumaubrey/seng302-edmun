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
      <b-form novalidate @submit.stop.prevent="onSubmit">
      <b-row class="my-1">
        <b-col sm="4">
          <label>First Name</label>
          <b-form-input id="input-default" placeholder="Enter name" :state="validateState('firstname')" v-model ="$v.firstname.$model" required></b-form-input>
          <b-form-invalid-feedback>Invalid first name</b-form-invalid-feedback>
        </b-col>

        <b-col sm="4">
          <label>Middle Name</label>
          <b-form-input id="input-default" placeholder="Enter middle name" :state="validateState('middlename')" v-model ="$v.middlename.$model" required></b-form-input>
          <b-form-invalid-feedback>Invalid middle name</b-form-invalid-feedback>
        </b-col>
        <b-col sm="4">
          <label>Last Name</label>
          <b-form-input id="input-default" placeholder="Enter last name" :state="validateState('lastname')" v-model ="$v.lastname.$model" required></b-form-input>
          <b-form-invalid-feedback>Invalid last name</b-form-invalid-feedback>
        </b-col>

      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Nickname</label>
          <b-form-input id="input-default" placeholder="Enter nickname" :state="validateState('nickname')" v-model ="$v.nickname.$model" required></b-form-input>
        </b-col>
      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Email address</label>
          <b-form-input id="email" placeholder="Enter email address" :state="validateState('primary_email')" v-model ="primary_email" required></b-form-input>
          <b-form-invalid-feedback>Invalid email</b-form-invalid-feedback>
        </b-col>
      </b-row>
      <b-row class="my-1">
        <b-col sm="6">
          <label>Password</label>
          <b-form-input type="password" id="input-default" placeholder="Enter password" :state="validateState('password')" v-model ="$v.password.$model" required></b-form-input>
          <b-form-invalid-feedback> Password should contain at least 8 characters with at least one digit, one lower case, one upper case</b-form-invalid-feedback>


        </b-col>

        <b-col sm="6">
          <label>Repeat Password</label>
          <b-form-input id="input-default" type="password" placeholder="Enter password again" :state="validateState('passwordRepeat')" v-model ="$v.passwordRepeat.$model" required></b-form-input>
          <b-form-invalid-feedback> Passwords should be the same</b-form-invalid-feedback>
        </b-col>

      </b-row>
      <b-row class="my-1">
        <b-col sm="12">
          <label>Date of birth</label>
          <b-form-input id="input-default" type="date_of_birth" placeholder="Enter date of birth" :state="validateState('date_of_birth')" v-model ="$v.date_of_birth.$model" required></b-form-input>
        </b-col>
      </b-row>
      <b-row>
        <b-col sm="12">
        <label>Gender</label>
        </b-col>
      </b-row>
      <b-row>
        <b-col sm="12">
          <b-form-select class="mb-3" required :state="validateState('gender')" v-model="$v.gender.$model">
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

  import { required, alpha, email, helpers, sameAs, alphaNum} from 'vuelidate/lib/validators'
  const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));
  export default {
  components: {
    NavBar
  },
    data() {
      return {
        SERVER: "https://69077def-475c-4406-ba02-dfc190024a7f.mock.pstmn.io",
        date_of_birth: null,
        firstname: null,
        middlename: null,
        lastname: null,
        nickname: null,
        password: null,
        primary_email: null,
        passwordRepeat: null,
        gender: null,
      }
    },
    validations: {
      firstname: {
        required,
        alpha
      },
      middlename: {
        alpha
      },
      lastname: {
        required,
        alpha
      },
      nickname: {
        alphaNum
      },
      primary_email: {
        required,
        email
      },
      password: {
        required,
        passwordValidate
      },
      passwordRepeat: {
        required,
        sameAsPassword: sameAs('password')
      },
      date_of_birth: {
        required
      },
      gender: {
        required
      }
    },
    methods: {
      validateState: function(name) {
        const { $dirty, $error } = this.$v[name];
        return $dirty ? !$error : null;
      },
      onSubmit: function () {
        this.$v.$touch();
        if (this.$v.$anyError) {
          return;
        }

        alert("Form submitted!");
      }
    }
  }

</script>


<style scoped>
  [v-cloak] {
    display: none;
  }

  .container {
    background-color: #f2f2f2;
    padding: 20px 20px 20px 20px;
    border: 1px solid lightgrey;
    border-radius: 3px;

  }
</style>
