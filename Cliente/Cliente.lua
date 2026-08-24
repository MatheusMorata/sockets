local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080

local client = socket.tcp()

local ok, err = client:connect(address, port)

if not ok then
    print("Erro ao conectar:", err)
else
    print("Conectado!")
end