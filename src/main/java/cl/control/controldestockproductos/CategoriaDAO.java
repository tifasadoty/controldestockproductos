package cl.control.controldestockproductos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CategoriaDAO {

    // Método para obtener todas las categorías
    public List<Categoria> obtenerCategorias() throws SQLException {
        String sql = "SELECT * FROM ID_Categorias";
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("Nombre"));
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    // Método para obtener una categoría por su ID
    public Categoria obtenerCategoriaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ID_Categorias WHERE ID = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("Nombre"));
                return categoria;
            }
        }
        return null;
    }

    // Método para obtener el ID de una categoría basado en su nombre
    public int obtenerIdCategoriaPorNombre(String nombreCategoria) throws SQLException {
        String sql = "SELECT ID FROM ID_Categorias WHERE Nombre = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            } else {
                return -1;
            }
        }
    }
}



