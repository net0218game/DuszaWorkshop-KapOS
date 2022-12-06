var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var contactName = document.querySelector('.contactName');
var inputBox = document.querySelector('#message');

var stompClient = null;
var username = null;
var receiver = ""

// Üzenet Maximum Hossza
var maxLength = 64;

const date = new Date();

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

username = document.querySelector('.dropbtn').textContent.trim();

var socket = new SockJS('/ws');
stompClient = Stomp.over(socket);

stompClient.connect({}, onConnected, onError);


function onConnected() {
    // Subscribe to the Public Chat
    stompClient.subscribe('/all/messages', onMessageReceived);
    stompClient.subscribe('/user/specific', onMessageReceived);

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'NEM SIKERULT CSATLAKOZNI A WEBSOCKET SERVERHEZ! INDÍTSD EL AZ ALKALMAZÁST!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            receiver: receiver,
            content: messageInput.value,
            type: 'CHAT'
        };

        // Uzenetek Megjelenitese Sajat Magadnak
        if (receiver !== username) {
            displayMessage(username, messageInput.value)
        }
        if(receiver === "DuszaGroupChat") {
            stompClient.send("/app/application", {}, JSON.stringify(chatMessage));
        } else {
            stompClient.send("/app/private", {}, JSON.stringify(chatMessage));
        }

        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined the Hype!';

        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;

    } else if (message.type === "CHAT") {
        if(message.sender === username || message.sender === receiver) {
            displayMessage(message.sender, message.content)
        }

        if(message.sender !== receiver) {
            displayNotification(message.sender, message.content)
        }
        displayLastMessages(receiver, username)
    }
}

// ========== Üzenet Hossz Ellenőrzése ==========
function textLength(value) {
    return value.length <= maxLength;
}

document.getElementById('message').onkeyup = function () {
    if (!textLength(this.value)) {
        // BoxShadow Pirosra Valtoztatasa
        messageInput.style.boxShadow = "0 0px 30px rgba(255, 0, 0)";
    } else {
        // BoxShadow Visszavaltoztatasa Feherre
        messageInput.style.boxShadow = "0 0px 30px rgba(255, 255, 255)";
    }
}

// ========== Üzenet Hossz Ellenőrzése ==========

// Üzenet Megjelenitese
function displayMessage(messageUsername, content) {
    if (receiver !== "") {
        var messageElement = document.createElement('li');
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(messageUsername[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(messageUsername);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(messageUsername);
        var dateText = document.createTextNode(" - " + date.getHours() + ":" + date.getMinutes());

        usernameElement.appendChild(usernameText);
        usernameElement.appendChild(dateText);
        messageElement.appendChild(usernameElement);

        var textElement = document.createElement('p');

        // Ha van a szovegben URL akkor beteszi <a> tagba.
        textElement.innerHTML = (replaceURLs(content));

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }
}

// Contactok Listazasa
function getContactName(contact) {

    // Torli az elozo uzeneteket
    messageArea.innerHTML = ""
    // Beallitja az uzenet fogadojat, es a contact cimet
    receiver = contact.id;
    contactName.innerHTML = receiver;
    // Beallitja az input box placeholder-jét
    inputBox.placeholder = "Send a message to " + receiver + "!"

    displayAllMessages();
    document.getElementById('notification-div').innerHTML = "";
    displayLastMessages(receiver, username);
}

// Avatar Szin Letrehozasa Felhasznalonak
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// URLek Felismerese A Szovegben Es Beagyazasuk
function replaceURLs(message) {
    if (!message) return;

    var urlRegex = /(((https?:\/\/)|(www\.))[^\s]+)/g;
    var youtube = /(https?\:\/\/)?((www\.)?youtube\.com|youtu\.be)\/.+$/g;

    return message.replace(urlRegex, function (url) {
        var hyperlink = url;
        if (!hyperlink.match('^https?:\/\/')) {
            hyperlink = 'http://' + hyperlink;
        }
        if (message.match(youtube)) {
            // Youtube Link Beagyazasa Videokent
            return '<a href="' + hyperlink + '" target="_blank">' + url + '</a><br><iframe width="560" height="315" src="' + hyperlink.replace("watch?v=", "embed/") + '" frameborder="0" allowfullscreen></iframe>'
        } else {
            // Atlagos Link Beagyazasa
            return '<a href="' + hyperlink + '" target="_blank">' + url + '</a>'
        }
    });
}

messageForm.addEventListener('submit', sendMessage, true)

function openNav() {
    document.getElementById("mySidenav").style.width = "400px";
    document.getElementById("chat-page").style.marginLeft = "400px";
    document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
    document.getElementById("chat-page").style.marginLeft = "0";
    document.body.style.backgroundColor = "white";
}

function displayAllMessages() {
    fetch('/listMessages/' + receiver + '/' + username, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            for(let i = 0; i < Object.keys(data).length; i++) {
                displayMessage(data[i].sender, data[i].content)
            }
        });
}
function displayLastMessages(receiver, username){


    fetch('/lastMessage/' + receiver + '/' + username, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log(data)
            for(let i = 0; i < Object.keys(data).length; i++) {
                document.querySelectorAll('#' + receiver)[i].innerHTML += '<p>' + data[i].content +'</p>'
            }
        });
}


function closeNotification(meik) {
    var x = document.getElementById(meik.parentElement);
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
}

let notificationId = 0;

function displayNotification(sender, content) {

    document.getElementById('notification-div').innerHTML = '<label class="alert-message" id="notification-' + notificationId.toString() + '" onclick="getContactName(' + sender +')">\n' +
        '    <strong class="hype"> <i class="fa fa-message"></i> NEW MESSAGE</strong><br><span id="notification-text">' + sender.toUpperCase() + ': ' + content + '</span>\n' +
        '    <button onclick="closeNotification(this)" class="close"><i class="fa fa-close"></i></button>\n' +
        '</label>'

    notificationAudio()

    setTimeout(function () {

        document.getElementById('notification-div').innerHTML = '<label class="hidden" id="notification-' + notificationId.toString() + '" onclick="getContactName(' + sender +')">\n' +
            '    <strong class="hype"> <i class="fa fa-message"></i> NEW MESSAGE</strong><br><span id="notification-text">' + sender.toUpperCase() + ': ' + content + '</span>\n' +
            '    <button onclick="closeNotification(this)" class="close"><i class="fa fa-close"></i></button>\n' +
            '</label>'
    }, 5000)
}

function notificationAudio() {
    var audio = new Audio("Media/notification.mp3");
    audio.play();
}