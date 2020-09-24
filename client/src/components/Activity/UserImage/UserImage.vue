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
                          class="user-profile-img" size="2.5em" ></b-avatar>

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
             @mouseleave="show_overlay=false">
            <b-overlay :show="show_overlay && editable"
                       variant="dark" opacity="0.5"
                       rounded="lg" class="activity-profile-overlay fill_space">

                <!-- Avatar Image -->
                <b-img v-if="!default_image"
                       :src="image_src" alt=""
                       rounded="lg"
                       @error="handleInvalidSrcError"
                       class="user-profile-img fill_space"></b-img>
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
    import api from '@/Api'

    /**
     * User Image can be used with either activityId or userId. Component
     * is self contained and has all the logic and actions to add/edit/delete user images.
     * This component emits several events
     * @success : Triggered on user deleting or updating image successfully
     * @error(error_message) : Error on user deleting or updating image with an appropriate error message
     **/
    export default {
        name: "UserImage",
        data: function(){
            return {
                show_overlay: false,
                image_src: this.getUserImageSrc(this.id),

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
            id: {
                type: Number,
                default: -1
            },
            isActivity: {
                type: Boolean,
                default: false
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
            setUserImg: function() {
                this.getUserImageSrc(this.id);
            },

            handleInvalidSrcError: function() {
                this.user_has_image = false;
                this.default_image = true;
            },

            /**
             * gets user image src location from
             * @param id either activity or user id
             * @returns the url location of where to get/update/delete the image
             */
            getUserImageSrc: function(id) {
                let image_url;

                if (this.isActivity === true) {
                  image_url = process.env.VUE_APP_SERVER_ADD + "/profiles/1/activities/" + id + "/image?cache=" + Math.random();
                } else {
                  image_url =  process.env.VUE_APP_SERVER_ADD + "/profiles/" + id + "/image?cache=" + Math.random()
                }
                return image_url;
            },

            /**
             * handler for user selecting a file and uploads it to api if save-on-change is true
             * otherwise stores it for later
             * @param event file data from upload prompt
             */
            handleImageUpload: function(event) {
                this.image_data = event.target.files[0];
                this.image_src = URL.createObjectURL(this.image_data);
                this.default_image = false;

                this.last_user_action = 'add';

                if(this.saveOnChange) {
                    this.saveChanges(this.id);
                }
            },

            /**
             * Sets image to be deleted, if save-on-change is true, calls the api to remove
             */
            handleImageDelete: function() {
                this.image_src = '';
                this.default_image = true;

                this.last_user_action = 'remove';

                if(this.saveOnChange) {
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
            saveChanges: function(id) {
                if(this.last_user_action === 'add') {
                    this.putImageToAPI(id);
                } else if(this.last_user_action === 'remove') {
                    if(this.user_has_image) {
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
                let formData = new FormData();
                formData.append("file", this.image_data);

                if (this.isActivity === true) {
                    api.updateActivityImage(id, formData).then(
                        ).catch((error) => {
                            console.log(error.response);
                            this.$emit('error', error.response.data);
                        }
                    );
                } else {
                    api.updateProfileImage(id, formData).then(
                        ).catch((error) => {
                            console.log(error.response);
                            this.$emit('error', error.response.data);
                        }
                    );
                }

            },

            /**
             * Calls delete api
             * @param id activity or user id
             */
            deleteImageToAPI: function (id) {
                if (this.isActivity === true) {
                    api.deleteActivityImage(id).then(
                        ).catch((error) => {
                          console.log(error.response);
                        }
                    );
                } else {
                    api.deleteProfileImage(id).then(
                       ).catch((error) => {
                          console.log(error.response);
                       }
                    );
                }
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
        pointer-events:none;
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
