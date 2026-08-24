local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080
local opcao = nil
local cliente = socket.tcp()
local pacote = {tipo = nil, val = nil}

local ok = cliente:connect(address, port)
print('Conectando ao servidor...')

if ok then
    print('Conectado: ' .. address .. ':' .. port)
    while true do
        print('=============================')
        print('[1] - Inteiro ' .. '\n[2] - Caractere' .. '\n[3] - Cadeia de carecteres')
        print('=============================')
        opcao = io.read()
        
        if opcao == '1' then
            pacote.tipo = 'int'
            print('Valor: ')
            pacote.val = io.read()
        elseif opcao == '2' then
            pacote.tipo = 'char'
            print('Valor: ')
            pacote.val = io.read()
        elseif opcao == '3' then
            pacote.tipo = 'string'
            print('Valor: ')
            pacote.val = io.read()
        end 

        local resultado = string.format(
            '{"tipo":"%s","val":"%s"}',
            pacote.tipo, pacote.val)
        
        cliente:send(resultado)
    end

    cliente:close()
else
    print('Não conectado')
end