local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080
local pacote = nil
local cliente = socket.tcp()

local ok = cliente:connect(address, port)
print('Conectando ao servidor...')

if ok then
    print('Conectado: ' .. address .. ':' .. port)
    print('======================================')
    print('[1] - Inteiro ' .. '\n[2] - Caractere' .. '\n[3] - Cadeia de carecteres')
    print('======================================')
    cliente:send(pacote)
    cliente:close()
else
    print('Não conectado')
end