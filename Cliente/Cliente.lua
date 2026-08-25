local socket = require 'socket'

local address = '127.0.0.1'
local port = 8080
local opcao = nil
local cliente = socket.tcp()
local pacote = {tipo = nil, val = nil}

local ok, erro = cliente:connect(address, port)
print('Conectando ao servidor...')

if ok then

    print('Conectado: ' .. address .. ':' .. port)

    while true do

        print('=============================')
        print('[1] - Inteiro')
        print('[2] - Caractere')
        print('[3] - Cadeia de caracteres')
        print('[0] - Sair')
        print('=============================')

        io.write('Opção: ')
        opcao = io.read()

        if opcao == '0' then
            break
        end

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

            local resultado = string.format('{"tipo":"%s","val":"%s"}', pacote.tipo, pacote.val)

            local enviado, erroEnvio = cliente:send(resultado .. '\n')

            if enviado then

                local resposta, erroRecebimento = cliente:receive('*l')

                if resposta then
                    print('Resposta do servidor: ' .. resposta)
                else
                    print('Erro ao receber: ' .. tostring(erroRecebimento))
                    break
                end

            else

                print('Erro ao enviar: ' .. tostring(erroEnvio))
                break

            end
        end
    end

    cliente:close()
    print('Conexão encerrada.')

else

    print('Não conectado: ' .. tostring(erro))

end