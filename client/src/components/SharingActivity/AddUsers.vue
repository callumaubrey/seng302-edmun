<template>
  <b-form-group>
    <label>Add users</label>
    <b-input-group>
      <b-input
          name="email"
          id="emails-input"
          placeholder="john@example.com mary@example.com"
          v-model="emailInput"
          :state="validInput"
          v-on:input="resetValidity"
          autocomplete="off"
      ></b-input>

      <b-input-group-append>
        <b-button id="add-users-button" @click="processInput" variant="primary"
                  :disabled="!hasInput">Add
        </b-button>
      </b-input-group-append>
    </b-input-group>
    <b-form-invalid-feedback :state="validInput">
      {{invalidMessage}}
    </b-form-invalid-feedback>

    <p style="color: floralwhite; font-size: 12px">Add multiple users by separating emails by space
      or semi-colon</p>
  </b-form-group>
</template>

<script>
  import api from '@/Api.js'

  export default {
    name: "AddEmails",
    props: ["allUsersIds", "activityId"],
    data() {
      return {
        emailInput: '',
        emailsArray: [],
        validInput: null,
        invalidEmail: '',
        notRegistered: [],
        added: [],
        creatorId: null,
        creatorEmails: [],
        invalidMessage: 'An error has occurred',
        alreadyAdded: []
      }
    },
    methods: {
      /**
       * Parses the string of emails into a array of unique emails all in lower case
       */
      parseEmailInput() {
        let data = this.emailInput;
        data = data.split(/[\s;]+/);
        data = data.map(email => email.toLowerCase());
        data = [...new Set(data)];
        this.emailsArray = data;
      },
      /**
       * Validates the email and see if the email follows the pattern of a email
       * @param email The email to validate
       * @returns {boolean} true if valid, false if invalid
       */
      validateEmail(email) {
        const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
      },
      creatorEmail(email) {
        return this.creatorEmails.includes(email);
      },
      /**
       * Validates all the emails in the emailsArray and set validInput to false if any if invalid.
       * A error message will display with the first email that is invalid which is why invalidEmail is set as email.
       */
      validateEmails() {
        for (let email of this.emailsArray) {
          if (!this.validateEmail(email)) {
            this.validInput = false;
            this.invalidEmail = email;
            this.invalidMessage = email + " is not a valid email"
          }
          if (this.creatorEmail(email)) {
            this.validInput = false;
            this.invalidEmail = email;
            this.invalidMessage = "You cannot add the creators email: " + email;
          }
        }
      },
      /**
       * The entire process from start to finish on adding users to the activity
       */
      async processInput() {
        this.parseEmailInput();
        this.validateEmails();
        if (this.validInput != false) {
          await this.getEmailUsers();
          this.emitToParent(this.added);
          this.addedNotification();
          this.resetData();
        }
      },
      /**
       * Gets all the users's data using their email
       */
      async getEmailUsers() {
        let promiseArray = []
        for (let email of this.emailsArray) {
          promiseArray.push(api.getProfileByEmailAsync(email))
        }
        let registered = [];
        let vueObj = this;
        await Promise.all(promiseArray).then((values) => {
          for (let i = 0; i < values.length; i++) {
            if (values[i].data.results.length > 0) {
              let user = values[i].data.results[0]
              if (this.allUsersIds.includes(user.profile_id)) {
                this.alreadyAdded.push(this.emailsArray[i])
              } else {
                vueObj.added.push({
                  full_name: user.firstname + " " + user.lastname,
                  primary_email: user.primary_email,
                  profile_id: user.profile_id
                })
              }
              registered.push(this.emailsArray[i])
            }
          }
        })
        this.notRegistered = this.emailsArray.filter(x => !registered.includes(x));
      },
      /**
       * Used to reset the input box when the user starts typing in the input field again
       */
      resetValidity() {
        this.validInput = null;
        this.invalidEmail = '';
        this.invalidMessage = 'An error has occurred';
      },
      /**
       * Emits the final list of users' data to the parent based on the emails that were inputted
       * @param value the users' data
       */
      emitToParent(value) {
        this.$emit('usersAdded', value);
      },
      /**
       * The notification that displays to the user once users are successfully added.
       */
      addedNotification() {
        if (this.notRegistered.length > 0) {
          if (this.added.length > 0) {
            this.makeToast(
                'The following emails are not registered and users were not added: '
                + this.notRegistered.join('; '), 'warning', 7000);
            this.makeToast('Registered users with the specified emails were added', 'success');
          } else {
            this.makeToast(
                'All emails specified were not registered or were already apart of the activity, no users were added',
                'warning');
          }

        } else {
          this.makeToast('All users with specified emails were added', 'success');
        }
        if (this.alreadyAdded.length > 0) {
          this.makeToast('The following users with specified emails were already apart of the activity: ' + this.alreadyAdded.join('; '), 'success', 7000 )
        }
      },
      /**
       * Makes a toast notification
       * @param message the notification message
       * @param variant the colour of the notification based on variant (see Bootstrap Vue variants)
       * @param delay the milliseconds that the toast would stay on the screen
       */
      makeToast(message = 'EDMUN', variant = null, delay = 5000,) {
        this.$bvToast.toast(message, {
          variant: variant,
          solid: true,
          autoHideDelay: delay
        })
      },
      /**
       * Once a full process of adding users is done, it will reset the component data values awaiting for next input
       */
      resetData() {
        this.emailInput = '';
        this.emailsArray = [];
        this.validInput = null;
        this.invalidEmail = '';
        this.notRegistered = [];
        this.added = [];
        this.invalidMessage = 'An error has occurred';
        this.alreadyAdded = [];
      },
      async getCreatorEmails() {
        await api.getProfileEmails(this.loggedInId)
            .then((res) => {
                  let emails = res.data.emails;
                  for (let email of emails) {
                    this.creatorEmails.push(email.address);
                  }
                }
            )
            .catch(() => {
              alert("An error has occurred, please refresh the page")
            })
      }
    },
    computed: {
      hasInput() {
        return this.emailInput != '';
      }
    },
    mounted: async function () {
      await api.getActivityCreatorId(this.activityId).then((res) => {
        this.creatorId = res.data;
      })
      await this.getCreatorEmails();
    }
  }
</script>

<style scoped>

</style>