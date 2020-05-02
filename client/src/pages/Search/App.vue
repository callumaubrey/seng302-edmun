<template>
    <div id="app">
        <NavBar isLoggedIn=true v-bind:userName="userName"></NavBar>

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
                        <b-form-radio v-model="searchBy" class="searchByRadio">Email</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio">Full Name</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio">First Name</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio">Nickname</b-form-radio>
                        <b-form-radio v-model="searchBy" class="searchByRadio">Last Name</b-form-radio>
                        <b-button variant="light">Search</b-button>
                    </b-form>
                </b-col>
            </b-row>

            <b-row>
                <b-col style="padding:0;">
                    <b-table striped hover :items="items"></b-table>
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
                searchBy: null,
                items: [
                    { age: 40, first_name: 'Dickerson', last_name: 'Macdonald' },
                    { age: 21, first_name: 'Larsen', last_name: 'Shaw' },
                    { age: 89, first_name: 'Geneva', last_name: 'Wilson' },
                    { age: 38, first_name: 'Jami', last_name: 'Carney' }
                ]
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
            }
        },
        mounted() {
            this.getUserId();
            this.getUserName();
        }
    };

    export default App;
</script>

<style>
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