<template>
    <b-navbar class="nav-bar" fixed toggleable="lg" type="dark" variant="dark">
        <b-navbar-brand v-if="!isLoggedIn" to='/'>Edmun</b-navbar-brand>
        <b-navbar-brand v-if="isLoggedIn" @click="goToProfile">Edmun</b-navbar-brand>
        <b-navbar-toggle fixed target="nav-collapse" toggleable="true"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav>
                <b-nav-item v-if="!isLoggedIn" to='/register'>Register</b-nav-item>
                <b-nav-item v-if="isLoggedIn" @click="goToProfile">Profile</b-nav-item>
                <b-nav-item v-if="isLoggedIn" @click="goToActivities">Activities</b-nav-item>
                <b-collapse id="my-collapse" v-if="isLoggedIn">
                    <b-form inline>
                        <b-input-group>
                            <template v-slot:prepend>
                                <b-form-select v-model="searchBy" :options="searchOptions"></b-form-select>
                            </template>

                            <b-form-input placeholder="Search" v-model="searchQuery"></b-form-input>

                            <b-input-group-append>
                                <b-button @click="search()"><b-icon-search></b-icon-search></b-button>
                            </b-input-group-append>
                        </b-input-group>
                    </b-form>
                </b-collapse>
                <b-nav-item v-if="isLoggedIn">
                    <span v-b-toggle.my-collapse>
                        <b-icon-x class="when-opened">Close</b-icon-x> <b-icon-search class="when-closed">Open</b-icon-search>
                    </span>
                </b-nav-item>
            </b-navbar-nav>

            <!--            &lt;!&ndash; Right aligned nav items &ndash;&gt;-->
            <!--            <b-navbar-nav class="ml-auto">-->
            <!--                <b-nav-form>-->
            <!--                    <b-form-input class="mr-sm-2" placeholder="Search" size="sm"></b-form-input>-->
            <!--                    <b-button class="searchbtn" size="sm" type="submit" variant="primary">Search</b-button>-->
            <!--                </b-nav-form>-->
            <!--            </b-navbar-nav>-->

            <b-navbar-nav class="ml-auto">
                <b-button class="loginbtn" to="/login" size="sm" type="button" v-if="!isLoggedIn" variant="primary">
                    Log in
                </b-button>
                <b-nav-item-dropdown right v-else>
                    <template v-slot:button-content>
                        <em>{{ userName }}</em>
                    </template>
                    <b-dropdown-item @click="goToEdit" v-if="!hideElements">Edit Profile</b-dropdown-item>
                    <b-dropdown-item @click="logout">Log Out</b-dropdown-item>
                </b-nav-item-dropdown>
            </b-navbar-nav>


        </b-collapse>
    </b-navbar>
</template>

<script>
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'

    const NavBar = {
        name: 'NavBar',
        components: {},
        props: ['isLoggedIn', 'userName', 'hideElements', 'loggedInId'],
        data: function () {
            return {
                name: "",
                searchBy: 1,
                searchQuery: "",
                searchOptions: [
                    { value: 1, text: 'Users' }
                ]
            }
        },
        watch: {
            isLoggedIn: function (newVal, oldVal) {
                console.log('IsLoggedIn prop changed to ' + newVal + " from old value: " + oldVal);
            }
        },
        methods: {
            logout() {
                const vueObj = this;
                this.axios.get('http://localhost:9499/logout/')
                    .then(function (response) {
                        console.log(response.data);
                        vueObj.$router.push('/');
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },
            getUserId: async function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profileId = response.data;
                    })
                    .catch(function () {
                    });
            },
            goToEdit() {
                const profileId = this.loggedInId;
                this.$router.push('/profiles/edit/' + profileId);
                this.$router.go('/profiles/edit/' + profileId);
            },
            goToProfile() {
                const profileId = this.loggedInId;
                this.$router.push('/profiles/' + profileId);
                this.$router.go(0);
            },
            goToActivities() {
                const profileId = this.profileId;
                this.$router.push('/profiles/' + profileId + '/activities');
            },
            getUserName() {
                let currentObj = this;
                this.axios.get('http://localhost:9499/profiles/user')
                    .then(function (response) {
                        currentObj.userName = response.data.firstname;
                    })
                    .catch(function () {
                    });
            },
            search() {
                if (this.searchQuery === '') return;

                this.$router.push('/profiles?fullname=' + this.searchQuery);
            }
        },
        mounted: function () {
            this.getUserId();
        }
    };
    export default NavBar
</script>

<style scoped>
    [v-cloak] {
        display: none;
    }

    .searchbtn {
        margin-right: 10px;
    }
    .nav-bar{
        margin-bottom: 50px;
        margin-right: 0px;
    }

    .collapsed > .when-opened,
    :not(.collapsed) > .when-closed {
        display: none;
    }
</style>
