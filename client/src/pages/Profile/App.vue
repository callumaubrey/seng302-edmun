<template>
  <div id="app">
    <div class="container">
    <h1> Here is your account!!!! </h1>
    <hr> 
      <div class ="row">
        <div class="col-25">
          <label for="fname"> First name</label>
        </div>
        <div class="col-25">
          <input type="text" id="fname" name="firstname" placeholder="John" required>
        </div>
        <div class="col-25">
          <label for="dob">Date of Birth</label>
        </div>
        <div class="col-25">
          <input type="date" id="dob" required>
        </div>
      </div>
      <div class="row">
        <div class="col-25">
          <label for="mname"> Middle</label>
        </div>
        <div class="col-25">
          <input type="text" id="mname" name="middlename" placeholder="Middle">
        </div>
        <div class="col-25">
          <label>Gender</label>
        </div>
        <div class="col-25">
          <div class="col-25">
            <label for="male"><input type="radio" id="male" name="gender" value="male">F</label><br>
          </div>
          <div class="col-25">
            <label for="female"><input type="radio" id="female" name="gender" value="female">M</label><br>
          </div>
          <div class="col-25">
            <label for="other"><input type="radio" id="other" name="gender" value="other">N/A</label>
          </div>
        </div>
      </div>
      <div class="col-33">
        <label for="lname"> Last name</label>
        <input type="text" id="lname" name="lastname" placeholder="Doe" required>
      </div>

      <div class="row">
        <div class="col-50">
          <label for="nName"> Nickname</label>
          <input type="text" id="nName" name="lastname" placeholder="Johnny" required>
        </div>
      </div>

      <div class="row">
        <div class="col-50">
          <label for="email">Email address</label>
          <input type="text" id="email" name="email" placeholder="john@example.com" required>
        </div>
        <div class="col-50">
          <label for="fitnessLevel">Fitness Level</label>
          <select id="fitnessLevel">
            <option value="level0">Couch potato</option>
            <option value="level1">Coming back from injury</option>
            <option value="level2">Go for walks</option>
            <option value="level3">Play sport</option>
            <option value="level4">Marathon runner</option>
          </select>
        </div>
      </div>

      <div class="row">
        <div class="col-50">
          <label for="password"><b>Password</b></label>
          <input type="password" id="password" name="password" placeholder="8-10 Characters" required>
        </div>
        <div class="col-50">
          <label for="psw-repeat"><b>Repeat Password</b></label>
          <input type="password" id="psw-repeat" placeholder="Repeat Password" name="psw-repeat" required>
        </div>
      </div>
    </div>
      <a href="index.html">Home</a>
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

.col-10{
  -ms-flex: 10%; /* IE10 */
  flex: 10%;
}

.col-25 {
  -ms-flex: 25%;
  flex: 25%;
}

.col-40 {
  -ms-flex: 40%; /* IE10 */
  flex: 40%;
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

.col-25,
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
  height: 1px;
  width: 1px;

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