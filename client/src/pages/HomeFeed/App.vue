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
                    <b-row style="margin-top:10px;">
                        <div v-for="item in items" :key="item" style="width:100%;margin-top:10px;">
                            <b-card :title="item.title">
                                <b-card-text>
                                    {{ item.text }}
                                </b-card-text>
                            </b-card>
                        </div>
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
                followingCount: 46,
                items: [
                    { title: 'Card 1', text: 'Card 1 text' },
                    { title: 'Card 2', text: 'Card 2 text' }
                ]
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