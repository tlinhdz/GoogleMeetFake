let localPeerConnection;
var localVideo, remoteVideo;
var localStream;
var mic, webcam;
const servers = {
    "iceServers": [{ "url": "stun:stun.1.google.com:19302" }]
};;

var micOn = false;
var webcamOn = false;
var xhr_timeout = 120000;

const offerOptions = {
    offerToReceiveVideo: 1
}

var member = [];
var pairPeerConnection = [];
var pendingCandidate = [];
var updateRequestID = Date.now();

var numberOfVideoPerLine = 4;

$(document).ready(function () {
    console.log(cloneName);
    checkRoom(roomID)
    .then( ()=>{
        console.log(roomID)
        if( canStart){
            localVideo = document.querySelector("#local");
            mic = document.querySelector('#mic');
            webcam = document.querySelector('#webcam');
            setUpMediaDevices();
            setUp();

        }
    })
    .catch( (e)=>{
        console.log("Loi roi ",e);
    })
});

checkRoom = (roomid)=>{
    return new Promise( (resolve,reject)=>{
        var xhr = new XMLHttpRequest();
        let params = "&roomid=" + roomid;
        xhr.open('GET', 'room?op=check'+params, true);
        xhr.onreadystatechange = () => {
            if (xhr.readyState == 4 && xhr.status == 200) {
                resolve();
            }else if( xhr.readyState == 4 && xhr.status == 204){
                console.log( xhr.status);
                alert("This room is full! Please enter or create another room!");
                window.location.href = "/tl/joinroom";
            }
        }

        console.log("Checking room");
        xhr.send();
    })
}

setUpMediaDevices = () => {
    if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {};
    }

    if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
            var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

            if (!getUserMedia) {
                return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
            }

            return new Promise(function (resolve, reject) {
                getUserMedia.call(navigator, constraints, resolve, reject);
            });
        }
    }
}

setUp = async () => {
    let input = document.querySelector('#input');
    input.onkeyup = (e) => {
        if (e.keyCode === 13) {
            let text = input.value;
            if (text != "") {
                xhr = new XMLHttpRequest();
                xhr.open('POST', 'signalling', true);
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.onreadystatechange = () => {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                    }
                }

                console.log("sending message");

                xhr.send(JSON.stringify({
                    op: "message",
                    roomid: roomID,
                    val: {
                        text: text,
                    },
                    clonename: cloneName,
                }));

                input.value = '';
            }
        }
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
    
    joinRoomSuccessful();
    setUpUserOutEvent();

    let stream = await navigator.mediaDevices.getUserMedia({
        "video": webcamOn,
        "audio": true,
    })

    stream.getAudioTracks().forEach(track => track.stop());

    var videoContainer = document.querySelector('.video');
    localVideo.width = localVideo.height =  videoContainer.offsetWidth / 4;
    localVideo.srcObject = stream;
    localStream = stream;

    sendUpdateRequest();

    //let member_temp = document.querySelectorAll('input');
    for (var i = 0; i < member_temp.length; i++) {
        if (member_temp[i] !== cloneName) {
            var newLocal = newMemberJoin(member_temp[i]);
            setUpPeerConnection(newLocal, member_temp[i]);
        }
    }
}

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

    let stream = await navigator.mediaDevices.getUserMedia(constraint)

    media.srcObject = stream;

    for (var i = 0; i < pairPeerConnection.length; i++) {
        pairPeerConnection[i].connection.removeStream(localStream);
        pairPeerConnection[i].connection.addStream(stream)
    }
    localStream = stream;

    if (!webcamOn && !micOn) {
        localStream.getAudioTracks().forEach(track => track.stop());
        console.log("mute")
        localStream.getAudioTracks().forEach(track => console.log(track));
    } else {
        localStream.getAudioTracks().forEach(track => console.log(track));
    }

    return true;
}

joinRoomSuccessful = () => {
    var xhr = new XMLHttpRequest();
    let params = "&clonename=" + cloneName  + "&roomid=" + roomID;
    xhr.open('GET', 'room?op=justjoinroom' + params, true);
    xhr.onreadystatechange = () => {
        if (xhr.readyState == 4 && xhr.status == 200) {
            let message = xhr.response;
            message = JSON.parse(message);
            let clone_name = document.querySelector("#info-clonename");
            let mem = document.querySelector("#info-members");
            
            let temp = cloneName.split("_",2);
            clone_name.innerText = "ID: " + temp[0] + " #" + temp[1];
            mem.innerText = "Members: " + message.mem;
        }else if( xhr.readyState == 4 && xhr.status == 204){
            alert("This room is full! Please enter or create another room!");
            window.location.href = "/tl/joinroom";
        }
    }

    console.log("joined room");
    console.log(cloneName)
    console.log(roomID)

    xhr.send();
}

