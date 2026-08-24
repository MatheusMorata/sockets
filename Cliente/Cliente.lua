local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080
local pacote = 'Teste'
local client = socket.tcp()

local ok = client:connect(address, port)
print('Conectado ao servidor')

if ok then
    print('Conectado: ' .. address .. port)
    client:send(pacote)
    client:close()
else
    print('Não conectado')
end