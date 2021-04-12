<!DOCTYPE html> 
<html>
    <head>
        <title> We Meet </title>
        <link rel="icon" href="image/web_icon.png">
        <script src="view/js/jquery-3.5.1.min.js"></script>
        <link rel='stylesheet' href="view/css/room.css">
        <link rel='stylesheet' href="view/css/main.css">
    </head>
    <body>
        <div class="navbar">
            <a href='/tl'> <img src="image/web_icon.png"> </a>
<!--            <div>
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
        <div class='joinroom-container'>
            
            <div class='video-sample'>
                <video id='local' class='sample' autoplay></video>
                <div class="video-sample-control">
                    <div id='webcam'>
                         <img  src='image/webcam_off.png' height="100%">
                    </div>
                   
                    <div id='mic'>
                        <img src='image/mic_mute.png' height="100%" >
                    </div>
                </div>
            </div>
            <div class='form-sample'>
                <div id='join' class='button-item '>
                    <button id='btnJoin' class='button' > Join </button>
                    <div id='button-join-detail' class="button-detail">
                        <div class='button-detail-setting'>
                            <input  id='roomID' type="text" placeholder="Room ID" class="normal-border">
                        </div>
                        <div class='button-detail-button'>
                            <button id='joinStart' class='button'> Start </button>
                        </div>
                    </div> 
                </div>
                
                <div id='create' class=' button-item '>
                    <button id='btnCreate' class='button'> Create </button>
                    <div id='button-create-detail' class="button-detail">
                        <div class='button-detail-setting'>
                            <input type="number" id="numMember" min="2" max="30" class="normal-border" placeholder="Members(2 - 30)" >
                        </div>
                       
                        <div class='button-detail-button'>
                            <button id="createStart" class='button'> Start </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="pop-up">
            <div class="pop-up-header"> Pick a name to join ROOM xD</div>
            <div class="pop-up-content">
                <div>
                    Start with an alphabet
                </div>
                <form id="Go">
                    <input id="cloneName" class='normal-border' type="text" placeholder="Enter your name">
                    <button> Go </button>
                </form>
            </div>
            
        </div>
              
              
        <script>
        let dropDown = document.querySelector('.drop-down-content');

        if( dropDown){
            dropDown.style.display = "none";
            document.querySelector('.drop-down-header').onclick = (e)=>{ 
                let content = document.querySelector('.drop-down-content');
                console.log('dfasdf')
                if( content.style.display === 'none')
                  content.style.display = "flex";
                else  
                  content.style.display = "none";

                e.stopPropagation();
            }
        }
        
        </script>
        <!-- * set up buttons -->
        <script>
            let popUp = document.querySelector('.pop-up');

            popUp.style.display = "none";
            popUp.onclick = (e)=>{
                e.stopPropagation();
            }
            
            window.onclick = (e)=>{
                if( dropDown){
                    if( dropDown.style.display !== "none"){
                        dropDown.style.display = "none"
                    }
                }
                
                if( popUp.style.display !== "none")
                    popUp.style.display = "none";
            } 

            let isJoin;
            let join = document.querySelector('#join');
            let create = document.querySelector('#create');
            let btnJoinDetail = document.querySelector('#button-join-detail');
            let btnCreateDetail = document.querySelector('#button-create-detail');

            btnJoinDetail.style.display = "none";
            btnCreateDetail.style.display = "none";
          

            localStorage.setItem("isDirect",true);

            document.querySelector('#Go').onsubmit =  (e)=>{
                e.preventDefault();

                let cloneName = document.querySelector('#cloneName').value + "_" +  Date.now();
                
                if( isJoin ){
                    let id = document.querySelector('#roomID').value;
                    var params = "roomid=" + id + "&clonename=" + cloneName  ;
                    var xhr = new XMLHttpRequest();
                    xhr.open('GET','room?op=check&' + params,true);
                    xhr.onreadystatechange = ()=>{
                        if( xhr.readyState == 4 && xhr.status == 200){
                            sessionStorage.setItem('myapproomid',id);
                            sessionStorage.setItem('myappusername',cloneName);
                            location.href = 'room?roomid=' + id + "&clonename=" + cloneName;
                        }else if( xhr.readyState == 4 && xhr.status == 204 ){
                            alert("Cannot join room !");
                        }
                    }
                    
                    xhr.send();   
                }else{
                    let numMember = document.querySelector('#numMember').value;
                        
                    var params = "nummem=" + numMember + "&clonename=" + document.querySelector('#cloneName').value ;
                    var xhr = new XMLHttpRequest();
                    xhr.open('GET','room?op=createroom&' + params,true);
                    xhr.onreadystatechange = ()=>{
                        if( xhr.readyState == 4 && xhr.status == 200){
                            sessionStorage.setItem('myapproomid',xhr.responseText);
                            sessionStorage.setItem('myappclonename',cloneName);
                            location.href = 'room?roomid=' + xhr.responseText + "&clonename=" + cloneName;
                        }else if( xhr.readyState == 4 && xhr.status == 400){
                            alert("Check your name to match the format or Maximum number of member");
                        }
                    }
                    
                    xhr.send();   
                }
            }

            $("#btnJoin").click( (e)=>{
                join.classList.add('button-item-c');
                create.classList.remove('button-item-c');
                btnJoinDetail.style.display = "flex";
                btnCreateDetail.style.display = "none";
            })

            $("#btnCreate").click( (e)=>{
                join.classList.remove('button-item-c');
                create.classList.add('button-item-c');
                btnJoinDetail.style.display = "none";
                btnCreateDetail.style.display = "flex";
            })

            $('#joinStart').click( (e)=>{
                let roomID = document.querySelector('#roomID'); 

                if( roomID.value.match(/\S/)){
                    roomID.classList.remove('warning-border')
                    roomID.classList.add('normal-border')
                    isJoin = true;
                    popUp.style.display = "flex";
                }else{
                    roomID.classList.remove('normal-border')
                    roomID.classList.add('warning-border')
                }
                
                e.stopPropagation();
            })

            $('#createStart').click( (e)=>{
                let numMem = document.querySelector('#numMember'); 
                
                if( 2 <= parseInt(numMem.value) && parseInt(numMem.value) <= 30  ){
                    numMem.classList.remove('warning-border')
                    numMem.classList.add('normal-border')

                    isJoin = false;
                    popUp.style.display = "flex";
                }else{
                    numMem.classList.remove('normal-border')
                    numMem.classList.add('warning-border')
                }

                e.stopPropagation();
            })
        </script>

        <!-- * set up media -->
        <script>
            let micOn = true;
            let webcamOn = false;
            let webcam = document.querySelector('#webcam');
            let mic = document.querySelector('#mic');
            let localVideo = document.querySelector('.sample');

            const mediaStreamConstaints = {
                "audio": micOn,
                "video": webcamOn,
            }
            
            navigator.mediaDevices.getUserMedia(mediaStreamConstaints)
            .then( (stream)=>{
                stream.getAudioTracks().forEach(track => track.stop());
                micOn = false;
                document.querySelector('video').srcObject = stream;
            })
            .catch( (err)=>{
                console.log(err);
            })


            changeMedia = async (media) => {
                let constraint;
                if (!webcamOn && !micOn) {
                    constraint = {
                        "video": webcamOn,
                        "audio": true
                    }
                } else {
                    constraint = {
                        "video": webcamOn,
                        "audio": micOn,
                    }
                }

                let stream = await navigator.mediaDevices.getUserMedia(constraint);

                if( !webcamOn && !micOn)
                    stream.getAudioTracks().forEach(track => track.stop());

                media.srcObject = stream;

                return true;
            }

            webcam.onclick = (e) => {
                if (webcamOn) {
                    webcam.querySelector("img").src = 'image/webcam_off.png';
                } else {
                    webcam.querySelector("img").src = 'image/webcam.png';
                }
                webcamOn = !webcamOn;
                if (!changeMedia(localVideo))
                    webcamOn = !webcamOn;
            }

            mic.onclick = (e) => {
                if (micOn) {
                    mic.querySelector("img").src = 'image/mic_mute.png';
                } else {
                    mic.querySelector("img").src = 'image/mic.png';
                }
                micOn = !micOn;
                if (!changeMedia(localVideo))
                    micOn = !micOn;
            }
        </script>
    </body>
</html>
