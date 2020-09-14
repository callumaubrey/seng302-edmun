<template>
  <div>
    <div>
      <b-form-input v-model="locationText" @keyup.native="doAutocomplete(locationText)"
                    autocomplete="off" placeholder="Search address"
                    :state="validLocation"/>
      <b-form-invalid-feedback id="input-live-feedback">
        The selected location is unknown
      </b-form-invalid-feedback>
      <em v-if="loadingLocations"
          class="autocomplete-loading-icon text-primary fas fa-circle-notch fa-spin"/>
      <b-form-text>Click on the map or enter into input to set location</b-form-text>
    </div>
    <div v-for="i in locations" :key="i.osm_id">
      <b-input v-on:click="setSelectedLocation(i)" type="button" :value=i.display_name
               style="cursor: pointer"/>
    </div>
  </div>
</template>

<script>
  import axios from "axios";

  const LocationAutocomplete = {
    name: "LocationAutocomplete",
    props: {
      givenLocation: String,
      priorityGeoLocation: { // Latitude and Longitude of the user currently to narrow down the search
        type: Object,
        default: null
      },
    },
    data() {
      return {
        locationText: null,
        timeout: null,
        locations: [],
        selectedLocation: null,
        loadingLocations: false,
        validLocation: null
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

      /**
       * Extracts useful display information from an OSM feature
       * @param feature
       */
      parseOSMFeature: function (feature) {
        let obj = feature.properties;
        let geo = feature.geometry.coordinates;

        let displayName;

        let properties = [];
        switch (obj.osm_value) {
          case "house":
            properties.push(obj.housenumber + " " + obj.street);
            if (obj.city) {
              properties.push(obj.city);
            }
            if (obj.state) {
              properties.push(obj.state);
            }
            properties.push(obj.country);
            displayName = properties.join(", ");
            break;
          case "residential":
            properties.push(obj.name);
            if (obj.city) {
              properties.push(obj.city);
            }
            if (obj.state) {
              properties.push(obj.state);
            }
            properties.push(obj.country);
            displayName = properties.join(", ");
            break;
          case "country":
            displayName = obj.country;
            break;
          default:
            properties.push(obj.name);
            if (obj.city) {
              properties.push(obj.city)
            }
            if (obj.state) {
              properties.push(obj.state);
            }
            properties.push(obj.country);
            displayName = properties.join(", ");
        }
        return {
          "lng": geo[0],
          "lat": geo[1],
          "display_name": displayName,
          "osm_id": obj.osm_id
        };
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

          let geo_priority_query = "";
          if (_this.priorityGeoLocation !== null) {
            geo_priority_query += "&lat=" + _this.priorityGeoLocation.lat;
            geo_priority_query += "&lon=" + _this.priorityGeoLocation.lng;
          }

          let locationData = axios.create({
            baseURL: "https://photon.komoot.de/api/?q=" + locationText + geo_priority_query
                + "&limit=10",
            timeout: 5000,
            withCredentials: false,
          });

          _this.loadingLocations = true;
          let data = await (locationData.get());
          _this.loadingLocations = false;

          let fixedData = JSON.parse('{"data":[]}');
          for (let i = 0; i < data.data.features.length; i++) {
            let feature_data = _this.parseOSMFeature(data.data.features[i]);
            if (feature_data.display_name) {
              fixedData['data'].push(feature_data)
            }
          }

          if (fixedData.data.length > 0) {
            _this.locations = fixedData.data;
          }
        }, 1000);
      },
      setSelectedLocation: function (location) {
        this.locationText = location.display_name;
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
      },

      emitLocationToParent: function (value) {
        this.$emit("emitLocation", value);
      },

      validateLocation: function() {
        return this.validLocation == null;
      },

      setLocationTextByCoords: async function (lat, lng) {
        let coordsDataAPI = axios.create({
          baseURL: "https://photon.komoot.de/reverse?lon=" + lng + "&lat=" + lat + "&limit=1",
          timeout: 5000,
          withCredentials: false,
        });

        this.loadingLocations = true;
        await coordsDataAPI.get().then((res) => {
          // If relevant feature is found
          if (res.data.features) {
            if (res.data.features.length > 0) {
              let feature_data = this.parseOSMFeature(res.data.features[0]);
              this.locationText = feature_data.display_name;
              this.locations = [];
              this.validLocation = null;
            } else {
              this.validLocation = false;
              this.clearLocation();
            }
          } else {
            this.$bvToast.toast('An error has occurred, please try again.', {
              variant: "danger",
              solid: true
            })
          }
          this.loadingLocations = false;
        }).catch(() => {
          this.validLocation = false;
          this.loadingLocations = false;
          this.clearLocation();
        })
      },

      clearLocation: function () {
        this.locationText = '';
        this.locations = [];
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