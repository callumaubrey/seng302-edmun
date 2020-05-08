<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>

        <b-container>
            <b-row>
                <b-col>
                    <h1>User Search</h1>
                </b-col>
            </b-row>
            <b-container id="searchRow">
                <b-row>
                    <b-col>
                        <b-form inline>
                            <b-input v-model="searchQuery" style="width:400px;margin-right:10px;"
                                     placeholder="Search"></b-input>
                            <b-form-radio v-model="searchBy" class="searchByRadio" value="fullName">Full Name
                            </b-form-radio>
                            <b-form-radio v-model="searchBy" class="searchByRadio" value="email">Email
                            </b-form-radio>
                            <b-form-radio v-model="searchBy" class="searchByRadio" value="nickname">Nickname
                            </b-form-radio>

                        </b-form>
                    </b-col>
                    <b-col sm="2">
                        <b-button class="float-right" @click.prevent="updateUrl()" v-on:click="searchUser()"
                                  variant="light">Search
                        </b-button>
                    </b-col>


                </b-row>
                <br>
                <b-row>
                    <b-col>
                        <b-form-tags v-model="activityTypesForm.selectedOptions" no-outer-focus
                                     size="lg">
                            <template v-slot="{ tags, disabled, addTag, removeTag }">
                                <ul v-if="tags.length > 0" class="list-inline d-inline-block mb-2">
                                    <li v-for="tag in tags" :key="tag" class="list-inline-item">
                                        <b-form-tag
                                                @remove="removeTag(tag)"
                                                :title="tag"
                                                :disabled="disabled"
                                                variant="primary"
                                        >{{ tag }}
                                        </b-form-tag>
                                    </li>
                                </ul>
                                <b-dropdown size="sm" variant="outline-secondary" block menu-class="w-100">
                                    <template v-slot:button-content>
                                        <b-icon icon="tag-fill"></b-icon>
                                        Choose activity types to search by
                                    </template>
                                    <b-dropdown-form @submit.stop.prevent="() => {}">
                                        <b-form-group
                                                label-for="tag-search-input"
                                                label="Search activity types"
                                                label-cols-md="auto"
                                                class="mb-0"
                                                label-size="sm"
                                                :description="searchDesc"
                                                :disabled="disabled"
                                        >
                                            <b-form-input
                                                    v-model="activityTypesForm.search"
                                                    id="tag-search-input"
                                                    type="search"
                                                    size="sm"
                                                    autocomplete="off"
                                            ></b-form-input>
                                        </b-form-group>
                                    </b-dropdown-form>
                                    <b-dropdown-divider></b-dropdown-divider>
                                    <b-dropdown-item-button
                                            v-for="option in availableOptions"
                                            :key="option"
                                            @click="onOptionClick({ option, addTag })"
                                    >
                                        {{ option }}
                                    </b-dropdown-item-button>
                                    <b-dropdown-text v-if="availableOptions.length === 0">
                                        There are no activity types available to select
                                    </b-dropdown-text>
                                </b-dropdown>
                            </template>

                        </b-form-tags>
                    </b-col>
                </b-row>
                <br>
                <b-row>
                    <b-col>

                        <b-form inline>
                            <label style="margin-right:10px;">Search Method: </label>
                            <b-form-radio-group id="activityTypesSearchMethods" v-model="activityTypesForm.method" aria-describedby="activityTypesSearchMethodsHelp">
                                <b-form-radio class="searchByRadio" value="AND">And
                                </b-form-radio>
                                <b-form-radio class="searchByRadio" value="OR">Or
                                </b-form-radio>
                            </b-form-radio-group>

                        </b-form>
                        <b-form-text id="activityTypesSearchMethodsHelp">
                            The option you select determines how activity types are searched e.g Hiking and Biking
                        </b-form-text>
                    </b-col>
                </b-row>
            </b-container>

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
                    <b-form-select v-model="limit" v-on:change="getUsers()" :options="[10,20]"></b-form-select>
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
                    {key: 'lastname', sortable: true},
                    {key: 'firstname', sortable: true},
                    {key: 'middlename', sortable: true},
                    {key: 'nickname', sortable: true},
                    {key: 'primary_email', sortable: true},
                    {key: 'activity_types', sortable: false},
                ],
                currentPage: 1,
                count: 1,
                limit: 10,
                data: null,
                routeQuery: {},
                offset: null,
                activityTypesForm: {
                    options: [],
                    search: "",
                    selectedOptions: [],
                    method: "AND"
                }
            }
        },
        computed: {
            criteria() {
                // Compute the search criteria
                return this.activityTypesForm.search.trim().toLowerCase()
            },
            availableOptions() {
                const criteria = this.criteria
                // Filter out already selected options
                const options = this.activityTypesForm.options.filter(opt => this.activityTypesForm.selectedOptions.indexOf(opt) === -1)
                if (criteria) {
                    // Show only options that match criteria
                    return options.filter(opt => opt.toLowerCase().indexOf(criteria) > -1);
                }
                // Show all options available
                return options
            },
            searchDesc() {
                if (this.criteria && this.availableOptions.length === 0) {
                    return 'There are no tags matching your search criteria'
                }
                return ''
            }
        },
        methods: {
            onOptionClick({option, addTag}) {
                addTag(option)
                this.activityTypesForm.search = ''
            },
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
                if (this.searchQuery === '' && this.activityTypesForm.selectedOptions == '') return;
                //This funtion seem to take at leaat 10 seconds to execute, when using database
                const currentObj = this;
                this.offset = (this.currentPage - 1) * this.limit;

                // Full name by default
                let query = 'http://localhost:9499/profiles';
                if (this.searchBy == 'fullName' && this.searchQuery != "" && this.activityTypesForm.selectedOptions != '') {
                    query = this.searchNames(query)
                    query += "&activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery = {activity: this.activityTypesForm.selectedOptions.join(' ')}
                    this.routeQuery.method = this.activityTypesForm.method
                    alert("hello")
                }else if (this.searchQuery != ''){
                    query = this.searchNames(query)
                }else {
                    query += "?activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery = {activity: this.activityTypesForm.selectedOptions.join(' ')}
                    this.routeQuery.method = this.activityTypesForm.method
                }
                this.routeQuery.offset = this.offset;
                this.routeQuery.limit = this.limit;
                alert(query)
                this.axios.get(query + '&offset=' + this.offset + "&limit=" + this.limit)
                    .then((res) => {
                        currentObj.data = res.data.results;
                    })
                    .catch(err => console.log(err));
            },
            searchNames: function (query){
                if (this.searchBy == 'email') {
                    this.routeQuery = {email: this.searchQuery};
                    query += '?email=' + this.searchQuery;
                }
                else if (this.searchBy == 'nickname') {
                    this.routeQuery = {nickname: this.searchQuery};
                    query += '?nickname=' + this.searchQuery;
                }
                else {
                    this.routeQuery = {fullname: this.searchQuery};
                    query += '?fullname=' + this.searchQuery;
                }
                return query;
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
                    this.searchUser();
                } else if (fromNavBar) {
                    this.searchUser();
                }
            },
            searchUser: function () {
                this.currentPage = 1
                if (this.searchQuery === '' && this.activityTypesForm.selectedOptions == '') return;
                let query = 'http://localhost:9499/profiles/count';
                if (this.searchBy == 'fullName' && this.searchQuery != "" && this.activityTypesForm.selectedOptions != '') {
                    query = this.searchNames(query)
                    query += "&activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    // this.routeQuery.activity = this.activityTypesForm.selectedOptions.join(' ')
                }else if (this.searchQuery != ''){
                    query = this.searchNames(query)
                }else {
                    query += "?activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery.activity = this.activityTypesForm.selectedOptions.join(' ')
                }
                const currentObj = this
                this.count = 10
                console.log(query);
                this.axios.get(query)
                    .then((res) => {
                        currentObj.count = res.data/2
                    })
                    .catch(err => console.log(err));
                currentObj.getUsers()

            },
            getActivities: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/activity-types')
                    .then(function (response) {
                        console.log(response.data);
                        currentObj.activityTypesForm.options = response.data;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },

        },
        mounted() {
            this.getUserId();
            this.getUserName();
            this.populatePage();
            this.getActivities();
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
</style>