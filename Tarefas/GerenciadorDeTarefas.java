import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GerenciadorDeTarefas {

    static class Tarefa implements Serializable {
        private static final long serialVersionUID = 1L;

        int id;
        String titulo;
        String descricao;
        LocalDate dataVencimento;
        String prioridade;
        boolean concluida;

        public Tarefa(int id, String titulo, String descricao, LocalDate dataVencimento, String prioridade) {
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

        public boolean isConcluida() {
            return this.concluida;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Título: " + titulo + " | Descrição: " + descricao +
                    " | Data: " + dataVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " | Prioridade: " + prioridade +
                    " | Status: " + (concluida ? "Concluída" : "Pendente");
        }
    }

    private static List<Tarefa> tarefas = new ArrayList<>();
    private static int idCounter = 1;
    private static final String ARQUIVO_TAREFAS = "tarefas.dat";

    public static void salvarTarefas() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_TAREFAS))) {
            oos.writeObject(tarefas);
            oos.writeInt(idCounter);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar tarefas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void carregarTarefas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_TAREFAS))) {
            tarefas = (List<Tarefa>) ois.readObject();
            idCounter = ois.readInt();
        } catch (FileNotFoundException e) {
            System.out.println("Nenhum arquivo de tarefas encontrado. Começando do zero.");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    public static void adicionarTarefa(String titulo, String descricao, String dataVencimento, String prioridade) {
        try {
            LocalDate data = LocalDate.parse(dataVencimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Tarefa tarefa = new Tarefa(idCounter++, titulo, descricao, data, prioridade);
            tarefas.add(tarefa);
            salvarTarefas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao adicionar tarefa: Verifique os dados inseridos.");
        }
    }

    public static void removerTarefa(int id) {
        if (tarefas.removeIf(t -> t.id == id)) {
            salvarTarefas();
            JOptionPane.showMessageDialog(null, "Tarefa removida com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Tarefa com ID " + id + " não encontrada.");
        }
    }

    public static List<Tarefa> listarTarefas() {
        return tarefas;
    }

    public static void marcarComoConcluida(int id) {
        for (Tarefa t : tarefas) {
            if (t.id == id) {
                t.marcarComoConcluida();
                salvarTarefas();
                JOptionPane.showMessageDialog(null, "Tarefa marcada como concluída.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Tarefa com ID " + id + " não encontrada.");
    }

    public static void main(String[] args) {
        carregarTarefas();
        SwingUtilities.invokeLater(() -> criarInterface());
    }

    public static void criarInterface() {
        JFrame frame = new JFrame("Gerenciador de Tarefas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel tarefasPanel = criarPainelTarefas();
        JPanel graficosPanel = criarPainelGraficos();

        tabbedPane.addTab("Tarefas", tarefasPanel);
        tabbedPane.addTab("Gráficos", graficosPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private static JPanel criarPainelTarefas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Título", "Descrição", "Data", "Prioridade", "Status"}, 0);
        JTable tabela = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);

        atualizarTabela(tableModel);

        JButton btnAdicionar = new JButton("Adicionar Tarefa");
        btnAdicionar.addActionListener(e -> {
            String titulo = JOptionPane.showInputDialog("Título:");
            String descricao = JOptionPane.showInputDialog("Descrição:");
            String dataVencimento = JOptionPane.showInputDialog("Data de Vencimento (dd/MM/yyyy):");
            String prioridade = JOptionPane.showInputDialog("Prioridade (alta, média, baixa):");
            adicionarTarefa(titulo, descricao, dataVencimento, prioridade);
            atualizarTabela(tableModel);
        });

        JButton btnRemover = new JButton("Remover Tarefa");
        btnRemover.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("ID da Tarefa a remover:");
            try {
                int id = Integer.parseInt(idStr);
                removerTarefa(id);
                atualizarTabela(tableModel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.");
            }
        });

        JButton btnConcluir = new JButton("Concluir Tarefa");
        btnConcluir.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("ID da Tarefa a concluir:");
            try {
                int id = Integer.parseInt(idStr);
                marcarComoConcluida(id);
                atualizarTabela(tableModel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.");
            }
        });

        panel.add(scrollPane);
        panel.add(btnAdicionar);
        panel.add(btnRemover);
        panel.add(btnConcluir);

        return panel;
    }

    private static JPanel criarPainelGraficos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton btnAtualizarGraficos = new JButton("Atualizar Gráficos");
        btnAtualizarGraficos.addActionListener(e -> {
            panel.removeAll();
            panel.add(btnAtualizarGraficos);
            panel.add(criarGraficoBarrasPrioridadeManual());
            panel.add(criarGraficoPizzaStatusManual());
            panel.revalidate();
            panel.repaint();
        });

        panel.add(btnAtualizarGraficos);
        panel.add(criarGraficoBarrasPrioridadeManual());
        panel.add(criarGraficoPizzaStatusManual());

        return panel;
    }

    private static JPanel criarGraficoBarrasPrioridadeManual() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Map<String, Long> contagemPrioridade = tarefas.stream()
                        .collect(Collectors.groupingBy(t -> t.prioridade, Collectors.counting()));

                int larguraBarra = 50;
                int espacamento = 20;
                int x = 50;

                int alturaMaxima = 200;
                long maiorValor = contagemPrioridade.values().stream().max(Long::compareTo).orElse(1L);

                g2d.drawString("Prioridade", getWidth() / 2 - 40, getHeight() - 20);
                g2d.drawString("Quantidade", 10, 30);

                for (Map.Entry<String, Long> entry : contagemPrioridade.entrySet()) {
                    String prioridade = entry.getKey();
                    long quantidade = entry.getValue();

                    int altura = (int) ((quantidade / (double) maiorValor) * alturaMaxima);
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(x, getHeight() - altura - 50, larguraBarra, altura);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, getHeight() - altura - 50, larguraBarra, altura);
                    g2d.drawString(prioridade, x + 5, getHeight() - 30);

                    x += larguraBarra + espacamento;
                }
            }
        };
    }

    private static JPanel criarGraficoPizzaStatusManual() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                long concluidas = tarefas.stream().filter(Tarefa::isConcluida).count();
                long pendentes = tarefas.stream().filter(t -> !t.isConcluida()).count();

                int total = tarefas.size();
                if (total == 0) total = 1; 

                int anguloConcluidas = (int) ((concluidas / (double) total) * 360);
                int anguloPendentes = 360 - anguloConcluidas;

                g2d.setColor(Color.GREEN);
                g2d.fillArc(50, 50, 200, 200, 0, anguloConcluidas);
                g2d.setColor(Color.RED);
                g2d.fillArc(50, 50, 200, 200, anguloConcluidas, anguloPendentes);

                g2d.setColor(Color.BLACK);
                g2d.drawString("Concluídas: " + concluidas, 300, 150);
                g2d.drawString("Pendentes: " + pendentes, 300, 170);
            }
        };
    }

    public static void atualizarTabela(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Tarefa tarefa : tarefas) {
            tableModel.addRow(new Object[]{
                    tarefa.id,
                    tarefa.titulo,
                    tarefa.descricao,
                    tarefa.dataVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    tarefa.prioridade,
                    tarefa.isConcluida() ? "Concluída" : "Pendente"
            });
        }
    }
}
