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
      const coordinates = [event.latlng.lat, event.latlng.lng]
      if (this.autoRoute == "true" && marker) {
        this.getRoutePoints(coordinates)
      } else {
        this.$refs.map.routePoints.push(coordinates)
      }
      this.$refs.map.createMarker(this.count, 3, event.latlng.lat, event.latlng.lng,
          "", null, true)
      this.$refs.map.updateStartFinishMarkers()

      if (this.$refs.map.routePoints.length == 0) {
        this.canChangeSelection = true
      } else {
        this.canChangeSelection = false
      }
      currObj.count += 1
    },

    clickOnMarker(marker) {
      this.count += 1
      const latitude = marker[0]
      const longitude = marker[1]
      if (this.autoRoute == "true" && marker != null) {
        this.getRoutePoints(marker)
      } else {
        this.$refs.map.routePoints.push(marker)
      }
      this.$refs.map.createMarker(this.count, 1, latitude, longitude, "", null)
      this.$refs.map.updateStartFinishMarkers()
    },

    handleDragEvent(index, newCoords) {
      if (this.autoRoute != "true") {
        this.$refs.map.editSingleRoutePoint(newCoords, index)
        this.$refs.map.routePoints.push(newCoords)
        this.$refs.map.routePoints.pop()
      } else {
        this.getRoutePoints([])
      }
      this.$refs.map.updateStartFinishMarkers()
    },

    getRoutePoints(coordinates){
      const currObj = this
      let apiInput = this.$refs.map.getAllMarkersCoords()
      // console.log(apiInput)
      if (coordinates.length != 0){
        //For some reason api takes [lng,lat] points rather than [lat,lng] points, hence reverse()
        apiInput.push([coordinates[1], coordinates[0]])
      }
      axios.post("https://api.openrouteservice.org/v2/directions/driving-car/geojson",
          {
            coordinates: apiInput,
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
        if (currObj.$refs.map.getAllMarkersCoords().length == 2){
          currObj.resetMarkerAndPoint()
        }
        else if (currObj.$refs.map.savedMarkers != null) {
          currObj.$refs.map.revertMarkers()
        }
        else {
          currObj.$refs.map.markers.pop();
          currObj.$refs.map.updateStartFinishMarkers()
        }
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
            this.getRoutePoints([])
          }
        }
        this.$refs.map.updateStartFinishMarkers()
      }
    }
  },
}
</script>