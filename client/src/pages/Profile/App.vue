<template>
    <div id="app">
        <NavBar isLoggedIn=true></NavBar>

        <div class="container">
            <b-container fluid>
                <b-row>
                    <b-col>
                        <h4>Callum Aubrey</h4>
                        <hr>
                    </b-col>
                </b-row>
            </b-container>
        </div>
    </div>
</template>



<script>
    // import api from '../Api';
    import NavBar from "@/components/NavBar.vue"

    export default {
        components: {
            NavBar
        }
    }
</script>