setUpUserOutEvent = () => {
    let alreadySentData = false;

    window.addEventListener("beforeunload", () => {
        sendTicketsBeforeCloseSession();
    });
    window.addEventListener("unload", () => {
        sendTicketsBeforeCloseSession();
    });

    function sendTicketsBeforeCloseSession() {
        let DESTINATION_URL = "room";
        let encodedTickets = JSON.stringify({
            op: "justleaveroom",
            clonename: cloneName,
            roomid: roomID,
        })

        let headers = {
            type: 'application/json'
        };

        let blob = new Blob([encodedTickets], headers);

        if (alreadySentData) {
            return;
        }

        if (typeof navigator.sendBeacon !== "undefined") {
            const success = navigator.sendBeacon(DESTINATION_URL, blob);
            alreadySentData = success;
        } else {
            const xhr = new XMLHttpRequest();
            xhr.open("POST", DESTINATION_URL, false);
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.send(encodedTickets);
            if (xhr.status === 200) {
                this.sentLastTickets = true;
            }
        }
    }
}

sendUpdateRequest = async () => {
    let params = "clonename=" + cloneName;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'update?'+ params , true);
    xhr.timeout = xhr_timeout;

    xhr.onreadystatechange = async () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log("receving new udpate request");
            console.log(xhr.response)
            sendUpdateRequest();
            await updateMessageHandler(xhr.response);
        }
    }
  
    xhr.ontimeout = () => {
        sendUpdateRequest();
    }
    console.log("sending updateee");
    xhr.send();
}

setUpPeerConnection = (connection, desUser) => {
    connection.addEventListener('icecandidate', (e) => ((arg) => handleConnection(e, arg))(desUser));
    connection.addEventListener('connectionstatechange', (e) => ((arg1, arg2) => handleConnectionChange(e, arg1, arg2))(connection, desUser));
    connection.addEventListener('iceconnectionstatechange', (e) => ((arg) => handleIceConnectionChange(e, arg))(connection));
}

setUpConnection = (connection, desUser) => {
    connection.onnegotiationneeded = async () => {
        try {
            await connection.setLocalDescription(await connection.createOffer());
            xhr = new XMLHttpRequest();
            xhr.open('POST', 'signalling', true);
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onreadystatechange = () => {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    console.log("sent offer");
                }
            }            
            console.log("sending offer");console.log( connection.localDescription);
            xhr.send(JSON.stringify({
                op: "offer",
                roomid: roomID,
                clonename: cloneName,
                val: connection.localDescription,
                to: desUser,
            }));
            
            
        } catch (e) {
            console.log(e);
        }
    }
}

setUpConnection1 = (connection, desUser) => {
    connection.onnegotiationneeded = async () => {
        try {
            xhr = new XMLHttpRequest();
            xhr.open('POST', 'signalling', true);
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onreadystatechange = () => {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    console.log("sending offer");
                }
            }

            xhr.send(JSON.stringify({
                op: "offer",
                roomid: roomID,
                clonename: cloneName,
                val: connection.localDescription,
                to: desUser,
            }));
        } catch (e) {
            console.log(e);
        }
    }
}

newMemberJoin = (clone_name) => {
    member.push(clone_name);
    let mem = document.querySelector("#info-members");
    mem.innerText = "Members: " + ( member.length + 1);
    var videoContainer = document.querySelector('.video');
    var newVid = document.createElement('video');
    newVid.setAttribute('id', "I"+clone_name.split("_",2)[1]);
    newVid.autoplay = true;
    videoContainer.appendChild(newVid);

    let videos = document.querySelector('.video').querySelectorAll('video');
    
    let numberOfMem = member_temp.length + 1;

    let numberOfLine = numberOfMem < numberOfVideoPerLine ? 1 : Math.ceil(numberOfMem / numberOfVideoPerLine);

    let width_height = ( videoContainer.offsetWidth - 100) / numberOfVideoPerLine;

    for(let vid of videos){
        vid.style.width = width_height + "px";
        vid.style.height = width_height + "px";
    }

    var newLocal = new RTCPeerConnection();

    localStream.getTracks().forEach((track) => {
        newLocal.addTrack(track, localStream);
    })

    var remoteStream = new MediaStream();

    newVid.srcObject = remoteStream;
    newLocal.ontrack = (e) => {
        var remote = new MediaStream();
        newVid.srcObject = remote;
        remote.addTrack(e.track, remote);
    }

    pairPeerConnection.push({
        connection: newLocal,
        cloneName: clone_name,
        video: newVid,
        media: remoteStream
    })
    return newLocal;
}


