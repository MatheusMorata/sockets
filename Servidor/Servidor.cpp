#include <iostream>
#include <cstring>
#include <string>
#include <algorithm>
#include <cctype>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

// Função que procura e retorna o valor de uma chave dentro de um JSON simples.
// Exemplo:
// JSON:   {"tipo":"int","val":"25"}
// chave:  "tipo"
// retorno: "int"
std::string pegarValor(
    const std::string& json,
    const std::string& chave
) {
    std::string busca = "\"" + chave + "\":\"";

    size_t inicio = json.find(busca);

    if (inicio == std::string::npos) {
        return "";
    }

    inicio += busca.length();

    size_t fim = json.find("\"", inicio);

    if (fim == std::string::npos) {
        return "";
    }

    return json.substr(inicio, fim - inicio);
}

int main() {

    sockaddr_in serverAddress{};
    int serverSocket, clientSocket, bytesReceived;
    char buffer[1024] = {0};

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);

    // Definindo endereço
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(8080);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    std::cout << "Servidor iniciando..." << std::endl;
    bind(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress));


    std::cout << "Ouvindo em 127.0.0.1:8080" << std::endl;
    listen(serverSocket, 5);

    clientSocket = accept(serverSocket, nullptr, nullptr);
    std::cout << "Cliente conectado" << std::endl;


    bytesReceived = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

    if (bytesReceived > 0) {

        buffer[bytesReceived] = '\0';

        std::cout << "Mensagem do cliente: "
                  << buffer
                  << std::endl;

        std::string mensagem(buffer);

        std::string tipo = pegarValor(mensagem, "tipo");
        std::string valor = pegarValor(mensagem, "val");

        std::string resposta;

        if (tipo == "int") {

            int numero = std::stoi(valor);

            numero++;

            resposta = std::to_string(numero);
        }

        else if (tipo == "char") {

            if (!valor.empty()) {

                char caractere = valor[0];

                if (std::islower(
                    static_cast<unsigned char>(caractere)
                )) {
                    caractere = std::toupper(
                        static_cast<unsigned char>(caractere)
                    );
                }
                else if (std::isupper(
                    static_cast<unsigned char>(caractere)
                )) {
                    caractere = std::tolower(
                        static_cast<unsigned char>(caractere)
                    );
                }

                resposta = caractere;
            }
        }

        else if (tipo == "string") {

            resposta = valor;

            std::reverse(
                resposta.begin(),
                resposta.end()
            );
        }

        else {
            resposta = "Tipo desconhecido";
        }

        std::string respostaJson = "{\"tipo\":\"" + tipo + "\",\"val\":\"" + resposta + "\"}";

        send(clientSocket, respostaJson.c_str(), respostaJson.size(), 0);

        std::cout << "Resposta enviada: " << respostaJson << std::endl;
    }

    close(clientSocket);
    close(serverSocket);

    return 0;
}