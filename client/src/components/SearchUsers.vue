<template>
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
                    <b-button :disabled=isSearchButtonDisabled() class="float-right" @click.prevent="updateUrl()" v-on:click="searchUser()"
                              @keydown.enter.native="searchUser()"
                              variant="light">Search
                    </b-button>
                </b-col>


            </b-row>
            <br>
            <div v-if="searchBy!='email'" id="acitivity-tags">
                <b-row >
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
            </div>
        </b-container>

        <b-row>
            <b-col>
              <SearchProfileList :profile_data="data"></SearchProfileList>
              <template v-if="tableIsLoading">
                <div class="text-center text-primary my-2">
                  <b-spinner class="align-middle"></b-spinner>
                  <strong> Loading...</strong>
                </div>
              </template>
            </b-col>
        </b-row>
        <b-row v-if="this.data != null">
            <b-col>
                <b-pagination
                        v-model="currentPage"
                        :total-rows="count"
                        :per-page="limit"
                        @input="calibratePagination()"
                ></b-pagination>
            </b-col>
            <b-col>
                <b-form-select v-model="limit" v-on:change="getUsers()" :options="[10,20]"></b-form-select>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import api from '@/Api'
import SearchProfileList from "@/components/SearchProfileList";

export default {
  name: 'Search',
  components: {SearchProfileList},
  data() {
    return {
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
      },
      tableIsLoading: false,
    }
  },
  computed: {
    criteria() {
      // Compute the search criteria
      return this.activityTypesForm.search.trim().toLowerCase()
            },
            availableOptions() {
                const criteria = this.criteria;
                // Filter out already selected options
                const options = this.activityTypesForm.options.filter(opt => this.activityTypesForm.selectedOptions.indexOf(opt) === -1);
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
                addTag(option);
                this.activityTypesForm.search = ''
            },
            getUsers: function () {
                if (this.searchQuery.trim() === '' && this.activityTypesForm.selectedOptions.length === 0) {
                    this.tableIsLoading = false;
                    return;
                }
                const currentObj = this;
                this.offset = (this.currentPage - 1) * this.limit;
                // Full name by default
                let query = '/profiles';
                console.log(this.activityTypesForm.selectedOptions.length === 0);
                if (this.searchBy !== 'email' && this.searchQuery.trim() !== "" && this.activityTypesForm.selectedOptions.length !== 0) {
                    query = this.searchNames(query);
                    console.log("reached1");
                    query += "&activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery.activity = this.activityTypesForm.selectedOptions.join(' ');
                    this.routeQuery.method = this.activityTypesForm.method;
                }else if (this.searchQuery.trim() !== ''){
                    console.log("reached2");
                    query = this.searchNames(query);
                    this.routeQuery.method = this.activityTypesForm.method;
                }
                else if (this.activityTypesForm.selectedOptions !== '') {
                    console.log("reached3");
                    query += "?activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery.activity = this.activityTypesForm.selectedOptions.join(' ');
                    this.routeQuery.method = this.activityTypesForm.method;
                } else {
                    query += "&method=" + this.activityTypesForm.method;
                    this.routeQuery.method = this.activityTypesForm.method;
                }
                this.routeQuery.page = this.currentPage;
                this.routeQuery.offset = this.offset;
                this.routeQuery.limit = this.limit;
                api.instance.get(query + '&offset=' + this.offset + "&limit=" + this.limit)
                    .then((res) => {
                      currentObj.data = res.data.results;
                      this.getProfileImage(currentObj.data);
                      currentObj.updateUrl();
                      currentObj.tableIsLoading = false;
                    })
                .catch(err => {
                  currentObj.tableIsLoading = false;
                  console.log(err)
                });
            },
          getProfileImage(users) {
            for (let i = 0; i < users.length; i++) {
              this.$set(users[i], 'imageSrc',
                  process.env.VUE_APP_SERVER_ADD + "/profiles/" + users[i].profile_id + "/image")
            }
          },
          searchNames: function (query) {
            if (this.searchBy === 'email') {
              this.routeQuery = {email: this.searchQuery.trim()};
              query += '?email=' + this.searchQuery.trim();
            } else if (this.searchBy === 'nickname') {
              this.routeQuery = {nickname: this.searchQuery.trim()};
              query += '?nickname=' + this.searchQuery.trim();
            } else {
                    this.routeQuery = {fullname: this.searchQuery.trim()};
                    query += '?fullname=' + this.searchQuery.trim();
                }
                return query;
            },
            updateUrl: function () {
                this.$router.push({name: "Users", query: this.routeQuery});
            },
            getUsersFromNavBar: function () {
                let navBarQuery = this.$route.query.fullname;
                if (this.searchQuery.trim() === '' && navBarQuery) {
                    this.searchQuery = navBarQuery;
                    return true;
                }
                return false;
            },
            calibratePagination: function () {
                this.offset = (this.currentPage - 1) * this.limit;
                this.routeQuery.offset = this.offset;
                this.routeQuery.limit = this.limit;
                this.routeQuery.page = this.currentPage;
                console.log(this.routeQuery);
                this.updateUrl();
                console.log(this.$route.query);
            },
            searchUser: function () {
                this.routeQuery = {};
                this.tableIsLoading = true;
                // this.currentPage = 1;
                if (this.searchQuery.trim() === '' && this.activityTypesForm.selectedOptions.length === 0) return;
                let query = '/profiles/count';
                if (this.searchBy !== 'email' && this.searchQuery.trim() !== "" && this.activityTypesForm.selectedOptions.length !== 0) {
                    query = this.searchNames(query);
                    query += "&activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                }else if (this.searchBy === 'email'){
                    this.getUsers();
                    return;
                }else if(this.searchQuery.trim() !== ''){
                    query = this.searchNames(query)
                }
                else {
                    query += "?activity=" + this.activityTypesForm.selectedOptions.join(' ');
                    query += "&method=" + this.activityTypesForm.method;
                }
                const currentObj = this;
                api.instance.get(query)
                    .then((res) => {
                        currentObj.count = res.data;
                        console.log("count" + res.data);
                        currentObj.getUsers()
                    })
                    .catch(err => console.log(err));

            },
            getActivities: function () {
                let currentObj = this;
                api.instance.get('/profiles/activity-types')
                    .then(function (response) {
                        console.log(response.data);
                        currentObj.activityTypesForm.options = response.data;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                    });
            },
            itemRowClicked: function (record) {
                this.$router.push('/profiles/' + record.profile_id);
            },
            populatePage: function() {
                let fromNavBar = this.getUsersFromNavBar();
                if (this.$route.query.fullname) {
                    this.searchQuery = this.$route.query.fullname;
                    this.searchBy = "fullName";
                } else if (this.$route.query.email) {
                    this.searchQuery = this.$route.query.email;
                    this.searchBy = "email";
                } else if (this.$route.query.nickname) {
                    this.searchQuery = this.$route.query.nickname;
                    this.searchBy = "nickname";
                }

                if (this.$route.query.method) {
                    this.activityTypesForm.method = this.$route.query.method;
                } else {
                    this.activityTypesForm.method = 'AND';
                }

                if (this.$route.query.activity) {
                    this.activityTypesForm.selectedOptions = this.$route.query.activity.split(' ');
                } else {
                    this.activityTypesForm.selectedOptions = [];
                }

                // if offset is null, the user is entering the search page for the first time, which means
                // there wouldn't be a need to repopulate the page (except when query is from the nav bar)
                if (this.$route.query.offset || this.$route.query.page) {
                    this.offset = this.$route.query.offset;
                    this.limit = this.$route.query.limit;
                    this.currentPage = this.$route.query.page;
                    console.log(this.currentPage);
                    this.searchUser();
                } else if (fromNavBar) {
                    this.searchUser();
                } else {
                    this.currentPage = 1;
                }
            },
            isSearchButtonDisabled: function () {
                if (this.activityTypesForm.selectedOptions.length === 0 && this.searchQuery.trim() ==='')
                {
                    return true;
                } else if (this.searchBy === "email" && this.searchQuery.trim() === '') {
                    return true;
                }
                return false;
            }
        },
        watch: {
            '$route': function () {
                this.populatePage();
            },
            deep: true,
            immediate: true
        },
        mounted() {
            this.getActivities();
            this.populatePage();
        }
    };
</script>


<style scoped>

</style>