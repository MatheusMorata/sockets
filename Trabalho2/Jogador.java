import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Jogador extends JFrame implements IJogador {

    private IServidor servidor;
    private int meuId = -1;
    private char meuSimbolo = ' ';
    private boolean emAndamento = false;
    private boolean minhaVez = false;

    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);
    private final JButton[] botoes = new JButton[9];
    private final JLabel status = new JLabel("", SwingConstants.CENTER);
    private final JButton btnReiniciar = new JButton("Nova partida");
    private final JLabel lblEspera = new JLabel("", SwingConstants.CENTER);

    public Jogador(String hostServidor) {
        super("Jogo da Velha RMI");
        montarTelaEspera();
        montarTelaJogo();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { encerrar(); }
        });
        setSize(360, 440);
        setLocationRelativeTo(null);
        add(raiz);

        String nome = JOptionPane.showInputDialog(null, "Seu nome:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (nome == null || nome.isBlank()) System.exit(0);

        try {
            Registry registry = LocateRegistry.getRegistry(hostServidor, main.PORTA);
            servidor = (IServidor) registry.lookup(main.NOME_SERVICO);

            IJogador stub = (IJogador) UnicastRemoteObject.exportObject(this, 0);
            meuId = servidor.login(stub, nome.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível conectar ao servidor " + hostServidor + ":\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if (meuId == -1) {
            JOptionPane.showMessageDialog(null, "O servidor já tem dois jogadores.", "Cheio",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        lblEspera.setText("<html><center>Olá, <b>" + nome.trim()
                + "</b>!<br><br>Aguardando o outro jogador...</center></html>");
        cartas.show(raiz, "espera");
        setVisible(true);
    }
    
    private void montarTelaEspera() {
        JPanel p = new JPanel(new GridBagLayout());
        lblEspera.setFont(lblEspera.getFont().deriveFont(15f));
        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(10, 10, 10, 10);
        p.add(lblEspera, c);
        c.gridy = 1; c.fill = GridBagConstraints.HORIZONTAL;
        p.add(barra, c);
        raiz.add(p, "espera");
    }

    private void montarTelaJogo() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        status.setFont(status.getFont().deriveFont(Font.BOLD, 15f));
        p.add(status, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(3, 3, 4, 4));
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            JButton b = new JButton("");
            b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 56));
            b.setFocusPainted(false);
            b.addActionListener(e -> aoClicar(idx));
            botoes[i] = b;
            grade.add(b);
        }
        p.add(grade, BorderLayout.CENTER);

        btnReiniciar.setVisible(false);
        btnReiniciar.addActionListener(e -> chamarServidor(() -> servidor.reiniciar(meuId)));
        p.add(btnReiniciar, BorderLayout.SOUTH);

        raiz.add(p, "jogo");
    }

    private void aoClicar(int i) {
        if (!emAndamento || !minhaVez) return;
        chamarServidor(() -> servidor.jogar(meuId, i));
    }

    private interface ChamadaServidor { void executar() throws RemoteException; }

    /** Chamadas ao servidor fora da thread da interface, para não congelar a janela. */
    private void chamarServidor(ChamadaServidor c) {
        new Thread(() -> {
            try {
                c.executar();
            } catch (RemoteException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Conexão com o servidor perdida.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                });
            }
        }).start();
    }

    private void encerrar() {
        try { if (servidor != null) servidor.sair(meuId); } catch (Exception ignored) { }
        System.exit(0);
    }

    // ------------------------------------------- callbacks (chamados pelo servidor)
    @Override
    public void iniciar(char simbolo, String nomeAdversario) {
        SwingUtilities.invokeLater(() -> {
            meuSimbolo = simbolo;
            setTitle("Jogo da Velha - Você é " + simbolo + " contra " + nomeAdversario);
            cartas.show(raiz, "jogo");
        });
    }

    @Override
    public void atualizar(String tabuleiro, char vez, String resultado) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                char c = tabuleiro.charAt(i);
                botoes[i].setText(c == ' ' ? "" : String.valueOf(c));
                botoes[i].setForeground(c == 'X' ? new Color(0x1565C0) : new Color(0xC62828));
                botoes[i].setEnabled(c == ' ' && resultado.isEmpty());
            }

            emAndamento = resultado.isEmpty();
            minhaVez = emAndamento && vez == meuSimbolo;
            btnReiniciar.setVisible(!emAndamento);

            if (emAndamento) {
                status.setText(minhaVez ? "Sua vez (" + meuSimbolo + ")" : "Aguardando o adversário...");
            } else if (resultado.equals("EMPATE")) {
                status.setText("Empate!");
            } else {
                status.setText(resultado.charAt(0) == meuSimbolo ? "Você venceu!" : "Você perdeu!");
            }
        });
    }

    @Override
    public void adversarioSaiu() {
        SwingUtilities.invokeLater(() -> {
            setTitle("Jogo da Velha RMI");
            lblEspera.setText("<html><center>O adversário saiu.<br><br>Aguardando outro jogador...</center></html>");
            cartas.show(raiz, "espera");
        });
    }
}
