<template>
    <div id="app">
        <div>
            <NavBar></NavBar>
        </div>
        <div class="container">
            <b-row align-h="center">
              <b-img :src="require('@/assets/goatNOLOGO.jpg')" height="150px"></b-img>
            </b-row>
            <h2 style="text-align:center;">Log in to Edmun</h2>
            <hr>
            <b-form @submit="onSubmit" novalidate class="needs-validation">
                <b-form-group id="input-group-1" label="Email address" label-for="input-1">
                    <b-form-input
                            :state="emailState"
                            aria-describedby="input-live-feedback"
                            class="input-s"
                            id="input-1"
                            placeholder="Enter email"
                            required
                            trim
                            type="email"
                            v-model="email"
                            value="jacky@google.com"
                    ></b-form-input>

                    <!-- This will only be shown if the preceding input has an invalid state -->
                    <b-form-invalid-feedback id="input-live-feedback">
                        Enter a valid email
                    </b-form-invalid-feedback>

                </b-form-group>
                <b-form-group id="input-group-2" label="Password" label-for="input-2">
                    <b-form-input
                            :state="passwordState"
                            aria-describedby="input-live-feedback"
                            id="input-2"
                            placeholder="Enter password"
                            required
                            type="password"
                            v-model="password"
                            value="JackysSecuredPwd1"
                    ></b-form-input>

                    <b-form-invalid-feedback id="input-live-feedback">
                        Enter your password
                    </b-form-invalid-feedback>

                    <p id="user-error-feedback"></p>

                </b-form-group>
                <b-button type="submit" variant="primary" style="width:100%;">Submit</b-button>
            </b-form>
        </div>
        <b-container class="b-container">
          <b-row align-h="center">
            <b-link to="/forgot-password" id="forgot-password-link">Forgot password?</b-link>
            <span class="dot"></span>
            <b-link to="/register" id="signup-link">Sign up for Edmun</b-link>
          </b-row>
        </b-container>

    </div>

</template>

<script>
import NavBar from '@/components/NavBar.vue';
import api from '@/Api';
import {mutations} from "../../store";

export default {
  components: {
    NavBar
  },
  computed: {
    emailState() {
      let state = null;
      if (this.submitted) {
        if (this.email.length > 0) {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
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
                submitted: false,
            }
        },
        methods: {
            onSubmit(e) {
                this.submitted = true;
                if (this.emailState != false && this.passwordState != false) {
                    let currentObj = this;
                    api.login(this.email, this.password)
                        .then(function (response) {
                            let adminPattern = new RegExp("Admin");
                            if (adminPattern.test(currentObj.output)) {
                                currentObj.$router.push('/admin');
                            } else {
                                currentObj.$router.push('/home')
                            }

                            mutations.setLoggedInUser(response.data);
                        })
                        .catch(function (error) {
                            console.log(error)
                            currentObj.output = error;
                            document.getElementById("user-error-feedback").textContent = error.response.data + '\n';
                        });
                }
                e.preventDefault();
            },
        }
    }

</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .container {
        width:600px;
        padding: 5px 40px 15px 20px;
        border-radius: 3px;
    }

    #user-error-feedback {
        color: red;
        padding-top: 10px;
    }

    .b-container {
        background-color: white;
        border: 0 white;
    }

    .dot {
      height: 5px;
      width: 5px;
      background-color: #000;
      border-radius: 50%;
      display: inline-block;
      margin-top:10px;
      margin-left:7px;
      margin-right:7px;
    }

</style>
