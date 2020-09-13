<template>
  <div>
    <map-pane :path-overlay="true" :can-hide="false" @onMapClick="mapClicked" ref="map"></map-pane>
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
      const marker = this.$refs.map.getLatestMarker()
      if (this.autoRoute == "true" && marker != null) {
        this.getRoutePoints(event)
      } else {
        this.$refs.map.routePoints.push([event.latlng.lat, event.latlng.lng])
      }
      this.$refs.map.createMarker(this.count, 3, event.latlng.lat, event.latlng.lng)
      this.$refs.map.updateStartFinishMarkers()

      if (this.$refs.map.routePoints.length == 0) {
        this.canChangeSelection = true
      } else {
        this.canChangeSelection = false
      }
      currObj.count += 1

    },
    getRoutePoints(event){
      const currObj = this
      let markerCoords = this.$refs.map.getAllMarkersCoords()
      if (event != false){
        markerCoords.push([event.latlng.lng, event.latlng.lat])
      }
      axios.post("https://api.openrouteservice.org/v2/directions/driving-car/geojson",
          {
            coordinates: markerCoords,
          },
          {headers: {Authorization: "5b3ce3597851110001cf6248183abbef295f42049b13e7a011f98247"}})
      .then(function (res) {
        let newRoutePoints = []
        for (let iii of res.data.features[0].geometry.coordinates) {
          newRoutePoints.push([iii[1], iii[0]])
        }
        currObj.$refs.map.setRoutePoints(newRoutePoints)
      })
      .catch(function () {
        currObj.$refs.map.markers.pop();
        currObj.$refs.map.updateStartFinishMarkers()
      })
    },
    resetMarkerAndPoint() {
      this.$refs.map.setMarkers([])
      this.$refs.map.setRoutePoints([])
      this.canChangeSelection = true;
    },
    prevPoint() {
      if (this.$refs.map.markers.length > 1) {
        this.$refs.map.markers.pop();
        this.$refs.map.routePoints.pop();
        if(this.autoRoute == "true") {
          if (this.$refs.map.markers.length == 1) {
            this.$refs.map.setRoutePoints([])
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