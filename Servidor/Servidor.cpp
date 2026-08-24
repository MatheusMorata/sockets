#include <iostream>

int main(){
    sockaddr_in serverAddress;
    int serverSocket, clientSocket;

    // Criando o socket
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    
    // Definindo endereço
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(8080);
    serverAddress.sin_addr.s_addr = INADDR_ANY;
    
    bind(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress));

    listen(serverSocket, 5);

    clientSocket = accept(serverSocket, nullptr, nullptr);

    close(serverSocket);

    return 0;
}