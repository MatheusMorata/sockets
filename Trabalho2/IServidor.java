import java.rmi.Remote;
import java.rmi.RemoteException;

/** Métodos que o Jogador pode chamar no Servidor. */
public interface IServidor extends Remote {

    /** Registra o jogador. Retorna o id (0 = X, 1 = O) ou -1 se o servidor estiver cheio. */
    int login(IJogador jogador, String nome) throws RemoteException;

    void jogar(int id, int celula) throws RemoteException;

    void reiniciar(int id) throws RemoteException;

    void sair(int id) throws RemoteException;
}
