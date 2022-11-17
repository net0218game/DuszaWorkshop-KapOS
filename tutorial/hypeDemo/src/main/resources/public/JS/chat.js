var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var contactName = document.querySelector('.contactName');
var inputBox = document.querySelector('#message');

var stompClient = null;
var username = null;
var sessionId = "";
var receiver = "test"

const date = new Date();


var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];


function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Chat
    //stompClient.subscribe('/all/messages', onMessageReceived);
    stompClient.subscribe('/user/specific', onMessageReceived);

    /*stompClient.send("/app/application.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )*/
    fetch("/username").then((result) => console.log(result));

    connectingElement.classList.add('hidden');
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

        if (receiver !== username) {
            displayMessage(username, messageInput.value)
        }

        stompClient.send("/app/private", {}, JSON.stringify(chatMessage));
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
    } else if(message.type === 'SYSTEMMESSAGE') {
        messageElement.classList.add('event-message');
        message.content = message.content + ' This is a system message!';

    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function displayMessage(username, content) {
    var messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');

    var avatarElement = document.createElement('i');
    var avatarText = document.createTextNode(username[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(username);

    messageElement.appendChild(avatarElement);

    var usernameElement = document.createElement('span');
    var usernameText = document.createTextNode(username);
    var dateText = document.createTextNode(" - " + date.getHours() + ":" + date.getMinutes());

    usernameElement.appendChild(usernameText);
    usernameElement.appendChild(dateText);
    messageElement.appendChild(usernameElement);

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getContactName(contact) {
    // Torli az elozo uzeneteket
    messageArea.innerHTML = ""
    // Beallitja az uzenet fogadojat, es a contact cimet
    receiver = contact.id;
    contactName.innerHTML = receiver;
    // Beallitja az input box placeholder-jét
    inputBox.placeholder = "Send a message to " + receiver + "!"
    console.log(receiver)
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)