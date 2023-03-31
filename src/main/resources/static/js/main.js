'use strict';

var usernamePage = document.querySelector('#username-page');
var avatarPage = document.querySelector('#avatar-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    console.log('username>>>>>>>>>',document.getElementsByTagName('input')[0].value)
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        avatarPage.classList.remove('hidden');

        var socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);
    //订阅个人消息
    stompClient.subscribe(`/user/${username}/queue/justL`,getMessageByOtherUser);
    //点对点发送消息
    stompClient.subscribe(`/user/${username}/queue/chatToUser/res`,getMessageByMyself);
    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN',user:{toUserName: "L", sendUserName: username},})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = '无法连接到WebSocket服务器。请刷新此页再试!';
    connectingElement.style.color = 'red';
}

function getMessageByOtherUser(payload){
    console.log('getMessageByOtherUser>>>>>>>>>>>>>',payload);
    generateMsgElement(payload);
}
function getMessageByMyself(payload){
    console.log('getMessageByMyself>>>>>>>>>>>>>',payload);
    generateMsgElement(payload);
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        //如果当前用户是chc,发送消息到个人L
        if(username == 'chc'){
            var chatMessage = {
                user:{toUserName: "L", sendUserName: "chc"},
                sender: username,
                content: messageInput.value,
                type: 'CHAT'
            };
            stompClient.send("/app/chatToUser", {}, JSON.stringify(chatMessage));
            messageInput.value = '';
            event.preventDefault();
            return;
        }
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    generateMsgElement(payload);
}
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function generateMsgElement(payload){
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');
    var avatarElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
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
usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
