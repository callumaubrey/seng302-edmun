<template>
  <div id="app">
    <NavBar></NavBar>

    <b-container>
      <b-row align-h="center">
        <h3>Forgot Password?</h3>
      </b-row>

      <b-row v-if="emailSent" align-h="center" style="margin-top:20px;">
        <b-alert variant="success" show dismissible>Please check your email and follow the instructions.</b-alert>
      </b-row>

      <b-row v-if="showErrorMessage" align-h="center" style="margin-top:20px;">
        <b-alert variant="danger" show dismissible>Failed to send email.</b-alert>
      </b-row>

      <b-row align-h="center" style="margin-top:10px;">
        Enter your email address below and we'll send you a link so you can reset it.
      </b-row>

      <b-form @submit.stop.prevent="onSubmit">
        <b-row align-h="center" style="margin-top:20px;">
          <b-input
              :state="validateState('email')"
               v-model="$v.email.$model"
               placeholder="Email"
               style="width:300px;"></b-input>
          <b-form-invalid-feedback style="text-align:center;">Please enter a valid email</b-form-invalid-feedback>
        </b-row>

        <b-row align-h="center" style="margin-top:10px;">
          <b-button type="submit" style="width:300px;">Send</b-button>
        </b-row>
      </b-form>
    </b-container>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar';
import {email,required} from 'vuelidate/lib/validators'
import Api from '@/Api';

export default {
  components: {
    NavBar
  },
  data() {
    return {
      email: null,
      emailSent: false,
      showErrorMessage: false
    }
  },
  validations: {
    email: {
      required,
      email
    }
  },
  methods: {
    validateState: function (name) {
      const {$dirty, $error} = this.$v[name];
      return $dirty ? !$error : null;
    },
    onSubmit: function() {
      this.$v.$touch();
      if (this.$v.$anyError) {
        return;
      }

      let data = {
        email: this.email
      };

      Api.sendForgotPasswordEmail(data)
        .then(() => {
          this.emailSent = true;
          this.email = null;
          this.$v.$reset();
        })
        .catch((err) => {
          this.showErrorMessage = true;
          console.log(err);
        });
    }
  }
}
</script>