<template>
  <div class="d-flex justify-content-center fill_space">

    <!-- Profile Image Template -->
    <div v-if="!isActivity"
         class="user-profile-img-container"
         @mouseover="show_overlay=true"
         @mouseleave="show_overlay=false">


      <b-overlay :show="show_overlay && editable"
                 variant="dark" opacity="0.5"
                 rounded="circle" class="user-profile-overlay">

        <!-- Avatar Image -->
        <b-avatar :src="image_src" @img-error="handleInvalidSrcError"
                  class="user-profile-img" size="2.5em"></b-avatar>

        <!-- Overlay Content -->
        <template v-slot:overlay>
          <div class="text-center user-profile-overlay-content">

            <!-- Add Option -->
            <div v-if="default_image">
              <label for="file-upload">
                  <span class="fa-stack fa-2x user-profile-img-icon-add">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-plus fa-stack-1x fa-inverse"></i>
                  </span>
              </label>
            </div>

            <!-- Edit and Delete Options -->
            <div v-else>
              <label for="file-upload">
                  <span class="fa-stack fa-2x user-profile-img-icon-edit">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-pen fa-stack-1x fa-inverse"></i>
                  </span>
              </label>
              <span class="fa-stack fa-2x user-profile-img-icon-remove" @click="handleImageDelete">
                  <i class="fas fa-circle fa-stack-2x"></i>
                  <i class="fas fa-trash fa-stack-1x fa-inverse"></i>
                </span>
            </div>

            <!-- Image Input -->
            <input id="file-upload" class="d-none"
                   accept=".gif,.jpg,.png,.jpeg" type="file"
                   @change="handleImageUpload"/>
          </div>
        </template>

      </b-overlay>

    </div>

    <!-- Activity Image Template -->
    <div v-else
         class="fill_space"
         @mouseover="show_overlay=true"
         @mouseleave="show_overlay=false"
     >
      <b-overlay :show="show_overlay && editable"
                 variant="dark" opacity="0.5"
                 rounded="lg" class="activity-profile-overlay fill_space">

        <!-- Activity Image -->
        <b-img v-if="!default_image"
               :src="image_src" alt=""
               rounded="lg"
               @error="handleInvalidSrcError"
               class="user-profile-img fill_space"
        ></b-img>
        <!-- Default Image -->

        <div v-else
             class="activity_default_img_container user-profile-img fill_space rounded-lg">
          <b-img :src="require('@/assets/goatonly_navbar.png')" style="max-height: 50%"></b-img>
        </div>

        <!-- Overlay Content -->
        <template v-slot:overlay>
          <div class="text-center user-profile-overlay-content">

            <!-- Add Option -->
            <div v-if="default_image">
              <label for="file-upload">
                  <span class="fa-stack fa-2x user-profile-img-icon-add">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-plus fa-stack-1x fa-inverse"></i>
                  </span>
              </label>
            </div>

            <!-- Edit and Delete Options -->
            <div v-else>
              <label for="file-upload">
                  <span class="fa-stack fa-2x user-profile-img-icon-edit">
                    <i class="fas fa-circle fa-stack-2x"></i>
                    <i class="fas fa-pen fa-stack-1x fa-inverse"></i>
                  </span>
              </label>
              <span class="fa-stack fa-2x user-profile-img-icon-remove" @click="handleImageDelete">
                  <i class="fas fa-circle fa-stack-2x"></i>
                  <i class="fas fa-trash fa-stack-1x fa-inverse"></i>
                </span>
            </div>

            <span style="color: pink">
                  {{imageWarning}}
            </span>

            <!-- Image Input -->
            <input id="file-upload" class="d-none"
                   accept=".gif,.jpg,.png,.jpeg" type="file"
                   @change="handleImageUpload"/>
          </div>
        </template>

      </b-overlay>
    </div>
  </div>
</template>

