<template>
  <div id="app">
    <div>
      <NavBar></NavBar>
    </div>
    <div class="container">
      <h1> Login </h1>
      <hr>
      <form method="post">

        <div class="row">
          <div class="col-100">
            <label class="field" for="email">Email address</label>
          </div>
        </div>
        <div class="row">
          <div class="col-100">
            <input id="email" name="email" placeholder="john@example.com" required type="email">
          </div>
        </div>

        <div class="row">
          <div class="col-50">
            <label class="field" for="psw">Password</label>
            <input id="psw" name="password" placeholder="Password" required type="password">
          </div>
        </div>

        <div class="row">
          <div class="col-100">
            <input class="sign-up-btn" type="submit" v-on:click="validateFields" value="Login">
          </div>
        </div>

        <a href="/registration">Click here to register</a>
      </form>
    </div>
  </div>

</template>

<script>
  import NavBar from '@/components/NavBar.vue';

  export default {
    components: {
      NavBar
    }
  }

</script>

<style scoped>
  [v-cloak] {
    display: none;
  }

  .row {
    display: -ms-flexbox; /* IE10 */
    display: flex;
  }

  .col-50 {
    -ms-flex: 50%; /* IE10 */
    flex: 50%;
  }

  .col-33 {
    -ms-flex: 33%; /* IE10 */
    flex: 33%;
  }

  .col-100 {
    -ms-flex: 100%;
    flex: 100%;
  }

  .col-33,
  .col-50,
  .col-100 {
    padding: 0 16px;
  }

  input[type=text] {
    width: 100%;
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=password] {
    width: 100%;
    padding: 12px 12px 12px 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=email] {
    width: 100%;
    padding: 12px 12px 12px 12px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }

  input[type=radio] {
    padding: 0;
    margin: 0 10px 0 0;
    border: 0;
  }

  label {
    display: block;
  }

  .field {
    font-weight: bold;
    margin-top: 10px;
    margin-bottom: 10px;
  }

  .container {
    background-color: #f2f2f2;
    padding: 5px 40px 15px 20px;
    border: 1px solid lightgrey;
    border-radius: 3px;
  }

  .sign-up-btn {
    background-color: #000000; /* Green */
    border: none;
    color: white;
    padding: 15px 32px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
  }
</style>
