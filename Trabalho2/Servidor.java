import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Servidor RMI: guarda o estado do jogo e avisa os jogadores via callback. */
public class Servidor implements IServidor {


    private static final int[][] LINHAS = {
            {0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}
    };

    private interface Chamada { void executar(IJogador j) throws RemoteException; }

    private final IJogador[] jogadores = new IJogador[2];
    private final String[] nomes = new String[2];
    private final char[] tabuleiro = new char[9];
    private char vez = 'X';
    private String resultado = "";

    // Fila única: garante que os callbacks chegam na ordem certa e sem travar o servidor
    private final ExecutorService fila = Executors.newSingleThreadExecutor();

    // ------------------------------------------------------------ remoto
    @Override
    public synchronized int login(IJogador jogador, String nome) {
        int id = jogadores[0] == null ? 0 : (jogadores[1] == null ? 1 : -1);
        if (id == -1) {
            System.out.println("Login recusado (servidor cheio): " + nome);
            return -1;
        }
        jogadores[id] = jogador;
        nomes[id] = nome;
        System.out.println("Logou: " + nome + " (" + simbolo(id) + ")");

        if (jogadores[0] != null && jogadores[1] != null) {
            resetar();
            for (int i = 0; i < 2; i++) {
                final int eu = i;
                final String nomeAdversario = nomes[1 - i];
                chamar(eu, j -> j.iniciar(simbolo(eu), nomeAdversario));
            }
            enviarEstado();
        }
        return id;
    }

    @Override
    public synchronized void jogar(int id, int celula) {
        if (jogadores[0] == null || jogadores[1] == null) return;
        if (!resultado.isEmpty()) return;
        if (celula < 0 || celula > 8 || tabuleiro[celula] != ' ') return;
        if (simbolo(id) != vez) return;

        tabuleiro[celula] = vez;
        char v = vencedor();
        if (v != ' ') resultado = String.valueOf(v);
        else if (cheio()) resultado = "EMPATE";
        else vez = (vez == 'X') ? 'O' : 'X';

        enviarEstado();
    }

    @Override
    public synchronized void reiniciar(int id) {
        if (resultado.isEmpty()) return;     // só reinicia depois do fim
        resetar();
        enviarEstado();
    }

    @Override
    public synchronized void sair(int id) {
        if (id < 0 || id > 1 || jogadores[id] == null) return;
        System.out.println("Saiu: " + nomes[id]);
        jogadores[id] = null;
        nomes[id] = null;
        resetar();
        chamar(1 - id, IJogador::adversarioSaiu);
    }

    // ----------------------------------------------------------- interno
    private char simbolo(int id) { return id == 0 ? 'X' : 'O'; }

    private void resetar() {
        Arrays.fill(tabuleiro, ' ');
        vez = 'X';
        resultado = "";
    }

    private void enviarEstado() {
        final String t = new String(tabuleiro);
        final char v = vez;
        final String r = resultado;
        for (int i = 0; i < 2; i++) chamar(i, j -> j.atualizar(t, v, r));
    }

    /** Executa o callback em segundo plano; se o jogador caiu, remove-o. */
    private void chamar(int i, Chamada c) {
        final IJogador j = jogadores[i];
        if (j == null) return;
        fila.submit(() -> {
            try {
                c.executar(j);
            } catch (RemoteException e) {
                System.out.println("Jogador " + i + " não responde; removendo.");
                synchronized (Servidor.this) {
                    if (jogadores[i] == j) sair(i);
                }
            }
        });
    }

    private char vencedor() {
        for (int[] l : LINHAS) {
            char a = tabuleiro[l[0]];
            if (a != ' ' && a == tabuleiro[l[1]] && a == tabuleiro[l[2]]) return a;
        }
        return ' ';
    }

    private boolean cheio() {
        for (char c : tabuleiro) if (c == ' ') return false;
        return true;
    }

    // -------------------------------------------------------------- start
    public static void iniciar() throws Exception {
        Servidor servidor = new Servidor();
        IServidor stub = (IServidor) UnicastRemoteObject.exportObject(servidor, 0);
        Registry registry = LocateRegistry.createRegistry(main.PORTA);
        registry.rebind(main.NOME_SERVICO, stub);
        System.out.println("Servidor do Jogo da Velha pronto na porta " + main.PORTA + ". Aguardando jogadores...");
    }
}
