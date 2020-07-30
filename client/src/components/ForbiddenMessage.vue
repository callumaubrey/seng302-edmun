<template>
  <b-container>
    <b-row align-h="center">
      <b-img :src="require('@/assets/goat.jpg')" width="400px"></b-img>
    </b-row>

    <b-row align-h="center" style="margin-top:20px;">
      <h1>You are not authorized to view this page.</h1>
    </b-row>
  </b-container>
</template>

<script>
  const ForbiddenMessage = {
    name: "ForbiddenMessage"
  }

  export default ForbiddenMessage;
</script>