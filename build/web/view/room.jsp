<%@page import="java.util.List"%>
<!DOCTYPE html> 
<html>
    <head>
        <title> We Meet </title>
        <link rel="icon" href="image/web_icon.jpg">
        <script src="view/js/jquery-3.5.1.min.js" integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=" crossorigin="anonymous"></script>
        <script src="view/js/adapter-latest.js"></script>
        <link rel='stylesheet' href="view/css/room.css">
    </head>
    <body>
        <div class='container'>
            <div class='content'>
                <div class='video'>
                    <video id='local' muted autoplay></video>
                </div>
                <div class='option'>
                    <div class="option-container">
                        <div id='home-option' onclick="homeBtnClick()" >
                            <img src="image/web_icon.png">
                        </div>
                        <div>
                            <span id="info-clonename"> </span>
                            <br>
                            <span id="info-members"> </span>
                        </div>
                        <div id='message-option' >
                            <img src="image/message.png">
                        </div>
                    </div>
                </div>
                <div class='chat'>
                    <div class='chat-header'> CHAT</div>
                    <div class='chat-content'>  </div>
                    <div  class='chat-input'>
                        <input type="text" id='input' placeholder="Type something">
                    </div>
                </div>
            </div>
           
            <div class='config'>
                <div class="media-config">
                    <div id='webcam'>
                        <img  src='image/webcam_off.png' height="100%">
                    </div>
                    <div id='mic'>
                        <img src='image/mic_mute.png' height="100%" >
                    </div>
                </div>
            </div>
            <div class="pop-up">
                <div class="pop-up-header">
                    <div> Pick a name to join ROOM xD</div>
                </div>
                <div class="pop-up-content">
                    <div>
                        Start with an alphabet
                    </div>
                    <input id="cloneName" type="text" placeholder="Enter your name">
                    <button id="btnGo"> Go </button>
                </div>
                
            </div>
        </div>
        <div>

        </div>
        <script>    
            homeBtnClick = ()=>{
                let out = confirm("Are you sure want to quit room?");
                if(out){
                    location.href = "/tl";
                }
            }
        </script>
        <script>
            let chat  = document.querySelector('.chat');
            let message_option = document.querySelector('#message-option');
            let video_segment = document.querySelector('.video');
            let config = document.querySelector(".config");
            
            chat.style.display = 'none'

            message_option.onclick = ()=>{
                if( chat.style.display !== "none"){
                    chat.style.display = "none";
                    video_segment.style.width = "100%";
                    config.style.width = "100%";
                }else{
                    chat.style.display = "block";
                    video_segment.style.width = "75%";
                    config.style.width = "75%";
                }
            }
        </script>
        
        <% String cloneName = (String)request.getAttribute("clonename"); %>
        <% String roomid = (String)request.getAttribute("roomid"); %>
        <% List<String> member = (List<String>)request.getAttribute("member"); %>
        <script>
            console.log("d <%= cloneName %>")
            console.log("d <%= roomid %>")

            let canStart = true;
            let popUp = document.querySelector('.pop-up');
            let roomID = '<%= roomid %>';
            let cloneName = '<%= cloneName %>';
            
            popUp.style.display = "none";
            
            document.querySelector('#btnGo').onclick = (e)=>{
                let cloneName = document.querySelector('#cloneName').value;
                if( cloneName !== ""){
                    cloneName += "_" + Date.now();
                    sessionStorage.setItem('myapproomid',roomID);
                    sessionStorage.setItem('myappclonename',cloneName);
                    location.href = 'room?roomid=' + roomID + "&clonename=" + cloneName;
                }        
            }        
           

            if( sessionStorage.getItem('myappclonename') === null || (  sessionStorage.getItem("myappclonename") !== null && sessionStorage.getItem("myappclonename") !== "<%= cloneName %>" ) ){
                canStart = false;
                popUp.style.display = "block";
                window.addEventListener('click',(e)=>{
                    if( e.target != document.querySelector('#btnGo')){
                        e.stopPropagation();
                        e.preventDefault();
                    } },
                    true);
            }
            
            var member_temp = [];
            <% for(int i=0 ; i<member.size() ; i++){ %>
                member_temp.push('<%= member.get(i) %>');
            <% } %>;
        </script>

        <script src='view/js/room.js'>
        </script>
    </body>
</html>
