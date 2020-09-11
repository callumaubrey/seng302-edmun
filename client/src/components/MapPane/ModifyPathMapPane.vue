<template>
  <div>
    <b-form-group label="Using sub-components:">
      <b-form-checkbox-group id="checkbox-group-2" v-model="autoRoute" name="flavour-2"
                             v-if="canChangeSelection">
        <b-form-checkbox value="false">Selection</b-form-checkbox>
        <b-form-checkbox value="true">Auto route</b-form-checkbox>
      </b-form-checkbox-group>
    </b-form-group>
    <b-button @click="resetMarkerAndPoint()">Reset</b-button>
    <div>
      <b-button @click="prevPoint()">Back</b-button>
    </div>
    <map-pane @onMapClick="mapClicked" ref="map"></map-pane>
  </div>
</template>

<script>
import MapPane from "./MapPane";
import axios from 'axios'

export default {
  name: "RecordActivityResultModal",
  components: {
    MapPane
  },
  props: ['profileId', 'activityId', 'loggedInId'],
  data() {
    return {
      autoRoute: "false",
      canChangeSelection: true,
      count: 0
    }
  },
  methods: {
    mapClicked(event) {
      const currObj = this
      const marker = this.$refs.map.markers[this.$refs.map.markers.length - 1]
      if (this.autoRoute == "true" && marker != null) {
        this.getRoutePoints(event)
      } else {
        this.$refs.map.routePoints.push([event.latlng.lat, event.latlng.lng])
        this.$refs.map.createMarker(this.count, 0, event.latlng.lat, event.latlng.lng, "", "")
      }

      if (this.$refs.map.routePoints.length == 0) {
        this.canChangeSelection = true
      } else {
        this.canChangeSelection = false
      }
      currObj.count += 1

    },
    getRoutePoints(event){
      const currObj = this
      let listOfWayPoints = []
      for (var waypoint of this.$refs.map.markers) {
        listOfWayPoints.push([waypoint.position[1], waypoint.position[0]])
      }
      if (event != false){
        listOfWayPoints.push([event.latlng.lng, event.latlng.lat])
      }
      axios.post("https://api.openrouteservice.org/v2/directions/driving-car/geojson",
          {
            coordinates: listOfWayPoints,
          },
          {headers: {Authorization: "5b3ce3597851110001cf6248183abbef295f42049b13e7a011f98247"}})
      .then(function (res) {
        if (event != false) {
          currObj.$refs.map.createMarker(currObj.count, 0, event.latlng.lat, event.latlng.lng, "",
              "")
        }
        currObj.$refs.map.routePoints = []
        for (var iii of res.data.features[0].geometry.coordinates) {
          currObj.$refs.map.routePoints.push([iii[1], iii[0]])
        }
      })
    },
    resetMarkerAndPoint() {
      this.$refs.map.markers = [];
      this.$refs.map.routePoints = [];
      this.canChangeSelection = true;
    },
    prevPoint() {
      if (this.$refs.map.markers.length > 1) {
        this.$refs.map.markers.pop();
        this.$refs.map.routePoints.pop();
        if(this.autoRoute == "true") {
          if (this.$refs.map.markers.length == 1) {
            this.$refs.map.routePoints = []
          }else {
            this.getRoutePoints(false)
          }
        }
      }
    }
  },
  mounted() {
    this.$refs.map.tooltip = false
  }
}
</script>