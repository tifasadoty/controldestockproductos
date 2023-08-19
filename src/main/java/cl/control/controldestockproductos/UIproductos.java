package cl.control.controldestockproductos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.JComboBox;
import java.util.List;
import cl.control.controldestockproductos.Categoria;
import cl.control.controldestockproductos.CategoriaDAO;

public class UIproductos {

    protected JFrame frame;
    protected JTextField nombreProducto;
    protected JTextField descripcionProducto;
    protected JSpinner cantidadProducto;
    protected JComboBox<String> comboBoxCategoria;
    protected JTable tabla;
    protected DefaultTableModel tableModel;

    protected JButton btnGuardar;
    protected JButton btnLimpiar;
    protected JButton btnEliminar;
    protected JButton btnModificar;
    protected JButton btnVerReporte;

    public UIproductos() {
        inicializar();
    }

    private void inicializar() {
        configurarVentana();
        configurarPanelPrincipal();
    }

    private void configurarVentana() {
        frame = new JFrame("Control de Stock de Productos");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void configurarPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        frame.getContentPane().add(panel);

        panel.add(crearPanelDeEntrada(), BorderLayout.NORTH);
        panel.add(crearScrollPaneConTabla(), BorderLayout.CENTER);
        panel.add(crearPanelDeBotonesInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelDeEntrada() {

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detalles del producto"));

        // titulos 
        nombreProducto = new JTextField();
        descripcionProducto = new JTextField();
        cantidadProducto = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        comboBoxCategoria = new JComboBox<>();
        

        // agregando al panel
        inputPanel.add(new JLabel("Nombre del producto:"));
        inputPanel.add(nombreProducto);

        inputPanel.add(new JLabel("Descripcion del producto:"));
        inputPanel.add(descripcionProducto);

        inputPanel.add(new JLabel("Cantidad:"));
        inputPanel.add(cantidadProducto);

        inputPanel.add(new JLabel("Tipo de categoría:"));
        inputPanel.add(comboBoxCategoria);

        return inputPanel;

    }

    private JScrollPane crearScrollPaneConTabla() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Cantidad", "Categorias"}, 0);
        tabla = new JTable(tableModel);
        return new JScrollPane(tabla);
    }

    private JPanel crearPanelDeBotonesInferior() {

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");
        btnVerReporte = new JButton("Ver Reporte");

        bottomPanel.add(btnGuardar);
        bottomPanel.add(btnLimpiar);
        bottomPanel.add(btnEliminar);
        bottomPanel.add(btnModificar);
        bottomPanel.add(btnVerReporte);

        return bottomPanel;
    }

    private void guardarProductoEnBaseDeDatos() {
        Producto producto = new Producto();
        producto.setNombre(nombreProducto.getText());
        producto.setDescripcion(descripcionProducto.getText());
        producto.setCantidad((Integer) cantidadProducto.getValue());
        String categoriaSeleccionada = (String) comboBoxCategoria.getSelectedItem();
        ProductoDao productoDao = new ProductoDao();
        try {
            int idCategoria = productoDao.obtenerIdCategoriaPorNombre(categoriaSeleccionada);
            producto.setCategoriasId(idCategoria);

            if (productoDao.guardarProducto(producto)) {
                JOptionPane.showMessageDialog(frame, "Producto guardado con éxito.");
            } else {
                JOptionPane.showMessageDialog(frame, "Error al guardar el producto.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error de conexión a la base de datos.");
        }
    }


}
