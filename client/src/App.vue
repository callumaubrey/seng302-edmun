<template>
  <div id="app">
    <Students />
    <footer class="info">
      <h4>Seng302 Example</h4>
    </footer>
    <a href=registration.html>Registation</a> 
  </div>
</template>

<script>
  import Students from './components/Students'
  // app Vue instance
  const app = {
    name: 'app',
    components: {
      Students
    },
    // app initial state
    data: () => {
      return {
      }
    }
  }

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>