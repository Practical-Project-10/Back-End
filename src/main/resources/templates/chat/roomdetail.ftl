<!doctype html>
<html lang="en">
<head>
    <title>Websocket ChatRoom</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="container" id="app" v-cloak>
    <div>
        <h2>{{room.name}}</h2>
    </div>
    <div class="input-group">
        <div class="input-group-prepend">
            <label class="input-group-text">내용</label>
        </div>
        <input type="text" class="form-control" v-model="message" @keyup.enter="sendMessage">
        <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="sendMessage">보내기</button>
        </div>
    </div>
    <ul class="list-group">
        <li class="list-group-item" v-for="message in messages">
            {{message.sender}} - {{message.message}}</a>
        </li>
    </ul>
    <div></div>
</div>
<!-- JavaScript -->
<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
<script src="/webjars/bootstrap/4.3.1/dist/js/bootstrap.min.js"></script>
<script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
<script>
    // websocket & stomp initialize
    var sock = new SockJS("/ws-stomp");
    var ws = Stomp.over(sock);
    // vue.js
    var vm = new Vue({
        el: '#app',
        data: {
            roomId: '',
            room: {},
            sender: '',
            message: '',
            messages: [],
            token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJFWFBJUkVEX0RBVEUiOjE2NDA2MDA3NTksImlzcyI6InNwYXJ0YSIsIlVTRVJfTkFNRSI6ImJlb21pbjEyMyJ9.UxhFUln3xQmER4A_hPQtorxFUSe7Hw685Nd9FqMXy6w'
        },
        created() {
            this.roomId = localStorage.getItem('wschat.roomId');
            // this.sender = localStorage.getItem('wschat.sender');
            this.findRoom();
        },
        methods: {
            findRoom: function() {
                axios.get('/chat/room/'+this.roomId).then(response => { this.room = response.data; });
            },
            sendMessage: function() {
                ws.send("/pub/chat/message",{"token":this.token},JSON.stringify({type:'TALK', roomId:this.roomId, sender:this.sender, message:this.message}));
                this.message = '';
            },
            recvMessage: function(recv) {
                this.messages.unshift({"type":recv.type,"sender":recv.type=='ENTER'?'[알림]':recv.sender,"message":recv.message})
            },
            getPreviousMessages: function() {
                axios.get('/chat/message/' + this.roomId).then(response => {
                    this.messages = [...response.data, ...this.messages]
                });
            }
        }
    });
    // pub/sub event
    ws.connect({"token":vm.$data.token}, function(frame) {
        ws.subscribe("/sub/chat/room/"+vm.$data.roomId, (message) => {
            var recv = JSON.parse(message.body);
            vm.recvMessage(recv);
            console.log({ recv });
        });
        ws.send("/pub/chat/message",{token:vm.$data.token} ,JSON.stringify({type:'ENTER', roomId:vm.$data.roomId, sender:vm.$data.sender}));
        vm.getPreviousMessages();
    }, function(error) {
        alert("error "+error);
    });
</script>
</body>
</html>