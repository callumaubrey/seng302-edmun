<template>
    <b-navbar class="nav-bar" fixed toggleable="lg" type="light" variant="--white">
        <b-navbar-brand v-if="!isLoggedIn" to='/'>Edmun</b-navbar-brand>
        <b-navbar-brand v-if="isLoggedIn" @click="goToProfile">Edmun</b-navbar-brand>
        <b-navbar-toggle fixed target="nav-collapse" toggleable="true"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav>
                <b-nav-item v-if="!isLoggedIn" to='/register'>Register</b-nav-item>
                <b-nav-item v-if="isLoggedIn" @click="goToProfile">Profile</b-nav-item>
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
                    <b-dropdown-item @click="goToEdit">Edit Profile</b-dropdown-item>
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
        props: ['isLoggedIn', 'userName'],
        data: function () {
            return {
                name: ""
            }
        },
        watch: {
            isLoggedIn: function (newVal, oldVal) {
                console.log('IsLoggedIn prop changed' + newVal + ' ' + oldVal)
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
            goToEdit() {
                const profileId = this.$route.params.id;
                this.$router.push('/profile/edit/' + profileId);
            },
            goToProfile() {
                const profileId = this.$route.params.id;
                this.$router.push('/profile/' + profileId);
            }
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

</style>
