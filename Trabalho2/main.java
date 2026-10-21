import javax.swing.SwingUtilities;

public class main {

    public static final String IP_SERVIDOR = "localhost";
    public static final String IP_JOGADOR = "localhost";
    public static final int PORTA = 1099;
    public static final String NOME_SERVICO = "JogoDaVelha";

    // =========================================================================

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java Main servidor | java Main jogador");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "servidor" -> {
                // IP que o servidor anuncia nos stubs enviados aos clientes
                System.setProperty("java.rmi.server.hostname", IP_SERVIDOR);
                Servidor.iniciar();
            }
            case "jogador" -> {
                // IP que o jogador anuncia para o servidor poder chamá-lo de volta
                System.setProperty("java.rmi.server.hostname", IP_JOGADOR);
                SwingUtilities.invokeLater(() -> new Jogador(IP_SERVIDOR));
            }
            default -> System.out.println("Argumento inválido: use 'servidor' ou 'jogador'.");
        }
    }
}
