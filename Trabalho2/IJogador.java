import java.rmi.Remote;
import java.rmi.RemoteException;

/** Métodos que o Servidor chama de volta no Jogador (callback). */
public interface IJogador extends Remote {

    /** Os dois jogadores estão logados: abre o tabuleiro. */
    void iniciar(char simbolo, String nomeAdversario) throws RemoteException;

    /**
     * Estado atual do jogo.
     * @param tabuleiro 9 caracteres ('X', 'O' ou ' ')
     * @param vez       de quem é a vez ('X' ou 'O')
     * @param resultado "" (em andamento), "X", "O" ou "EMPATE"
     */
    void atualizar(String tabuleiro, char vez, String resultado) throws RemoteException;

    /** O adversário saiu: volta para a tela de espera. */
    void adversarioSaiu() throws RemoteException;
}
