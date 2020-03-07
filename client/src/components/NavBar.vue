<template>
    <b-navbar toggleable="lg" type="dark" variant="success">
        <b-navbar-brand href="/login">Hakinakima</b-navbar-brand>

        <b-navbar-toggle fixed target="nav-collapse" toggleable="true"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav="prop">
            <b-navbar-nav>
                <b-nav-item href="/registration">Register</b-nav-item>
                <b-nav-item href="/profile">Profile</b-nav-item>
            </b-navbar-nav>

            <!-- Right aligned nav items -->
            <b-navbar-nav class="ml-auto">
                <b-nav-form>
                    <b-form-input class="mr-sm-2" placeholder="Search" size="sm"></b-form-input>
                    <b-button class="my-2 my-sm-0" size="sm" type="submit">Search</b-button>
                </b-nav-form>

                <b-nav-item-dropdown right text="Lang">
                    <b-dropdown-item href="#">EN</b-dropdown-item>
                    <b-dropdown-item href="#">ES</b-dropdown-item>
                    <b-dropdown-item href="#">RU</b-dropdown-item>
                    <b-dropdown-item href="#">FA</b-dropdown-item>
                </b-nav-item-dropdown>

                <b-nav-item-dropdown right>
                    <!-- Using 'button-content' slot -->
                    <template v-slot:button-content>
                        <em>User</em>
                    </template>
                    <b-dropdown-item href="/profile">Profile</b-dropdown-item>
                    <b-dropdown-item href="/login">Sign Out</b-dropdown-item>
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
        data: function () {
        },
        mounted() {
        }
    };
    export default NavBar
</script>

<style scoped>
    [v-cloak] {
        display: none;
    }
</style>
