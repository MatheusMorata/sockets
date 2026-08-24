#include <iostream>
#include <cstring>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

int main(){
    sockaddr_in serverAddress;
    int serverSocket, clientSocket;

    // Criando o socket
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    
    // Definindo endereço
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(8080);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    std::cout <<  "Servidor iniciando" << std::endl;

    bind(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress));

    std::cout << "Ouvindo em: " << serverAddress.sin_addr.s_addr << ":" << serverAddress.sin_port;
    listen(serverSocket, 5);

    clientSocket = accept(serverSocket, nullptr, nullptr);

    char buffer[1024] = { 0 };
    recv(clientSocket, buffer, sizeof(buffer), 0);
    std::cout << "Message from client: " << buffer << std::endl;

    close(serverSocket);

    return 0;
}