updateMessageHandler = async (message) => {
    if( message.match(/\S/)){
        message = JSON.parse(message);
        if (message.op === "newmember") {
            console.log('new member');
            var t = newMemberJoin(message.clonename);
            setUpPeerConnection(t, message.clonename);
            setUpConnection(t, message.clonename);
        } else if (message.op === "offer") {
            var localPeer = searchConnection(message.from).connection;
            if (localPeer) {
                console.log("receive offer");
                await localPeer.setRemoteDescription(message.val);
                await localPeer.setLocalDescription(await localPeer.createAnswer());
                setUpConnection(localPeer, message.from);
                addPendingCandidate(localPeer, message.from);
    
                var xhr = new XMLHttpRequest();
                xhr.open('POST', 'signalling', true);
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.onreadystatechange = () => {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        console.log("sending answer");
                    }
                }
    
                xhr.send(JSON.stringify({
                    op: "answer",
                    roomid: roomID,
                    clonename: cloneName,
                    val: localPeer.localDescription,
                    to: message.from,
                }));
    
            } else {
                console.log("Ko tim thay peer connection offer");
            }
        }else if (message.op === "answer") {
            var localPeer = searchConnection(message.from).connection;
            if (localPeer) {
                console.log("receive answer");
                await localPeer.setRemoteDescription(message.val);
    
            } else {
                console.log("Ko tim thay peer connection answer");
            }
        }else if (message.op === "candidate") {
            var localPeer = searchConnection(message.from).connection;
            if (localPeer) {
    
                localPeer.addIceCandidate(new RTCIceCandidate(message.val))
                    .then(() => {
                        console.log("receive ICE candidate")
                    })
                    .catch(() => {
                        pendingCandidate.push({
                            cloneName: message.from,
                            candidate: message.val,
                        })
                    })
            } else {
                console.log("Ko tim thay peer connection candidate");
            }
        }else if (message.op === "message") {
            addNewMessage(message.from, message.val.text);
        }else if (message.op === "userleaveroom") {
            member.splice( member.indexOf(message.clonename),1);
            console.log(message, member.indexOf(message.clonename));
            let mem = document.querySelector("#info-members");
            mem.innerText = "Members: " + ( member.length + 1);
            console.log(member);
            let container = document.querySelector('.video');
            let video = document.querySelector('#I' + message.clonename.split("_",2)[1]);
            container.removeChild(video);
    
            let connection = searchConnection(message.clonename);
            connection.connection.close();
            pairPeerConnection.splice(pairPeerConnection.indexOf(connection), 1);
        }else if (message.op === "ok") {
            console.log(" ok day ")
        }else {
            console.log("Tam thoi chua ho tro : " + message.val);
        }
    }
}

addNewMessage = (clone_name, text) => {
    let chat = document.querySelector('.chat-content');

    let chatText = document.createElement('div');
    chatText.classList.add('chat-text');

    let content = document.createElement('div');
    content.classList.add("chat-content-c");
    content.innerHTML = text;

    let chatOwner = document.createElement('div');
    chatOwner.classList.add('chat-owner');
    let clone_name_arr = clone_name.split('_',2);
    chatOwner.innerHTML = clone_name_arr[0] + " #" + clone_name_arr[1];

    chatText.appendChild(content);
    chatText.appendChild(chatOwner);
    chat.appendChild(chatText);
}

searchConnection = (clone_name) => {
    var t = null;
    for (var i = 0; i < pairPeerConnection.length; i++) {
        if (pairPeerConnection[i].cloneName === clone_name) {
            t = pairPeerConnection[i];
        }
    }
    return t;
}

addPendingCandidate = (connection, desUser) => {
    var t = searchCandidate(desUser);
    for (var i = 0; i < t.length; i++) {
        connection.addIceCandidate(t[i]);
    }
}

searchCandidate = (clone_name) => {
    var t = [];
    for (var i = 0; i < pendingCandidate.length; i++) {
        if (pendingCandidate[i].cloneName === clone_name) {
            t.push(pendingCandidate.candidate);
        }
    }

    return t;
}

handleConnection = (event, desUser) => {
    const iceCandidate = event.candidate;

    if (iceCandidate) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'signalling', true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onreadystatechange = () => {
            if (xhr.readyState == 4 && xhr.status == 200) {
                console.log("sending candidate ");
            }
        }
        
        xhr.send(JSON.stringify({
            op: "candidate",
            roomid: roomID,
            clonename: cloneName,
            val: iceCandidate,
            to: desUser,
        }));
    }
}

handleOnDataReceive = (e, desUser) => {
    console.log("receive data")
}

handleConnectionChange = async (event, connection, desUser) => {
    if (connection.connectionState === 'connected') {
        console.log("Connection Connected")
    } else if (connection.connectionState === 'disconnected') {
        console.log("connection to " + desUser + " lost");
    } else if (connection.connectionState === 'failed') {
        console.log("Connection Failed")
    }
}

handleIceConnectionChange = (event, connection) => {
    if (connection.iceConnectionState === 'connected') {
        console.log("IceConnection Connected")
    } else if (connection.iceConnectionState === 'disconnected') {
        console.log("IceConnection Disconnected")
    } else if (connection.iceConnectionState === 'failed') {
        console.log("IceConnection Failed")
    }
}
