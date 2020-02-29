<template>
  <div id="app">
    <User/>
    <footer class="info">
      <h4>Seng302 Example</h4>
    </footer>
    <a href="/login">Login</a>
    <a href="/registration">Registation</a>
    <a href="/profile">Profile</a>
  </div>
</template>

<script>
  import User from './pages/Login/App'
  // app Vue instance
  const app = {
    name: 'app',
    components: {
      User
    },
    // app initial state
    data: () => {
      return {}
    }
  };

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>
