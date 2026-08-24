local socket = require 'socket'
local json = require("dkjson")

local address = '127.0.0.1'
local port = 8080
local opcao, valor = nil
local cliente = socket.tcp()
local pacote = {tipo = nil, val = nil}

local ok = cliente:connect(address, port)
print('Conectando ao servidor...')

if ok then
    print('Conectado: ' .. address .. ':' .. port)
    while true then
        print('=============================')
        print('[1] - Inteiro ' .. '\n[2] - Caractere' .. '\n[3] - Cadeia de carecteres')
        print('=============================')
        opcao = io.read()
        
        if opcao == 1 then
            pacote.tipo = 'int'
            print('Valor: ')
            valor = io.read()
        elseif opcao == 2 then
            pacote.tipo = 'char'
            print('Valor: ')
            valor = io.read()
        elseif opcao == 3 then
            pacote.tipo = 'string'
            print('Valor: ')
            valor = io.read()
        end 

        pacote = json.encode(pacote) 
        cliente:send(pacote)
    end

    cliente:close()
else
    print('Não conectado')
end