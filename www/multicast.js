var exec = cordova.require('cordova/exec');

// Events: 'message', 'error'
function Socket(type) {
    this._multicastSocket = type === 'multicast-udp4';
    this._socketId = ++Socket.socketCount;
    this._eventHandlers = { };
    Socket.sockets[this._socketId] = this;

    exec(null, null, 'Multicast', 'create', [ this._socketId, this._multicastSocket ]);
}

Socket.socketCount = 0;
Socket.sockets = { };

Socket.prototype.on = function (event, callback) {
    this._eventHandlers[event] = callback;
};

Socket.prototype.bind = function (port, callback) {
    callback = callback || function () { };
    exec(callback.bind(null, null), callback.bind(null), 'Multicast', 'bind', [ this._socketId, port ]);
};

Socket.prototype.close = function () {
    exec(null, null, 'Multicast', 'close', [ this._socketId ]);
    delete Socket.sockets[this._socketId];
    this._socketId = 0;
};

// sends utf-8
Socket.prototype.send = function (buffer, destAddress, destPort, callback) {
    callback = callback || function () { };
    exec(callback.bind(null, null), // success
         callback.bind(null), // failure
         'Multicast',
         'send',
         [ this._socketId, buffer, destAddress, destPort ]);
};

Socket.prototype.address = function () {
};

Socket.prototype.joinGroup = function (address, callback) {
    console.log('joinGroup');
    callback = callback || function () { };
    if (!this._multicastSocket) throw new Error('Invalid operation');
    exec(callback.bind(null, null), callback.bind(null), 'Multicast', 'joinGroup', [ this._socketId, address ]);
};

Socket.prototype.leaveGroup = function (address, callback) {
    callback = callback || function () { };
    if (!this._multicastSocket) throw new Error('Invalid operation');
    exec(callback.bind(null, null), callback.bind(null), 'Multicast', 'leaveGroup', [ this._socketId, address ]);
};

function createSocket(type) {
    console.log('createSocket');
    if (type !== 'udp4' && type !== 'multicast-udp4') {
        throw new Error('Illegal Argument, only udp4 and multicast-udp4 supported');
    }
    return new Socket(type);
}

function onMessage(id, msg, remoteAddress, remotePort) {
    console.log('onMessage');
    var socket = Socket.sockets[id];
    if (socket && 'message' in socket._eventHandlers) {
        socket._eventHandlers['message'].call(null, msg, { address: remoteAddress, port: remotePort });
    }
}

module.exports = {
    createSocket: createSocket,
    _onMessage: onMessage
}