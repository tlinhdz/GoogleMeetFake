<!DOCTYPE html>
<html>
  <head>
    <link rel="icon" href="image/web_icon.jpg">
    <link rel="stylesheet" href="view/css/main.css">
    <title>We Meet</title>
  </head>
  <body> 
    <div class="navbar">
      <a href="/tl"><img src="image/web_icon.jpg"> </a>
      <div>
        <% if( request.getAttribute("op") != null){
            String op = (String)request.getAttribute("op"); 
         if( op == "LOGIN") { %> 
          <a href="user?op=register"><button> Register</button> </a>
        <% } else { %>
          <a href="user?op=login"><button> Login</button> </a>
        <% }  %>
      </div>
    </div>
   
    <div class='body-container'> 
      <% if( op == "LOGIN") { %> 
      <form id="login" action='user' class='lr-form' method="post" >
          <input type="hidden" name="op" value="login">
          <input name='username' id='username' type="text" placeholder="Username" pattern="[a-zA-z0-9]{1,15}" required>
          <input name='password' id='password' type='password' placeholder="Password" required></input>
          <button class='btn-login'> Login</button>
          <p class="warning"></p>
        </form>
      <% } else { %>
        <form id="register" action='user' class='lr-form' method="post">
          <input type="hidden" name="op" value="register">
          <input name='username' id='username' type="text" placeholder="Username" pattern="[a-zA-z0-9]{1,15}" required>
          <input name='password' id='password' type='password' placeholder="Password" required></input>
          <input id='confirm-password' type='password' placeholder="Confirm Password" required></input>
          <input name='email' id='mail' type='mail' placeholder="Mail" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$" required></input>

          <button class='btn-login'> Register</button>
        </form>
      <% } }%>
      
      <% if( request.getAttribute("error") != null){ %>
        <h2> <%= request.getAttribute("error") %>  </h2>
      <% } %>
    </div>
  </body>
</html>