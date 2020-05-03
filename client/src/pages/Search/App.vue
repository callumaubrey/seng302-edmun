<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>

        <b-container>
            <b-row>
                <b-col>
                    <h1>User Search</h1>
                </b-col>
            </b-row>

            <b-row style="margin-top:30px;" id="searchRow">
                <b-col>
                    <b-form inline>
                        <b-input v-model="searchQuery" style="width:260px;margin-right:10px;" placeholder="Search"></b-input>
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="fullName">Full Name</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="email">Email</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="nickname">Nickname</b-form-radio>
                        <b-button @click.prevent="updateUrl()" v-on:click="getUsers()" variant="light">Search</b-button>
                    </b-form>
                </b-col>
            </b-row>

            <b-row>
                <b-col>
                    <b-table
                            :fields="fields"
                            :items="data"
                    >
                    </b-table>
                </b-col>
            </b-row>
            <b-row v-if="this.data != null">
                <b-col>
                    <b-pagination
                            v-model="currentPage"
                            :total-rows="count"
                            :per-page="limit"
                            @input="getUsers()"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-form-select v-model="limit" v-on:change="getUsers()" :options="[2,4,6,8,10]"></b-form-select>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    import NavBar from '@/components/NavBar.vue';

    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                searchQuery: '',
                searchBy: 'fullName',
                fields: [
                    { key: 'lastname', sortable: true },
                    { key: 'firstname', sortable: true },
                    { key: 'middlename', sortable: true },
                    { key: 'nickname', sortable: true },
                    { key: 'primary_email', sortable: true },
                ],
                currentPage: 1,
                count:null,
                limit:2,
                data: null,
                routeQuery: {},
                offset: null
            }
        },
        methods: {
            getUserId: function () {
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                .then((res) => {
                    this.profileId = res.data;
                    this.isLoggedIn = true;
                })
                .catch((err) => console.log(err));
            },
            getUserName: function () {
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/user')
                .then((res) => {
                    this.userName = res.data;
                })
                .catch(err => console.log(err));
            },
            getUsers: function () {
                if (this.searchQuery === '') return;
                //This funtion seem to take at leaat 10 seconds to execute, when using database
                const currentObj = this;
                this.offset = (this.currentPage - 1) * this.limit;

                this.routeQuery = {fullname: this.searchQuery};
                // Full name by default
                let query = 'http://localhost:9499/profiles?fullname='+this.searchQuery;
                if (this.searchBy == 'email') {
                    this.routeQuery = {email: this.searchQuery};
                    query = 'http://localhost:9499/profiles?email='+this.searchQuery;
                }
                if (this.searchBy == 'nickname') {
                    this.routeQuery = {nickname: this.searchQuery};
                    query = 'http://localhost:9499/profiles?nickname='+this.searchQuery;
                }
                this.routeQuery.offset = this.offset;
                this.routeQuery.limit = this.limit;
                this.axios.get(query + '&offset=' + this.offset + "&limit=" + this.limit)
                    .then((res) => {
                        currentObj.data = res.data.results;
                        currentObj.count = res.data.results.length
                    })
                    .catch(err => console.log(err));
            },
            updateUrl: function () {
                this.$router.push({name: "Users", query: this.routeQuery});
            },
            getUsersFromNavBar: function () {
                let navBarQuery = this.$route.query.fullname;
                if (this.searchQuery === '' && navBarQuery) {
                    this.searchQuery = navBarQuery;
                    return true;
                }
                return false;
            },
            populatePage: function () {
                let fromNavBar = this.getUsersFromNavBar();
                if (this.$route.query.fullname) {
                    this.searchQuery = this.$route.query.fullname;
                    this.searchBy = "fullName";
                } else if (this.$route.query.firstname) {
                    this.searchQuery = this.$route.query.firstname;
                    this.searchBy = "firstName";
                } else if (this.$route.query.lastname) {
                    this.searchQuery = this.$route.query.lastname;
                    this.searchBy = "lastName";
                } else if (this.$route.query.email) {
                    this.searchQuery = this.$route.query.email;
                    this.searchBy = "email";
                } else if (this.$route.query.nickname) {
                    this.searchQuery = this.$route.query.nickname;
                    this.searchBy = "nickname";
                }

                // if offset is null, the user is entering the search page for the first time, which means
                // there wouldn't be a need to repopulate the page (except when query is from the nav bar)
                if (this.$route.query.offset) {
                    this.offset = this.$route.query.offset;
                    this.limit = this.$route.query.limit;
                    this.getUsers();
                } else if (fromNavBar) {
                    this.getUsers();
                }
            },
        },
        mounted() {
            this.getUserId();
            this.getUserName();
            this.populatePage();
        }
    };

    export default App;
</script>

<style scoped>
    #searchRow {
        padding-left:20px;
        padding-top:15px;
        padding-bottom:15px;
        border-top: 1px solid #f0f0f5;
        border-bottom: 1px solid #D7D7D7;
        background-color: #F2F2F2;
        -webkit-box-shadow: inset 0 1px 0 0 #FFF;
        box-shadow: inset 0 1px 0 0 #FFF;
    }

    thead {
        display:none;
    }

    .searchByRadio {
        padding-right:7px;
    }
</style>