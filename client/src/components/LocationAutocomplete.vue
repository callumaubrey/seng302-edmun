<template>
  <div>
    <b-input v-model="locationText" @keyup.native="doAutocomplete(locationText)" autocomplete="off"></b-input>
    <div v-for="i in locations" :key="i.osm_id">
      <b-input v-on:click="setSelectedLocation(i)" type="button" :value=i.display_name></b-input>
    </div>
  </div>
</template>

<script>
  import axios from "axios";

  const LocationAutocomplete = {
    name: "LocationAutocomplete",
    data() {
      return {
        locationText: null,
        timeout: null,
        locations: [],
        selectedLocation: null
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

          _this.locations = [];

          let locationData = axios.create({
            baseURL: "https://photon.komoot.de/api/?q=" + locationText,
            timeout: 2000,
            withCredentials: false,
          });

          let data = await (locationData.get());

          let fixedData = JSON.parse('{"data":[]}');
          for (let i = 0; i < data.data.features.length; i++) {
            let obj = data.data.features[i].properties;
            let geo = data.data.features[i].geometry.coordinates;
            let displayName;
            if (obj.osm_value == "house") {
              displayName = obj.housenumber + " " + obj.street + ", " + obj.state + ", " + obj.country;
            } else if (obj.osm_value == "suburb" || obj.osm_value == "city" || obj.osm_value == "residential") {
              displayName = obj.name + ", " + obj.state + ", " + obj.country;
            } else if (obj.osm_value == "country") {
              displayName = obj.country;
            }
            if (displayName) {
              fixedData['data'].push({"lng": geo[0], "lat": geo[1], "display_name": displayName, "osm_id": obj.osm_id});
            }
          }

          if (fixedData.data.length > 0) {
            _this.locations = fixedData.data;
          }
        }, 1000);
      },
      setSelectedLocation: function (location) {
        this.locationText = location.display_name;

        if (location !== null) {
          let data = {
            osm_id: null,
            lat: null,
            lng: null
          };
          if (location.osm_id) {
            data.osm_id = location.osm_id;
          }
          if (location.lat) {
            data.lat = location.lat;
          }
          if (location.lng) {
            data.lng = location.lng;
          }
          this.selectedLocation = data;
        }
      },
    }
  };

  export default LocationAutocomplete;
</script>