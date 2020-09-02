<template>
  <div>
    <div>
      <b-input v-model="locationText" @keyup.native="doAutocomplete(locationText)" autocomplete="off" placeholder="Search address"></b-input>
        <i v-if="loadingLocations" class="autocomplete-loading-icon text-primary fas fa-circle-notch fa-spin"></i>
    </div>
    <div v-for="i in locations" :key="i.osm_id">
      <b-input v-on:click="setSelectedLocation(i)" type="button" :value=i.display_name style="cursor: pointer"></b-input>
    </div>
  </div>
</template>

<script>
  import axios from "axios";

  const LocationAutocomplete = {
    name: "LocationAutocomplete",
    props: {
      givenLocation: String
    },
    data() {
      return {
        locationText: null,
        timeout: null,
        locations: [],
        selectedLocation: null,
        loadingLocations: false,
      }
    },
    mounted() {
      this.checkProp();
    },
    methods: {
      checkProp: function () {
        if (this.givenLocation != null) {
          this.locationText = this.givenLocation;
        }
      },
      doAutocomplete: async function (locationText) {
        clearTimeout(this.timeout);

        if (locationText === '') {
          this.locations = [];
          return;
        }

        let _this = this;
        this.timeout = setTimeout(async function () {

          _this.locations = [];

          let locationData = axios.create({
            baseURL: "https://photon.komoot.de/api/?q=" + locationText + "&limit=10",
            timeout: 2000,
            withCredentials: false,
          });

          _this.loadingLocations = true;
          let data = await (locationData.get());
          _this.loadingLocations = false;

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
          this.locations = [];
          this.emitLocationToParent(data);
        }
      },
      emitLocationToParent: function (value) {
        this.$emit("emitLocation", value);
      }
    }
  };

  export default LocationAutocomplete;
</script>

<style>
  .autocomplete-loading-icon {
    position: absolute;
    top: 0.22em;
    right: 0.8em;
    font-size: 1.6em;
  }
</style>