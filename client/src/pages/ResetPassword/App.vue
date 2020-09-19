<template>
  <div id="app">
    <NavBar></NavBar>

    <b-container>
      <b-row align-h="center">
        <h3>Reset Password</h3>
      </b-row>

      <b-row v-if="showErrorMessage" align-h="center" style="margin-top:20px;">
        <b-alert variant="danger" show dismissible>Invalid token.</b-alert>
      </b-row>


      <b-row align-h="center">
        <b-col>In order to protect your account, make sure your password:</b-col>
      </b-row>
      <b-row align-h="center">
        <b-col>
          <ul>
            <li>Is longer than 7 characters</li>
            <li>Has at least 1 digit</li>
            <li>Has at least 1 lowercase</li>
            <li>Has at least 1 uppercase</li>
          </ul>
        </b-col>
      </b-row>

      <b-form @submit.stop.prevent="onSubmit">
        <b-row align-h="center" style="margin-top:20px;">
          <b-input
              type="password"
              :state="validateState('newPassword')"
              v-model="$v.newPassword.$model"
              placeholder="New password"
              style="width:300px;"></b-input>
          <b-form-invalid-feedback style="text-align:center;">Please enter a valid password</b-form-invalid-feedback>
        </b-row>

        <b-row align-h="center" style="margin-top:20px;">
          <b-input
              type="password"
              :state="validateState('repeatedPassword')"
              v-model="$v.repeatedPassword.$model"
              placeholder="Repeat new password"
              style="width:300px;"></b-input>
          <b-form-invalid-feedback style="text-align:center;">Please enter a valid password</b-form-invalid-feedback>
        </b-row>

        <b-row align-h="center" style="margin-top:10px;">
          <b-button type="submit" style="width:300px;">Change Password</b-button>
        </b-row>
      </b-form>
    </b-container>
  </div>
</template>

<script>
import Api from "@/Api";
import NavBar from "@/components/NavBar";
import {helpers, required, sameAs} from "vuelidate/lib/validators";
const passwordValidate = helpers.regex('passwordValidate', new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$"));

export default {
  components: {
    NavBar
  },
  data() {
    return {
      newPassword: null,
      repeatedPassword: null,
      showErrorMessage: false
    }
  },
  validations: {
    newPassword: {
      required,
      passwordValidate
    },
    repeatedPassword: {
      required,
      sameAsPassword: sameAs('newPassword')
    },
  },
  methods: {
    validateState: function (name) {
      const {$dirty, $error} = this.$v[name];
      return $dirty ? !$error : null;
    },
    onSubmit() {
      this.$v.$touch();
      if (this.$v.$anyError) {
        return;
      }

      let token = this.$route.params.token;
      let data = {
        "new_password": this.newPassword,
        "repeat_password": this.repeatedPassword
      };

      Api.resetPassword(token, data)
        .then(() => {
          alert('Success');
        })
        .catch((err) => {
          this.showErrorMessage = true;
          console.log(err);
        });
    }
  }
}
</script>