<script>
  import axios from 'axios'
  import api from '@/Api';

  /**
   * User Image can be used with either activityId or userId. Component
   * is self contained and has all the logic and actions to add/edit/delete user images.
   * This component emits several events
   * @success : Triggered on user deleting or updating image successfully
   * @error(error_message) : Error on user deleting or updating image with an appropriate error message
   **/
  export default {
    name: "UserImage",
    data: function () {
      return {
        show_overlay: false,
        image_src: null,

        // Assume this is true until 404 error
        user_has_image: true,

        // Is current avatar default
        default_image: false,

        // Uploaded image data
        image_data: null,

        last_user_action: null
      }
    },
    props: {
      editable: {
        type: Boolean,
        default: false,
      },
      saveOnChange: {
        type: Boolean,
        default: false,
      },
      userId: {
        type: Number,
        default: -1
      },
      activityId: {
        type: Number,
      },
      isActivity: {
        type: Boolean,
        default: false
      },
      imageWarning: {
        type: String,
        default: "The image will be stretched if it does not match the aspect ratio"
      }
    },
    mounted() {
      this.setUserImg();
    },
    watch: {
      '$route': 'setUserImg'
    },
    methods: {
      /**
       * Updates user image using image src generator function
       */
      setUserImg: function () {
        this.getUserImageSrc(this.userId, this.activityId);
      },

      handleInvalidSrcError: function () {
        this.user_has_image = false;
        this.default_image = true;
      },

      /**
       * gets user image src location from
       * @param id either activity or user id
       * @returns the url location of where to get/update/delete the image
       */
      getUserImageSrc: function (userId, activityId) {
        /**
         * ==============================================
         * REPLACE ME WITH ACTUAL ROUTE
         * Make sure to check if user or activity for url
         * ==============================================
         */
        if (this.isActivity) {
          api.getActivityImage(userId, activityId).then((response) => {
            let image = response.data;
            console.log(image)
            this.image_src = URL.createObjectURL(image)
            console.log(this.image_src)
          }).catch(() => {
            this.default_image = true;
          })
        }
        return api.getActivityImage(userId, activityId);
      },

      /**
       * handler for user selecting a file and uploads it to api if save-on-change is true
       * otherwise stores it for later
       * @param event file data from upload prompt
       */
      handleImageUpload: function (event) {
        this.image_data = event.target.files[0];
        console.log(event.target.files[0])
        this.image_src = URL.createObjectURL(this.image_data);
        this.default_image = false;

        this.last_user_action = 'add';

        if (this.saveOnChange) {
          this.saveChanges(this.id);
        }
      },

      /**
       * Sets image to be deleted, if save-on-change is true, calls the api to remove
       */
      handleImageDelete: function () {
        this.image_src = '';
        this.default_image = true;

        this.last_user_action = 'remove';

        if (this.saveOnChange) {
          this.saveChanges(this.id);
        }
      },

      /**
       * Calls appropriate api given last user action. This is a simple form of version history, so that
       * the right method is called. For example, say the users adds an image, then replaces it, then deletes it.
       * The api call to actually occur should be remove.
       *
       * This method should be called if wanting to update image from external input(submit button)
       * @param id user_id or activity id
       */
      saveChanges: function (id) {
        if (this.last_user_action === 'add') {
          this.putImageToAPI(id);
        } else if (this.last_user_action === 'remove') {
          if (this.user_has_image) {
            this.deleteImageToAPI(id);
          }
        }
      },

      /**
       * Calls put api using uploaded image data as request content.
       * @param id activity or user id
       */
      putImageToAPI: function (id) {
        // Set MIME data type
        let request_config = {
          headers: {
            'Content-Type': this.image_data.type
          }
        };

        // Call api
        axios.put(this.getUserImageSrc(id, this.activityId), this.image_data, request_config)
            .then(() => {
              this.$emit('success');
            })
            .catch((error) => {
              let error_message = '';

              if (error.response) {
                console.log(error.response);
                switch (error.response.status) {
                  case 400:
                    error_message = 'Image must be of type gif, png or jpeg';
                    break;
                  case 401:
                    error_message = 'Must be logged in to change photo';
                    break;
                  case 403:
                    error_message = 'Unauthorised to change photo';
                    break;
                  case 404:
                    error_message = (this.isActivity ? 'activity' : 'user') + ' does not exist';
                    break;
                  case 500:
                    error_message = 'Internal server error, try again later';
                    break;
                  default:
                    error_message = 'Unknown error occurred';
                }
              } else {
                error_message = 'Could not connect to server';
              }

              this.setUserImg();
              this.$emit('error', error_message);
            });
      },

      /**
       * Calls delete api
       * @param id activity or user id
       */
      deleteImageToAPI: function (id) {
        axios.delete(this.getUserImageSrc(id, this.activityId))
            .then(() => {
              this.$emit('success');
            })
            .catch((error) => {
              console.error(error);
              this.setUserImg();
              this.$emit('error');
            });
      }
    }
  }
</script>

<style scoped>
  .user-profile-img-container {
    height: 2.5em;
    width: 2.5em;
    border-radius: 100%;
  }

  .user-profile-img {
    pointer-events: none;
    border-style: solid;
    border-width: 0.08em;
    border-color: darkgray;

  }

  .user-profile-overlay {
    pointer-events: none;
  }

  .user-profile-overlay-content {
    pointer-events: all;
    white-space: nowrap;
    font-size: 0.18em;
    cursor: default;
  }

  .user-profile-img-icon-add:hover {
    color: #48ee78;
    cursor: pointer;
  }

  .user-profile-img-icon-edit:hover {
    color: #efce55;
    cursor: pointer;
  }

  .user-profile-img-icon-remove:hover {
    color: #f84040;
    cursor: pointer;
  }

  .activity_default_img_container {
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #6c757d;
  }

  .fill_space {
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
  }

</style>
