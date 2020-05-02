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
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="firstName">First Name</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="nickname">Nickname</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio" value="lastName">Last Name</b-form-radio>
                        <b-button variant="light" v-on:click="getUsers()">Search</b-button>
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
                if (this.searchQuery === '') return
                //This funtion seem to take at leaat 10 seconds to execute, when using database
                const currentObj = this;
                const offset = (this.currentPage-1) * this.limit

                // Full name by default
                let query = 'http://localhost:9499/profiles?fullname='+this.searchQuery;
                if (this.searchBy == 'email') {
                    query = 'http://localhost:9499/profiles?email='+this.searchQuery;
                }
                if (this.searchBy == 'nickname') {
                    query = 'http://localhost:9499/profiles?nickname='+this.searchQuery;
                }
                this.axios.get(query + '&offset=' + offset + "&limit=" +this.limit)
                    .then((res) => {
                        currentObj.data = res.data.results
                        currentObj.count = res.data.results.length
                    })
                    .catch(err => console.log(err));
            }

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