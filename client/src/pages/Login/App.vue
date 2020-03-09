<template>
  <div id="app">
    <div>
      <NavBar isLoggedIn=false></NavBar>
    </div>
    <div class="container">
      <h1> Login </h1>
      <hr>
      <b-form @submit="onSubmit" novalidate class="needs-validation">
        <b-form-group id="input-group-1" label="Email address" label-for="input-1">
          <b-form-input
            id="input-1"
            type="email"
            v-model="email"
            required
            placeholder="Enter email"
            class="input-s"
            :state="emailState"
            aria-describedby="input-live-feedback"
            trim
            ></b-form-input>

          <!-- This will only be shown if the preceding input has an invalid state -->
          <b-form-invalid-feedback id="input-live-feedback">
            Enter a valid email
          </b-form-invalid-feedback>

        </b-form-group>
        <b-form-group id="input-group-2" label="Password" label-for="input-2">
          <b-form-input
            id="input-2"
            v-model="password"
            type="password"
            required
            placeholder="Enter password"
            :state="passwordState"
            aria-describedby="input-live-feedback"
          ></b-form-input>

          <b-form-invalid-feedback id="input-live-feedback">
            Enter your password
          </b-form-invalid-feedback>
        </b-form-group>
        <b-button type="submit" variant="primary">Submit</b-button>
      </b-form>
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
        let state = null;
        if (this.submitted) {
          if (this.email.length > 0) {
            let pattern = new RegExp("^($|[a-zA-Z0-9_\\.\\+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-\\.]+)$");
            state = pattern.test(this.email);
          } else {
            state = false;
          }
        }
        if (!this.submitted) {
          state = null;
        }
        return state;
      },
      passwordState() {
        let state = null;
        if (this.submitted) {
          if (this.password.length == 0) {
            state = false;
          }
        }
        if (!this.submitted) {
          state = null;
        }
        return state;
      }
    },
    data() {
      return {
          email: "",
          password: "",
        submitted: false
      }
    },
    methods: {
      onSubmit(e) {
        this.submitted = true;
        if (this.emailState && this.passwordState) {
          let currentObj = this;
          this.axios.post('http://localhost:9499/account/login', {
            email: this.email,
            password: this.password
          })
                  .then(function (response) {
                    currentObj.output = response.data;
                    console.log(response.data);
                    this.getElementsByTagName('NavBar').setAttribute('isLoggedIn', true);
                  })
                  .catch(function (error) {
                    currentObj.output = error;
                    console.log(error);
                    this.getElementsByTagName('NavBar').setAttribute('isLoggedIn', false);
                  });
          return true;
        }
        e.preventDefault();
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
    padding: 5px 40px 15px 20px;
    border: 1px solid lightgrey;
    border-radius: 3px;
  }

</style>
