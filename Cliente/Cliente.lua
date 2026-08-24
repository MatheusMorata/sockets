local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080
local client = socket.tcp()

local ok = client:connect(address, port)

if ok then
    print('Conectado ao servidor')
else
    print('Não conectado')
end