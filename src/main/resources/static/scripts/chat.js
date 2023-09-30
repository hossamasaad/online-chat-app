// Get DOM elements
const chatRoomsList = document.getElementById('chat-rooms');
const chatMessages = document.getElementById('chat-messages');
const friendInput = document.getElementById('friend-input');
const addFriendButton = document.getElementById('add-friend-button');
const messageInput = document.getElementById('message-input');
const sendButton = document.getElementById('send-button');

// Get token and user IDs from local storage
let token = localStorage.getItem("jwtToken");
let senderId = localStorage.getItem("id");
let receiverId = null;
let chatroomId = null;

// Load chat rooms from the server
async function loadChatRooms() {
    try {
        const response = await fetch('/api/chat-rooms', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        const chatRooms = await response.json();

        // Create list items for each chat room
        chatRooms.forEach(chatRoom => {
            const li = document.createElement('li');
            li.textContent = chatRoom.title;

            // Add click event listener to load messages and set receiver ID
            li.addEventListener('click', () => {
                chatroomId = chatRoom.id;
                loadMessages(chatRoom.id, chatRoom.title);
                [receiverId] = chatRoom.users.filter(item => item !== senderId);
            });

            chatRoomsList.appendChild(li);
        });
    } catch (error) {
        console.log('An error occurred while loading chat rooms:', error);
    }
}

// Add a friend to the user's friend list
async function addFriend() {
    const friendEmail = friendInput.value;
    if (friendEmail) {
        try {
            const response = await fetch(`/api/users/add-friend/${friendEmail}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("Friend Added Successfully");
            } else {
                throw new Error('Failed to add friend');
            }
        } catch (error) {
            console.log('An error occurred while adding a friend:', error);
        }
    }
}

// Load messages for a specific chat room
async function loadMessages(chatRoomId, receiverName) {
    try {
        const response = await fetch(`/api/chat-rooms/messages/${chatRoomId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        const messages = await response.json();

        // Clear chat messages
        chatMessages.innerHTML = '';

        // Display messages in the chat window
        messages.forEach(message => {
            if (message.senderId == senderId) {
                chatMessages.innerHTML += `Me: ${message.message} <br>`;
            } else {
                chatMessages.innerHTML += `${receiverName}: ${message.message} <br>`;
            }
        });
    } catch (error) {
        console.log('An error occurred while loading messages:', error);
    }
}

// Connect to the WebSocket server
function connectWebSocket() {
    const Sock = new SockJS('/ws');
    const stompClient = Stomp.over(Sock);
    stompClient.connect({}, onConnected, onError);

    function onConnected() {
        const url = `/user/${senderId}/private`;
        stompClient.subscribe(url, onMessageReceived);
    }

    function onError(err) {
        console.log(err);
    }

    function sendMessage() {
        const content = messageInput.value;
        const message = {
            'senderId': senderId,
            'receiverId': receiverId,
            'chatroomId': chatroomId,
            'message': content
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
    }

    function onMessageReceived(payload) {
        const message = JSON.parse(payload.body);
        if (message.senderId == senderId) {
            chatMessages.innerHTML += `Me: ${message.message}<br>`;
        } else {
            chatMessages.innerHTML += `${receiverName}: ${message.message}<br>`;
        }
    }

    sendButton.addEventListener('click', sendMessage);
}

// Load chat rooms, add friend event listener, and connect to WebSocket
loadChatRooms();
addFriendButton.addEventListener('click', addFriend);
connectWebSocket();
