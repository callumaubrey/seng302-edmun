<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <b-container>
            <b-row>
                <b-col cols="2">
                    <b-row align-h="center">
                        <b-avatar size="6rem" style="text-align: center"></b-avatar>
                    </b-row>
                    <b-row style="margin-top: 10px;" align-h="center">
                        <p style="padding-bottom:0;margin-bottom: 0;font-size:18px">{{ fullName }}</p>
                        <p style="font-size:14px">Following: {{ followingCount }}</p>
                    </b-row>
                </b-col>
                <b-col cols="10">
                    <b-row align-h="center">
                        <h3>{{ userName }}'s Feed</h3>
                    </b-row>
                    <b-row style="margin-top:20px;">
                        <b-card title="Activity Update Card 1" style="width:100%">
                            <b-card-text>
                                Some quick example text to build on the <em>card title</em> and make up the bulk of the card's
                                content.
                            </b-card-text>
                        </b-card>

                        <b-card title="Activity Update Card 2" style="width:100%; margin-top:10px;">
                            <b-card-text>
                                Some quick example text to build on the <em>card title</em> and make up the bulk of the card's
                                content.
                            </b-card-text>
                        </b-card>
                    </b-row>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    import NavBar from '@/components/NavBar.vue';
    import axios from 'axios';

    export default {
        components: {
            NavBar
        },
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                fullName: '',
                followingCount: 46
            }
        },
        methods: {
            getUser: function () {
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/user')
                    .then((res) => {
                        this.userName = res.data.firstname;
                        this.fullName = res.data.firstname + ' ' + res.data.lastname;
                        this.isLoggedIn = true;
                    })
                    .catch(err => console.log(err));
            }
        },
        mounted() {
            this.getUser();
        }
    }
</script>