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
        print('[1] - Inteiro')
        print('[2] - Caractere')
        print('[3] - Cadeia de caracteres')
        print('=============================')

        opcao = io.read()

        local valido = false

        if opcao == '1' then
            io.write('Valor: ')
            local valor = io.read()

            if tonumber(valor) and valor:match('^-?%d+$') then
                pacote.tipo = 'int'
                pacote.val = valor
                valido = true
            else
                print('Valor inválido! Digite apenas um número inteiro.')
            end

        elseif opcao == '2' then
            io.write('Valor: ')
            local valor = io.read()

            if #valor == 1 then
                pacote.tipo = 'char'
                pacote.val = valor
                valido = true
            else
                print('Valor inválido! Digite apenas um caractere.')
            end

        elseif opcao == '3' then
            io.write('Valor: ')
            pacote.tipo = 'string'
            pacote.val = io.read()
            valido = true

        else
            print('Opção inválida!')
        end

        if valido then
            local resultado = string.format(
                '{"tipo":"%s","val":"%s"}',
                pacote.tipo,
                pacote.val
            )

            cliente:send(resultado)
        end
    end

    cliente:close()
else
    print('Não conectado')
end