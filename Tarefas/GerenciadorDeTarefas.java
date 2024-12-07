import java.util.*;

public class GerenciadorDeTarefas {

    static class Tarefa {
        int id;
        String titulo;
        String descricao;
        String dataVencimento;
        String prioridade;
        boolean concluida;

        public Tarefa(int id, String titulo, String descricao, String dataVencimento, String prioridade) {
            this.id = id;
            this.titulo = titulo;
            this.descricao = descricao;
            this.dataVencimento = dataVencimento;
            this.prioridade = prioridade;
            this.concluida = false;
        }

        public void marcarComoConcluida() {
            this.concluida = true;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Título: " + titulo + " | Descrição: " + descricao + 
                   " | Data: " + dataVencimento + " | Prioridade: " + prioridade + 
                   " | Status: " + (concluida ? "Concluída" : "Pendente");
        }
    }

    private static List<Tarefa> tarefas = new ArrayList<>();
    private static int idCounter = 1;

    public static void adicionarTarefa(String titulo, String descricao, String dataVencimento, String prioridade) {
        Tarefa tarefa = new Tarefa(idCounter++, titulo, descricao, dataVencimento, prioridade);
        tarefas.add(tarefa);
    }

    public static void removerTarefa(int id) {
        tarefas.removeIf(t -> t.id == id);
    }

    public static void listarTarefas() {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
        } else {
            for (Tarefa t : tarefas) {
                System.out.println(t);
            }
        }
    }

    public static void marcarComoConcluida(int id) {
        for (Tarefa t : tarefas) {
            if (t.id == id) {
                t.marcarComoConcluida();
                break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nGerenciador de Tarefas");
            System.out.println("1. Adicionar Tarefa");
            System.out.println("2. Remover Tarefa");
            System.out.println("3. Listar Tarefas");
            System.out.println("4. Marcar Tarefa como Concluída");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    System.out.print("Título: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Descrição: ");
                    String descricao = scanner.nextLine();
                    System.out.print("Data de Vencimento (dd/MM/yyyy): ");
                    String dataVencimento = scanner.nextLine();
                    System.out.print("Prioridade (alta, média, baixa): ");
                    String prioridade = scanner.nextLine();
                    adicionarTarefa(titulo, descricao, dataVencimento, prioridade);
                    break;

                case 2:
                    System.out.print("Digite o ID da tarefa a ser removida: ");
                    int idRemover = scanner.nextInt();
                    removerTarefa(idRemover);
                    break;

                case 3:
                    listarTarefas();
                    break;

                case 4:
                    System.out.print("Digite o ID da tarefa a ser marcada como concluída: ");
                    int idConcluir = scanner.nextInt();
                    marcarComoConcluida(idConcluir);
                    break;

                case 5:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
