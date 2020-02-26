<template>
  <div id="app">
    <div class="container">
    <h1> Here is your account </h1>
    <hr> 
      <div class ="row">
        <div class="col-33">
          <label for="fname"> First name</label>
          <input type="text" id="fname" name="firstname" placeholder="John" required>
        </div>
        <div class="col-33">
          <label for="mname"> Middle name</label>
          <input type="text" id="mname" name="middlename" placeholder="Middle">
        </div>
        <div class="col-33">
          <label for="lname"> Last name</label>
          <input type="text" id="lname" name="lastname" placeholder="Doe" required>
        </div>
      </div>

      <div class="row">
        <div class="col-100">
          <label for="nname"> Nickname</label>
        </div>
      </div>
      <div class="row">
        <div class="col-100">
          <input type="text" id="nname" name="nickname" placeholder="Johnny">
        </div>
      </div>


      <div class="row">
        <div class="col-100">
          <label for="email">Email address</label>
        </div>
      </div>
      <div class="row">
        <div class="col-100">
          <input type="text" id="email" name="email" placeholder="john@example.com" required>
        </div>
      </div>

      <div class="row">
        <div class="col-50">
          <label for="psw"><b>Password</b></label>
          <input type="password" id="password" name="password" placeholder="john@example.com" required>
        </div>
        <div class="col-50">
          <label for="psw-repeat"><b>Repeat Password</b></label>
          <input type="password" id="rpassword" placeholder="Repeat Password" name="psw-repeat" required>
        </div>
      </div>

      <div class="row">
        <div class="col-100">
          <label for="gender">Gender</label>
        </div>
      </div>

      <div class="row">
        <div class="col-33">
          <label for="male"><input type="radio" id="male" name="gender" value="male">Female</label><br>
        </div>
        <div class="col-33">
          <label for="female"><input type="radio" id="female" name="gender" value="female">Male</label><br>
        </div>
          <div class="col-33">
            <label for="other"><input type="radio" id="other" name="gender" value="other">Non-binary</label>
          </div>    
        </div>
    </div>
  </div>
</template>

<script>
//   import Students from './components/Students'
  // app Vue instance
  
</script>

<style scoped>
  [v-cloak] { display: none; }

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

.col-100{
  -ms-flex: 100%;
  flex: 100%;
}

.col-33,
.col-50,
.col-100{
  padding: 0 16px;
}

input[type=text] {
  width: 100%;
  margin-bottom: 20px;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 3px;
}
input[type=password] {
  width: 100%;
  margin-bottom: 20px;
  padding: 12px 12px 12px 12px;
  border: 1px solid #ccc;
  border-radius: 3px;
}

input[type=radio] {
  padding: 0;
  margin: 0;
  border: 0;

}

label {
  margin-bottom: 10px;
  display: block;
}

.container {
  background-color: #f2f2f2;
  padding: 5px 20px 15px 20px;
  border: 1px solid lightgrey;
  border-radius: 3px;
}
</style>