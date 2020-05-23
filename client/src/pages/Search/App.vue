<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <Search></Search>
    </div>
</template>

<script>
    import NavBar from '@/components/NavBar.vue';
    import Search from '@/components/Search.vue';

    import axios from 'axios'

    const App = {
        name: 'App',
        components: {
            NavBar, Search
        },
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                profileId: null
            }
        },
        methods: {
            getUserId: function () {
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/id')
                    .then((res) => {
                        this.profileId = res.data;
                        this.isLoggedIn = true;
                    })
                    .catch((err) => console.log(err));
            },
            getUserName: function () {
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/firstname')
                    .then((res) => {
                        this.userName = res.data;
                    })
                    .catch(err => console.log(err));
            },
        },
        mounted() {
            this.getUserId();
            this.getUserName();
        }
    };

    export default App;
</script>

<style scoped>
    #searchRow {
        padding-left: 20px;
        padding-top: 15px;
        padding-bottom: 15px;
        border-top: 1px solid #f0f0f5;
        border-bottom: 1px solid #D7D7D7;
        background-color: #F2F2F2;
        -webkit-box-shadow: inset 0 1px 0 0 #FFF;
        box-shadow: inset 0 1px 0 0 #FFF;
    }

    thead {
        display: none;
    }

    .searchByRadio {
        padding-right: 7px;
    }

    .cursor-pointer{
        cursor: pointer;
    }
</style>