package cl.control.controldestockproductos;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class Controldestockproductos extends UIproductos {

    final ProductoDao productoDao = new ProductoDao();
    final Producto producto = new Producto();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Controldestockproductos window = new Controldestockproductos();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void cargarCategorias() {
        CategoriaDAO categoriaDao = new CategoriaDAO();
        try {
            List<Categoria> categorias = categoriaDao.obtenerCategorias();
            for (Categoria categoria : categorias) {
                comboBoxCategoria.addItem(categoria.getNombre());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error cargando categorías desde la base de datos.");
        }
    }

    public Controldestockproductos() {
        super();
        llenarTabla();
        configurarAcciones();
        cargarCategorias();
    }

    private void configurarAcciones() {
        btnGuardar.addActionListener(e -> guardarProducto());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnVerReporte.addActionListener(e -> verReporte());
    }

    private void guardarProducto() {
        Producto producto = obtenerProductoDesdeCampos();
        try {
            if (productoDao.guardarProducto(producto)) {
                llenarTabla();
                limpiarCampos();
                
            } else {
                mostrarMensaje("Error al guardar el producto.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controldestockproductos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Producto obtenerProductoDesdeCampos() {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombreProducto.getText());
        nuevoProducto.setDescripcion(descripcionProducto.getText());
        nuevoProducto.setCantidad((int) cantidadProducto.getValue());

        // Obtener el ID de la categoría seleccionada:
        String nombreCategoria = (String) comboBoxCategoria.getSelectedItem();
        ProductoDao productoDao = new ProductoDao();

        try {
            int CategoriaId = productoDao.obtenerIdCategoriaPorNombre(nombreCategoria);
            nuevoProducto.setCategoriasId(CategoriaId);
        } catch (SQLException ex) {
            Logger.getLogger(Controldestockproductos.class.getName()).log(Level.SEVERE, null, ex);
            mostrarMensaje("Error obteniendo el ID de la categoría.");
        }

        return nuevoProducto;
    }

    private void eliminarProducto() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            try {
                int id = (int) tableModel.getValueAt(filaSeleccionada, 0);
                if (productoDao.eliminarProducto(id)) {
                    llenarTabla();
                } else {
                    mostrarMensaje("Error al eliminar el producto.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Controldestockproductos.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            mostrarMensaje("Selecciona un producto para eliminar.");
        }
    }

    private void modificarProducto() {
        try {
            int filaSeleccionada = tabla.getSelectedRow();

            if (filaSeleccionada >= 0) {
                Producto producto = obtenerProductoDesdeCampos();

                // Diagnostic logging
                System.out.println("Producto obtenido desde los campos: " + producto);

                producto.setId((int) tableModel.getValueAt(filaSeleccionada, 0));

                System.out.println("ID del producto a modificar: " + producto.getId());

                if (productoDao.modificarProducto(producto)) {
                    System.out.println("Producto modificado con éxito en la base de datos.");

                    llenarTabla();
                    limpiarCampos();
                } else {
                    System.out.println("El producto no fue modificado en la base de datos.");
                }
            } else {
                mostrarMensaje("Selecciona un producto para modificar.");
            }
        } catch (SQLException ex) {
            mostrarMensaje("Error al modificar el producto:" + ex.getMessage());
        }
    }

    private void llenarTabla() {
        try {
            List<Producto> productos = productoDao.obtenerProductos();
            tableModel.setRowCount(0);
            productos.forEach(producto -> tableModel.addRow(new Object[]{
                producto.getId(), producto.getNombre(),
                producto.getDescripcion(), producto.getCantidad(),
                producto.getCategoriaId(), producto.getCategoriaId()
            }));
        } catch (SQLException ex) {
            Logger.getLogger(Controldestockproductos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verReporte() {
        StringBuilder reporte = new StringBuilder();
        int totalProductos = tableModel.getRowCount();

        // Agrega el título y la fecha actual al reporte
        reporte.append("REPORTE\n");
        reporte.append("Fecha: ").append(new java.util.Date()).append("\n\n");

        // Agrega los encabezados de columna
        reporte.append("ID\tNombre\tDescripción\tCantidad\n");
        reporte.append("------------------------------------------------------\n");

        // Recorre cada fila de la tabla y agrega la información al reporte
        for (int i = 0; i < totalProductos; i++) {
            reporte.append(tableModel.getValueAt(i, 0)).append("\t"); // ID
            reporte.append(tableModel.getValueAt(i, 1)).append("\t"); // Nombre
            reporte.append(tableModel.getValueAt(i, 2)).append("\t"); // Descripción
            reporte.append(tableModel.getValueAt(i, 3)).append("\n"); // Cantidad
        }

        reporte.append("\nNúmero total de productos: ").append(totalProductos);

        mostrarMensaje(reporte.toString());
    }

    public boolean eliminarProducto(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean modificarProducto(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, cantidad = ?, categorias = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setInt(3, producto.getCantidad());
            stmt.setInt(4, producto.getCategoriaId());
            stmt.setInt(4, producto.getId());

            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                System.out.println("Producto modificado con éxito.");
            } else {
                System.out.println("No se pudo modificar el producto.");
            }
            return updated;

        }
    }

    public List<Producto> obtenerProductos() throws SQLException {
        String sql = "SELECT * FROM productos";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setCategoriasId(rs.getInt("categorias"));
                productos.add(producto);
            }
        }
        return productos;

    }

    private void limpiarCampos() {
        nombreProducto.setText("");
        descripcionProducto.setText("");
        cantidadProducto.setValue(0);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(frame, mensaje);
    }
}
