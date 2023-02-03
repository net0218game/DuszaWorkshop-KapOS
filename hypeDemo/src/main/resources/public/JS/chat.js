var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var contactArea = document.querySelector('#contactArea');
var connectingElement = document.querySelector('.connecting');
var contactName = document.querySelector('.contactName');
var inputBox = document.querySelector('#message');

var stompClient = null;
var username = null;
var receiver = ""

// Üzenet Maximum Hossza
var maxLength = 64;

var group_chat = "DuszaGroupChat"
var hypeBot = "hypeBot"

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
    stompClient.subscribe('/all/dusza-group', onMessageReceived);
    stompClient.subscribe('/user/specific', onMessageReceived);

    connectingElement.classList.add('hidden');

    fetch('/userStatus/online', {
        method: 'POST',
    })

    displayAllFriends()
}

function onError(error) {
    connectingElement.textContent = 'NEM SIKERULT CSATLAKOZNI A WEBSOCKET SERVERHEZ! INDÍTSD EL AZ ALKALMAZÁST!';
    connectingElement.style.color = 'red';
}

socket.addEventListener('close', (event) => {
    fetch('/userStatus/offline', {
        method: 'POST',
    })
})

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {

        const date = new Date();
        let hours = date.getHours();
        let minutes = String(date.getMinutes()).padStart(2, '0');
        var time = hours + ':' + minutes;

        var chatMessage = {
            sender: username,
            receiver: receiver,
            content: messageInput.value,
            date: time,
            type: 'CHAT'
        };

        if (receiver === group_chat) {
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        } else {
            stompClient.send("/app/private", {}, JSON.stringify(chatMessage))

            if (receiver !== hypeBot) {
                console.log("displayed")
                displayLastMessages(receiver, username)
                displayAllMessages(receiver, username)
            }

        }
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.type === "CHAT") {
        if (message.sender !== hypeBot) {
            displayAllMessages(receiver, username)
            //displayMessage(message.sender, message.content, message.date)
        } else {
            displayMessage(message.sender, message.content)
        }

        if (message.sender !== receiver && message.receiver !== group_chat) {
            displayNotification(message.sender, message.content)
        }

        if (message.receiver !== group_chat) {
            displayLastMessages(message.sender, username)
        }
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
function displayMessage(messageUsername, content, time, messageId) {
    if (receiver !== "") {
        var messageElement = document.createElement('li');

        messageElement.classList.add('chat-message');
        messageElement.setAttribute('id', messageId)

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(messageUsername[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(messageUsername);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(messageUsername);

        var deleteButton = document.createElement("button")
        deleteButton.textContent = "delete"

        var dateText = document.createTextNode(" - " + time);

        usernameElement.appendChild(usernameText);
        usernameElement.appendChild(dateText);
        messageElement.appendChild(usernameElement);

        var textElement = document.createElement('p');

        // Ha van a szovegben URL akkor beteszi <a> tagba.
        textElement.innerHTML = (replaceURLs(content));

        messageElement.appendChild(textElement);

        if (messageUsername === username) {
            messageElement.appendChild(deleteButton);

            deleteButton.addEventListener('click', function () {
                deleteMessage(this.parentElement.getAttribute('id'))
            });
        }

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
    }
}

function displayEventMessage(message) {
    messageArea.innerHTML = ''
    var messageElement = document.createElement('li');
    messageElement.classList.add('event-message');

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function clearUnreads(username, receiver){
    fetch('/clearUnreads/'+username+'/'+receiver, {method: 'POST',
    })
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

    document.getElementById('notification-div').innerHTML = "";

    if (receiver === hypeBot) {
        displayEventMessage("If you don't know how to use me yet, use the .help command, and I'll let you know! :D")
    } else {
        if (receiver === group_chat) {
            displayAllMessages(group_chat, group_chat);
        } else {
            displayAllMessages(receiver, username);
        }
    }
    clearUnreads(username, receiver)
    closeNav();
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
    var giphy = /(https?\:\/\/)?((www\.)?giphy\.com|embed)\/.+$/g;

    return message.replace(urlRegex, function (url) {
        var hyperlink = url;
        if (!hyperlink.match('^https?:\/\/')) {
            hyperlink = 'http://' + hyperlink;
        }
        if (message.match(youtube)) {
            // Youtube Link Beagyazasa Videokent
            return '<a href="' + hyperlink + '" target="_blank">' + url + '</a><br><iframe width="560" height="315" src="' + hyperlink.replace("watch?v=", "embed/") + '" frameborder="0" allowfullscreen></iframe>'
        } else if (message.match(giphy)) {
            return '<a href="' + hyperlink + '" target="_blank">' + url + '</a><br><iframe width="560" height="315" src="' + hyperlink + '" frameborder="0" allowfullscreen></iframe>'
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

function displayAllFriends() {
    fetch('/friends/' + username, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {

            for (let i = 0; i < Object.keys(data).length; i++) {
                if (data[i] !== username && data[i] !== group_chat && data[i] !== "") {

                    var messageElement = document.createElement('li');
                    messageElement.classList.add('chat-message');
                    messageElement.addEventListener('click', function () {
                        getContactName(this)
                    });

                    messageElement.setAttribute('id', data[i])

                    var avatarElement = document.createElement('i');
                    var avatarText = document.createTextNode(data[i].substring(0, 1));
                    avatarElement.appendChild(avatarText);
                    avatarElement.style['background-color'] = getAvatarColor(data[i]);

                    messageElement.appendChild(avatarElement);

                    var usernameElement = document.createElement('span');
                    var usernameText = document.createTextNode(data[i]);

                    usernameElement.appendChild(usernameText);
                    messageElement.appendChild(usernameElement);

                    var textElement = document.createElement('p');

                    textElement.innerHTML = "No messages yet.";
                    textElement.style.fontSize = "0.8rem";

                    messageElement.appendChild(textElement);

                    contactArea.appendChild(messageElement);

                    displayLastMessages(data[i], username)
                }
            }
        });
}

function displayAllContacts() {
    fetch('/contacts', {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            for (let i = 0; i < Object.keys(data).length; i++) {
                if (data[i].userName !== username) {

                    var messageElement = document.createElement('li');
                    messageElement.classList.add('chat-message');
                    messageElement.addEventListener('click', function () {
                        getContactName(this)
                    });

                    messageElement.setAttribute('id', data[i].userName)

                    var avatarElement = document.createElement('i');
                    var avatarText = document.createTextNode(data[i].userName.substring(0, 1));
                    avatarElement.appendChild(avatarText);
                    avatarElement.style['background-color'] = getAvatarColor(data[i].userName);

                    messageElement.appendChild(avatarElement);

                    var usernameElement = document.createElement('span');
                    var usernameText = document.createTextNode(data[i].userName);

                    usernameElement.appendChild(usernameText);
                    messageElement.appendChild(usernameElement);

                    var textElement = document.createElement('p');

                    textElement.innerHTML = "No messages yet.";
                    textElement.style.fontSize = "0.8rem";

                    messageElement.appendChild(textElement);

                    contactArea.appendChild(messageElement);
                    displayLastMessages(data[i].userName, username)
                }
            }
        });
}

function displayAllMessages(msgreceiver, msgusername) {
    if (receiver === group_chat) {
        fetch('/listGroupMessages/' + msgreceiver, {
            method: 'GET',
        })
            .then((response) => response.json())
            .then((data) => {
                if (Object.keys(data).length === 0) {
                    displayEventMessage("There are no messages in this group. Send a message to " + receiver + " and fire up the conversation!")
                } else {
                    for (let i = 0; i < Object.keys(data).length; i++) {
                        displayMessage(data[i].sender, data[i].content, data[i].date, data[i].id)
                    }
                }
            });
    } else {
        fetch('/listMessages/' + msgreceiver + '/' + msgusername, {
            method: 'GET',
        })
            .then((response) => response.json())
            .then((data) => {
                if (Object.keys(data).length === 0) {
                    displayEventMessage("You don't have any messages with user " + receiver + ". Send them a message and fire up the conversation!")
                } else {
                    messageArea.innerHTML = "";

                    for (let i = 0; i < Object.keys(data).length; i++) {
                        displayMessage(data[i].sender, data[i].content, data[i].date, data[i].id)
                    }
                }
            });
    }
}

function displayLastMessages(receiver, username) {
    fetch('/lastMessage/' + receiver + '/' + username, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log(data)
            for (let i = 0; i < Object.keys(data).length; i++) {
                var msg = data[i].content
                if (msg.length > 20) {
                    msg = msg.substring(0, 20) + "..."
                }
                document.querySelectorAll('#' + receiver)[i].getElementsByTagName('p').item(0).innerText = data[i].sender.toUpperCase() + ': ' + msg
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

    document.getElementById('notification-div').innerHTML = '<label class="alert-message" id="notification-' + notificationId.toString() + '" onclick="getContactName(' + sender + ')">\n' +
        '    <strong class="hype"> <i class="fa fa-message"></i> NEW MESSAGE</strong><br><span id="notification-text">' + sender.toUpperCase() + ': ' + content + '</span>\n' +
        '    <button onclick="closeNotification(this)" class="close"><i class="fa fa-close"></i></button>\n' +
        '</label>'

    notificationAudio()

    setTimeout(function () {

        document.getElementById('notification-div').innerHTML = '<label class="hidden" id="notification-' + notificationId.toString() + '" onclick="getContactName(' + sender + ')">\n' +
            '    <strong class="hype"> <i class="fa fa-message"></i> NEW MESSAGE</strong><br><span id="notification-text">' + sender.toUpperCase() + ': ' + content + '</span>\n' +
            '    <button onclick="closeNotification(this)" class="close"><i class="fa fa-close"></i></button>\n' +
            '</label>'
    }, 5000)
}

function notificationAudio() {
    var audio = new Audio("Media/notification.mp3");
    audio.play();
}

function deleteMessages() {
    fetch('/deleteMessages/' + receiver + '/' + username, {
        method: 'POST',
    }).then(() => {
        displayAllMessages(receiver, username);
    })
}

function deleteMessage(messageId) {
    fetch('/deleteMessage/' + messageId, {
        method: 'POST',
    }).then(() => {
        displayAllMessages(receiver, username);
    })
}

function editMessage(message) {
    console.log(message.getElementsByClassName())
}

function searchButton() {
    document.getElementById('contacts-title').innerHTML = '<h2 id="contacts-title">All Users <span onclick="backButton()" style="cursor: pointer"><i class="fa-solid fa-arrow-left"></i></span></h2>'
    contactArea.innerHTML = ''
    displayAllContacts()
}

function backButton() {
    // Csak azokat mutatja, akik mar a kontakt listankon vannak, vagy mar beszelgettunk velunk
    document.getElementById('contacts-title').innerHTML = '<h2>Contacts <span onclick="searchButton()" style="cursor: pointer"><i class="fa-solid fa-magnifying-glass"></i></span></h2>'
    contactArea.innerHTML = '<h3 class="notice"><i class="fa-solid fa-thumbtack"></i> Pinned Contacts:</h3>\n' +
        '        <li class="chat-message" id="DuszaGroupChat"\n' +
        '            onclick="getContactName(this)">\n' +
        '            <i style="background-color: rgb(255,183,0);">D</i>\n' +
        '            <span>Dusza-Workshop Group</span>\n' +
        '            <p style= "font-size: 0.8rem">Chat with Dusza Members!</p>\n' +
        '        <li class="chat-message" id="hypeBot"\n' +
        '            onclick="getContactName(this)">\n' +
        '            <i class="fa-solid fa-robot hype2"></i>\n' +
        '            <span><span class="hype">HYPE</span>-Bot</span>\n' +
        '            <p style= "font-size: 0.8rem">Bip-Bup! Chat with me!</p>\n' +
        '        </li>' +
        '        <hr>'
    displayAllFriends()
}
