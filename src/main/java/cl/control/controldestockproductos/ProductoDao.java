package cl.control.controldestockproductos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    public boolean guardarProducto(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, cantidad, categorias) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setInt(3, producto.getCantidad());
            stmt.setInt(4, producto.getCategoriaId());

            return stmt.executeUpdate() > 0;
        }
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
            stmt.setInt(5, producto.getId());

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

    public Categoria obtenerCategoriaPorId(int categoriaId) throws SQLException {
        Connection connection = ConexionBD.getConnection();
        String sql = "SELECT Nombre FROM ID_Categorias WHERE ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoriaId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("Nombre");
                Categoria categoria = new Categoria(id, nombre);
                return categoria;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 

    }

    public int obtenerIdCategoriaPorNombre(String nombreCategoria) throws SQLException {
    String sql = "SELECT id FROM ID_Categorias WHERE nombre = ?";
    try (Connection conn = ConexionBD.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, nombreCategoria);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
    }
    return -1; 
}


}
