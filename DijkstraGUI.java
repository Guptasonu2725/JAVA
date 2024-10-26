import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DijkstraGUI extends JFrame {

    private JTextField verticesField, edgesField, sourceField;
    private JTextArea edgesInputArea, resultArea;
    private JButton computeButton;
    private int V, E;
    private int[][] graph;
    private GraphPanel graphPanel; // Panel to display the graph

    public DijkstraGUI() {
        // Setting up the frame
        setTitle("Dijkstra's Algorithm Visualizer");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new GridBagLayout());

        // Set background color to light blue
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        // Input for number of vertices
        JPanel verticesPanel = new JPanel();
        verticesPanel.add(new JLabel("Enter number of vertices:"));
        verticesField = new JTextField(5);
        verticesPanel.add(verticesField);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(verticesPanel, gbc);

        // Input for number of edges
        JPanel edgesPanel = new JPanel();
        edgesPanel.add(new JLabel("Enter number of edges:"));
        edgesField = new JTextField(5);
        edgesPanel.add(edgesField);
        gbc.gridy = 1;
        add(edgesPanel, gbc);

        // Text area to input edges
        JPanel edgesInputPanel = new JPanel();
        edgesInputPanel.setLayout(new BorderLayout());
        edgesInputPanel.add(new JLabel("Enter edges (Format : Source, Destination, Weight):"), BorderLayout.NORTH);
        edgesInputArea = new JTextArea(5, 30);
        edgesInputArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        edgesInputPanel.add(new JScrollPane(edgesInputArea), BorderLayout.CENTER);
        gbc.gridy = 2;
        add(edgesInputPanel, gbc);

        // Input for source vertex
        JPanel sourcePanel = new JPanel();
        sourcePanel.add(new JLabel("Enter source vertex:"));
        sourceField = new JTextField(5);
        sourcePanel.add(sourceField);
        gbc.gridy = 3;
        add(sourcePanel, gbc);

        // Button to compute shortest path
        computeButton = new JButton("Compute");
        computeButton.setPreferredSize(new Dimension(150, 30)); // Set shorter width
        computeButton.setBackground(new Color(60, 179, 113)); // Green button
        computeButton.setForeground(Color.WHITE); // White text
        gbc.gridy = 4;
        add(computeButton, gbc);

        // Text area to display result
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridy = 5;
        add(new JScrollPane(resultArea), gbc);

        // Graph panel to visualize the graph
        graphPanel = new GraphPanel();
        gbc.gridy = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(graphPanel, gbc);

        // Action listener for the compute button
        computeButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    // Input validation
                    V = Integer.parseInt(verticesField.getText());
                    E = Integer.parseInt(edgesField.getText());

                    if (V <= 0 || E <= 0 || E > (V * (V - 1)) / 2) {
                        JOptionPane.showMessageDialog(null, "Invalid number of vertices or edges. Ensure that edges are less than or equal to V*(V-1)/2.");
                        return;
                    }

                    int source = Integer.parseInt(sourceField.getText());
                    if (source < 0 || source >= V) {
                        JOptionPane.showMessageDialog(null, "Invalid source vertex. It should be between 0 and " + (V - 1) + ".");
                        return;
                    }

                    graph = new int[V][V];
                    String[] edgesInput = edgesInputArea.getText().split("\n");

                    if (edgesInput.length != E) {
                        JOptionPane.showMessageDialog(null, "Please enter exactly " + E + " edges.");
                        return;
                    }

                    for (String edge : edgesInput) {
                        String[] parts = edge.trim().split(" ");
                        if (parts.length != 3) {
                            JOptionPane.showMessageDialog(null, "Each edge should have format: src dest weight.");
                            return;
                        }
                        int src = Integer.parseInt(parts[0]);
                        int dest = Integer.parseInt(parts[1]);
                        int weight = Integer.parseInt(parts[2]);

                        if (src < 0 || src >= V || dest < 0 || dest >= V || weight <= 0) {
                            JOptionPane.showMessageDialog(null, "Invalid edge values. Vertices should be between 0 and " + (V - 1) + " and weight should be positive.");
                            return;
                        }

                        graph[src][dest] = weight;
                        graph[dest][src] = weight; // For undirected graph
                    }

                    // Call Dijkstra to compute paths
                    int[] dist = new int[V];
                    int[] previous = new int[V];
                    dijkstra(graph, source, V, dist, previous);
                    displayResult(dist, previous, source);

                    // Update the graph panel with the computed paths
                    graphPanel.setGraph(graph, previous, source);
                    graphPanel.repaint(); // Refresh the panel

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for vertices, edges, and source.");
                }
            }
        });
    }

    // Dijkstra's algorithm method
    private void dijkstra(int[][] graph, int src, int V, int[] dist, int[] previous) {
        boolean[] visited = new boolean[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(previous, -1); // No previous node at the start
        dist[src] = 0;

        for (int i = 0; i < V - 1; i++) {
            int u = minDistance(dist, visited, V);
            visited[u] = true;

            for (int v = 0; v < V; v++) {
                if (!visited[v] && graph[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                    previous[v] = u;
                }
            }
        }
    }

    // Helper method to find the minimum distance vertex
    private int minDistance(int[] dist, boolean[] visited, int V) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < V; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    // Display the result in the text area
    private void displayResult(int[] dist, int[] previous, int src) {
        StringBuilder result = new StringBuilder();
        result.append("Vertex\tDistance from Source\t\tPath\n");
        for (int i = 0; i < V; i++) {
            result.append(i).append("\t\t").append(dist[i] == Integer.MAX_VALUE ? "INF" : dist[i]).append("\t\t");
            if (dist[i] == Integer.MAX_VALUE) {
                result.append("-\n");
            } else {
                result.append(getPath(i, previous)).append("\n");
            }
        }
        resultArea.setText(result.toString());
    }

    // Recursive method to get the path from source to destination
    private String getPath(int vertex, int[] previous) {
        if (previous[vertex] == -1) {
            return String.valueOf(vertex);
        }
        return getPath(previous[vertex], previous) + " -> " + vertex;
    }

    // GraphPanel to visualize the graph
    class GraphPanel extends JPanel {
        private int[][] graph;
        private int[] previous;
        private int source;

        public void setGraph(int[][] graph, int[] previous, int source) {
            this.graph = graph;
            this.previous = previous;
            this.source = source;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (graph == null) return;
        
            int radius = 20;
            int panelWidth = getWidth(); // Get panel width
            int panelHeight = getHeight(); // Get panel height
            int centerX = panelWidth / 2; // X-coordinate of center
            int centerY = panelHeight / 2; // Y-coordinate of center
            int vertexRadius = 100; // Radius of the circle on which vertices will be placed
            int vertexX[] = new int[V]; // X-coordinates of the vertices
            int vertexY[] = new int[V]; // Y-coordinates of the vertices
        
            // Calculate positions of the vertices on a circle
            for (int i = 0; i < V; i++) {
                double angle = 2 * Math.PI * i / V; // Angle for each vertex
                vertexX[i] = (int) (centerX + vertexRadius * Math.cos(angle)); // X-coordinate
                vertexY[i] = (int) (centerY + vertexRadius * Math.sin(angle)); // Y-coordinate
            }
        
            // Draw edges between vertices
            g.setColor(Color.GRAY);
            for (int i = 0; i < V; i++) {
                for (int j = i + 1; j < V; j++) {
                    if (graph[i][j] != 0) {
                        g.drawLine(vertexX[i], vertexY[i], vertexX[j], vertexY[j]); // Draw edge
                        String weight = String.valueOf(graph[i][j]);
                        int midX = (vertexX[i] + vertexX[j]) / 2; // X-coordinate of midpoint
                        int midY = (vertexY[i] + vertexY[j]) / 2; // Y-coordinate of midpoint
                        g.drawString(weight, midX, midY); // Draw weight at midpoint
                    }
                }
            }
        
            // Highlight shortest path edges in red
            g.setColor(Color.RED);
            for (int i = 0; i < V; i++) {
                if (previous[i] != -1) {
                    g.drawLine(vertexX[i], vertexY[i], vertexX[previous[i]], vertexY[previous[i]]);
                }
            }
        
            // Draw vertices
            for (int i = 0; i < V; i++) {
                g.setColor(Color.BLUE); // Vertex color
                g.fillOval(vertexX[i] - radius / 2, vertexY[i] - radius / 2, radius, radius); // Draw vertex as a filled oval
                g.setColor(Color.WHITE); // Vertex label color
                g.drawString(String.valueOf(i), vertexX[i] - 5, vertexY[i] + 5); // Draw vertex number
            }
        }
    }

    public static void main(String[] args) {
        // Launch the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DijkstraGUI().setVisible(true);
            }
        });
    }
}