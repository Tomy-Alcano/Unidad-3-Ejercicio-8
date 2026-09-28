import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ModificiarClientes {

    private static DefaultTableModel modelo;

    private static void cargarDatos() {

        modelo.setRowCount(0);

        try (
                Connection con = Conexion.conectar();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM clientes")
        ) {

            while (rs.next()) {

                modelo.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getString("email"),
                                rs.getString("telefono")
                        });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame ventana = new JFrame("Modificar Clientes");
            ventana.setSize(800, 500);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null);

            final int[] idSeleccionado = {-1};

            JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));

            JLabel lblId = new JLabel("ID: Ninguno");

            JTextField txtNombre = new JTextField();

            JTextField txtEmail = new JTextField();

            JTextField txtTelefono = new JTextField();

            JButton btnModificar = new JButton("Modificar cliente");

            btnModificar.setEnabled(false);

            formulario.add(new JLabel("Cliente seleccionado:"));
            formulario.add(lblId);

            formulario.add(new JLabel("Nombre:"));
            formulario.add(txtNombre);

            formulario.add(new JLabel("Email:"));
            formulario.add(txtEmail);

            formulario.add(new JLabel("Teléfono:"));
            formulario.add(txtTelefono);

            formulario.add(new JLabel(""));
            formulario.add(btnModificar);

            modelo = new DefaultTableModel(
                    new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0);

            JTable tabla = new JTable(modelo);

            JScrollPane scroll = new JScrollPane(tabla);

            cargarDatos();

            tabla.getSelectionModel().addListSelectionListener(e -> {

                        if (!e.getValueIsAdjusting()) {

                            int fila = tabla.getSelectedRow();

                            if (fila != -1) {

                                idSeleccionado[0] = Integer.parseInt(modelo.getValueAt(fila, 0).toString());

                                lblId.setText("ID: " + idSeleccionado[0]);

                                txtNombre.setText(modelo.getValueAt(fila, 1).toString());

                                txtEmail.setText(modelo.getValueAt(fila,2).toString());

                                txtTelefono.setText(modelo.getValueAt(fila, 3).toString());

                                btnModificar.setEnabled(true);
                            }
                        }
                    });

            btnModificar.addActionListener(e -> {

                String nombre = txtNombre.getText().trim();

                String email = txtEmail.getText().trim();

                String telefono = txtTelefono.getText().trim();

                if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {

                    JOptionPane.showMessageDialog(ventana, "Todos los campos son obligatorios");

                    return;
                }

                try (
                        Connection con = Conexion.conectar();

                        PreparedStatement ps = con.prepareStatement("UPDATE clientes SET nombre=?, email=?, telefono=? WHERE id=?")
                ) {

                    ps.setString(1, nombre);
                    ps.setString(2, email);
                    ps.setString(3, telefono);
                    ps.setInt(4, idSeleccionado[0]);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(ventana, "Cliente modificado correctamente");

                    cargarDatos();

                    txtNombre.setText("");
                    txtEmail.setText("");
                    txtTelefono.setText("");
                    lblId.setText("ID: Ninguno");

                    idSeleccionado[0] = -1;
                    btnModificar.setEnabled(false);

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(ventana, ex.getMessage());
                }
            });

            ventana.add(formulario, BorderLayout.NORTH);
            ventana.add(scroll, BorderLayout.CENTER);

            ventana.setVisible(true);
        });
    }
}