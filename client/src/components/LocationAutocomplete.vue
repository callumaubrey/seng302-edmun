<template>
  <div>
    <b-input v-model="selectedLocation" @keyup.native="doAutocomplete(selectedLocation)" autocomplete="off"></b-input>
    <p v-for="location in locations" v-bind:key="location">
      {{ location }}
    </p>
  </div>
</template>

<script>
  import axios from "axios";

  const LocationAutocomplete = {
    name: "LocationAutocomplete",
    data() {
      return {
        selectedLocation: null,
        timeout: null,
        locations: []
      }
    },
    methods: {
      doAutocomplete: async function (locationText) {
        clearTimeout(this.timeout);

        let _this = this;
        this.timeout = setTimeout(async function () {
          if (locationText == '') {
            return;
          }

          let locationData = axios.create({
            baseURL: "https://photon.komoot.de/api/?q=" + locationText,
            timeout: 2000,
            withCredentials: false,
          });

          let data = await (locationData.get());

          for (let i = 0; i < data.data.features.length; i++) {
            let feature = data.data.features[i].properties;
            _this.locations.push(feature.housenumber + " " + feature.street + " " + feature.state + " " + feature.country);
          }
        }, 1000);
      }
    }
  };

  export default LocationAutocomplete;
</script>