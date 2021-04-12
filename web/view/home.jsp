

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <link rel="icon" href="image/web_icon.png?dummy=23847">
    <link rel="stylesheet" href="view/css/main.css">
    <title>We Meet</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body> 
    <div class="navbar">
      <a href='/tl'> <img src="image/web_icon.png"> </a>
<!--      <div>
        <% if( session.getAttribute("username") == null){ %>
          <a href="user?op=login">
            <button> Log in</button> 
          </a>
          <a href="user?op=register">
            <button> Sign in</button> 
          </a>
        <% }else { 
            String username = (String)session.getAttribute("username");
            String avatar = (String)session.getAttribute("avatar");
            int role = (Integer)session.getAttribute("role");
        %>
          <a href="user?username=<%= username %>" >
            <img src="image/<%= avatar +".png" %>">
          </a>
          <div class="drop-down">
            <div class="drop-down-header">  
              <img src="image/menu.png">
            </div>
            <div class="drop-down-content">
              <div class='drop-down-item'>
                <img src="image/data.png">
                <a href="setting">Your data </a>
              </div>
              <div class='drop-down-item'>
                <img src="image/setting.png">
                <a href="setting">Setting </a>
              </div>
              <div class='drop-down-item'>
                <img src='image/logout.png'>
                <a href="user?op=logout">Logout </a>
              </div>
            </div>
          </div>
        <% } %>
        </div>-->
    </div>
    <div class="body-container">
      <div class="body-header"> 
        New way to connect people
      </div>

      <div class="image-gallery">
        <div class="left-button">
          <img src="image/left_arrow.png">
        </div>
        
        <div class="image-part" >
          <img  id="image-4" src="image/image_gallery_1.png">
        </div>

        <div class="image-part" >
          <img id="image-2" src="image/image_gallery_2.png">
        </div>
        
        <div class="image-part">
          <img id="image-3" src="image/image_gallery_3.png">
        </div>
        
        <div class="image-part">
          <img id="image-1" src="image/image_gallery_4.png">
        </div>
          
        <div class="right-button">
          <img src="image/right_arrow.png">
        </div>
      </div>

      <div class='body-main-button'>
        <div id="btnStart" class="button-deco">
          <button> Lets go</button>
          <img src='image/start.png'>
        </div>
      </div>
    </div>

    <!-- *init-->
    <script>
      let dropDown = document.querySelector('.drop-down-content');

      if( dropDown){
        dropDown.style.display = "none";
      }

      window.onclick = (e)=>{        
        if( dropDown)
          if( dropDown.style.display !== "none")
            dropDown.style.display = "none"
      }
    </script>

    <!-- *handle image gallery -->
    <script>
      let currImage = 1;
      let totalImage = 4;

      setImage = ()=>{
        for(let i=1 ; i< totalImage + 1 ; i++){
          let image = document.querySelector('#image-' + i);
          image.style.display =  currImage === i ? "block" : "none";
        }
      }

      let btnClick = (isLeft)=>{
        if(isLeft){
          if( currImage == 1){
            currImage = totalImage;
          }else{
            currImage -= 1;
          }

          
        }else{
          if( currImage == totalImage){
            currImage = 1;
          }else{
            currImage += 1;
          }
        }
        setImage();
      }
      let btnLeft = document.querySelector('.left-button')
      let btnRight = document.querySelector('.right-button')

      setImage();
      btnLeft.addEventListener('click',()=>{ btnClick(true) })
      btnRight.addEventListener('click',()=>{ btnClick(false) })
    </script>

    <!-- *handle dropdown -->
    <script>
      if( dropDown){       
        document.querySelector('.drop-down-header').onclick = (e)=>{ 
          let content = document.querySelector('.drop-down-content');
          if( content.style.display === 'none')
            content.style.display = "flex";
          else  
            content.style.display = "none";

          e.stopPropagation();
        }
      }
      
        
    </script>

    <!-- *handle start button -->
    <script>
      mainBtnClick = (e,isJoin)=>{
        window.location.href = "joinroom";
        e.stopPropagation();
      }

      document.querySelector('#btnStart').onclick =  (e)=>{
         mainBtnClick(e,true)
      }
    </script>
  </body>
</html>