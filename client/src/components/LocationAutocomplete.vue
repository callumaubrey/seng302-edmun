<template>
  <div>
    <div>
      <b-form-input v-model="locationText" v-on:keyup="doAutocomplete(locationText)"
                    autocomplete="off" placeholder="Search address"
                    :state="validLocation"/>
      <b-form-invalid-feedback id="input-live-feedback">
        {{invalidLocationErrorMessage}}
      </b-form-invalid-feedback>
      <em v-if="loadingLocations"
          class="autocomplete-loading-icon text-primary fas fa-circle-notch fa-spin"/>
      <b-form-text>Click on the map or enter into input to set location</b-form-text>
    </div>
    <div v-for="i in locations" :key="i.place_id">
      <b-input v-on:click="setSelectedLocation(i)" type="button" :value=i.name
               style="cursor: pointer"/>
    </div>
  </div>
</template>

<script>
  import api from '@/Api'

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
        validLocation: null,
        invalidLocationErrorMessage: "The selected location is unknown",
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
       * @param feature an OSM feature object
       * @return an object with longitude, latitude, the location name and the osm id
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
        if (this.timer) {
          clearTimeout(this.timer);
          this.timer = null;
        }
        this.timer = setTimeout(() => {
          api.getLocationAutocompleteByName(locationText).then(
              (response) => {
                this.locations = response.data.results
              }
          ).catch(() => {
            this.$bvToast.toast('An error has occurred, please try again.', {
              variant: "danger",
              solid: true
            })
          })
        }, 200)

      },
      /**
       * Set location text in input field based on unique place id by calling Google API from backend
       **/
      setSelectedLocation: async function (location) {
        this.locationText = location.name
        await api.getGeocodePlaceId(location.place_id).then((response) => {
          this.selectedLocation = response.data
        }).catch(() => {
          this.$bvToast.toast('An error has occurred, please try again.', {
            variant: "danger",
            solid: true
          })
        })
        this.locations = []
        this.emitLocationToParent(this.selectedLocation);
      },

      emitLocationToParent: function (value) {
        this.$emit("emitLocation", value);
      },

      validateLocation: function () {
        return this.validLocation == null;
      },
      /**
       * Uses the Google api from backend to search for the name of the location based on the selected longitude
       * and latitude.
       * If a name for the location is found, it will set the input field with the name of the
       * location which is parsed from the data returned from the photon api.
       * @param lat the longitude value of the location
       * @param lng the latitude value of the location
       */
      setLocationTextByCoords: async function (lat, lng) {
        await api.getLocationAutocompleteByLatLon(lat, lng).then((response) => {
          this.locationText = response.data.results[0];
          this.locations = [];
          this.validLocation = null;
        }).catch((err) => {
              console.log(err.response.status)
              if (err.response.status === 400) {
                this.invalidLocationErrorMessage = "The selected location is unknown"
                this.validLocation = false;
                this.clearLocation();
              } else {
                this.$bvToast.toast('An error has occurred, please try again.', {
                  variant: "danger",
                  solid: true
                })
              }

            }
        )
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