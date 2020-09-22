<template>
  <div>
    <map-pane :path-overlay="true" :can-hide="false" @onMapClick="mapClicked" ref="map"></map-pane>
  </div>
</template>

<script>
import MapPane from "./MapPane";
import axios from 'axios'
import api from '@/Api'

export default {
  name: "ModifyPathMapPane",

  components: {
    MapPane
  },

  data() {
    return {
      autoRoute: false,
      canChangeSelection: true,
      toPass: []
    }
  },

  methods: {
    mapClicked(event) {
      const marker = this.$refs.map.getLatestMarker()
      const coordinates = [event.latlng.lat, event.latlng.lng]
      if (this.autoRoute && marker) {
        this.getRoutePoints(coordinates)
      } else {
        this.$refs.map.routePoints.push(coordinates)
      }
      let id = this.$refs.map.markers.length
      this.$refs.map.createMarker(id, 3, event.latlng.lat, event.latlng.lng,
          "", null, true)
      this.$refs.map.updateStartFinishMarkers()

      if (this.$refs.map.routePoints.length == 0) {
        this.canChangeSelection = true
      } else {
        this.canChangeSelection = false
      }
      this.$emit('pathEdited')
    },

    clickOnMarker(marker) {
      const latitude = marker[0]
      const longitude = marker[1]
      if (this.autoRoute && marker != null) {
        this.getRoutePoints(marker)
      } else {
        this.$refs.map.routePoints.push(marker)
      }
      let id = this.$refs.map.markers.length
      this.$refs.map.createMarker(id, 1, latitude, longitude, "", null)
      this.$refs.map.updateStartFinishMarkers()
      this.$emit('pathEdited')
    },

    handleDragEvent(index, newCoords) {
      if (!this.autoRoute) {
        this.$refs.map.editSingleRoutePoint(newCoords, index)
        this.$refs.map.routePoints.push(newCoords)
        this.$refs.map.routePoints.pop()
      } else {
        this.getRoutePoints([])
      }
      this.$refs.map.updateStartFinishMarkers()
      this.$emit('pathEdited')
    },

    getRoutePoints(coordinates) {
      const currObj = this
      let apiInput = this.$refs.map.getAllMarkersCoords()
      if (coordinates.length != 0){
        //For some reason api takes [lng,lat] points rather than [lat,lng] points, hence reverse()
        apiInput.push([coordinates[1], coordinates[0]])
      }
      axios.post("https://api.openrouteservice.org/v2/directions/foot-hiking/geojson",
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
      this.$refs.map.markers = []
      this.$refs.map.setRoutePoints([])
      this.canChangeSelection = true;
      this.$emit('pathEdited')
    },

    prevPoint() {
      if (this.$refs.map.markers.length > 1) {
        this.$refs.map.markers.pop();
        this.$refs.map.routePoints.pop();
        if(this.autoRoute) {
          if (this.$refs.map.markers.length == 1) {
            this.$refs.map.setRoutePoints([])
          }else {
            this.getRoutePoints([])
          }
        }
        this.$refs.map.updateStartFinishMarkers()
      }
      this.$emit('pathEdited')
    },

    /**
     * Load activity path into editor
     * @param profileId
     * @param activityId
     * @param draggable
     */
    getPathFromActivity(profileId, activityId, draggable=false) {
      api.getActivityPath(profileId, activityId).then((res) => {
        this.autoRoute = res.data.type === "DEFINED";
        this.canChangeSelection = false;

        this.$refs.map.setPath(res.data, true, false, draggable);
      }).catch((err) => {
        console.error(err);
      });
    },

    /**
     * Generates a path object using editor data
     **/
    getPathObject() {
      if(this.$refs.map.routePoints.length === 0) {
        return null;
      }

      let pathObj = {
        pathType: this.autoRoute ? "DEFINED" : "STRAIGHT",
        coordinates: []
      };

      // Format locations
      for(const keypoint of this.$refs.map.markers) {
        pathObj.coordinates.push({
          latitude: keypoint.position[0],
          longitude: keypoint.position[1]
        });
      }
      return pathObj;
    },

    getUpdatedPathObject() {
      if(this.$refs.map.routePoints.length === 0) {
        return {}
      }

      let pathObj = {
        id: 0,
        locations: [],
        type: this.autoRoute ? "DEFINED" : "STRAIGHT"
      };

      // Format locations
      for(const keypoint of this.$refs.map.markers) {
        pathObj.locations.push({
          latitude: keypoint.position[0],
          longitude: keypoint.position[1]
        });
        pathObj.id = keypoint.id
      }
      return pathObj;
    },
    setMapCenterFromIndex(index) {
      let coordinates = this.$refs.map.markers[index].position
      this.$refs.map.setMapCenter(coordinates[0], coordinates[1])
    },

    /**
     * upload activity path in editor
     * @param profileId
     * @param activityId
     */
    updatePathInActivity(profileId, activityId) {
      let pathObj = this.getPathObject();
      return api.updateActivityPath(profileId, activityId, pathObj);
    }
  },
  mounted() {
    // this.$refs.pathInfo.data = this.$refs.map.markers
    if(this.$refs.map.markers == null) {
      this.toPass = []
    } else {
      this.toPass = this.$refs.map.markers
    }
  }
}
</script>