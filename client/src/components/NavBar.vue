<template>
    <b-navbar class="nav-bar" fixed toggleable="lg" type="light" variant="--white">
        <b-navbar-brand href="/login">Hakinakima</b-navbar-brand>

        <b-navbar-toggle fixed target="nav-collapse" toggleable="true"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav>
                <b-nav-item href="/registration">Register</b-nav-item>
                <b-nav-item href="/profile">Profile</b-nav-item>
            </b-navbar-nav>

            <!-- Right aligned nav items -->
            <b-navbar-nav class="ml-auto">
                <b-nav-form>
                    <b-form-input class="mr-sm-2" placeholder="Search" size="sm"></b-form-input>
                    <b-button class="searchbtn" size="sm" type="submit" variant="primary">Search</b-button>
                </b-nav-form>
            </b-navbar-nav>

            <b-navbar-nav>
                <b-button class="loginbtn" href="/login" size="sm" type="button" v-if="!isLoggedIn" variant="primary">
                    Log in
                </b-button>
                <b-nav-item-dropdown right v-else>
                    <template v-slot:button-content>
                        <em>anything</em>
                    </template>
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
        props: ['isLoggedIn'],
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
                this.axios.get('http://localhost:9499/account/logout')
                    .then(function (response) {
                        console.log(response.data);
                        window.location.href = '/login';
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
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
