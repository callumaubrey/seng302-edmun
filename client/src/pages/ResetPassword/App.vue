<template>
  <div id="app">
    <NavBar></NavBar>

    <b-container>
      <b-row align-h="center">
        <h3>Reset Password</h3>
      </b-row>

      <b-row align-h="center"></b-row>
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
      repeatedPassword: null
    }
  },
  validations: {
    newPassword: {
      required,
      passwordValidate
    },
    repeatedPassword: {
      required,
      sameAsPassword: sameAs('password')
    },
  },
  methods: {
    onSubmit() {
      let token = this.$route.params.token;
      let data = {
        "new_password": this.newPassword,
        "repeat_password": this.repeatedPassword
      };

      Api.resetPassword(token, data)
        .then(() => {

        })
        .catch((err) => {
          console.log(err);
        });
    }
  }
}
</script>