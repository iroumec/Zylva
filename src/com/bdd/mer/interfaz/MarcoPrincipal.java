package com.bdd.mer.interfaz;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.TipoAtributo;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.anotacion.Nota;
import com.bdd.mer.interfaz.barraDeMenu.MenuBar;
import com.bdd.mer.interfaz.menuLateral.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class MarcoPrincipal extends JFrame {

    private final PanelDibujo panelDibujo;
    private final com.bdd.mer.interfaz.barraDeMenu.MenuBar menuBar;
    private Point initialClick;

    public MarcoPrincipal() {
        String fuente = "Verdana";

        setUndecorated(false);  // Elimina la barra de título
        setTitle("Zilva DERExt");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crea el panel de dibujo, la barra de menú y el menú
        panelDibujo = new PanelDibujo();
        menuBar = new MenuBar(this, panelDibujo);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        // Aesthetic blue
        //menu.setBackground(new Color(215, 239, 249));
        menu.setBackground(Color.WHITE);

        // Añade un MouseListener para permitir cambiar el tamaño de la ventana
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Si se arrastra el borde inferior derecho de la ventana, cambia el tamaño
                if (initialClick != null && initialClick.x > getWidth() - 10 && initialClick.y > getHeight() - 10) {
                    setSize(e.getX(), e.getY());
                }
            }
        });

        menu.add(new AddEntityButton("Entidad", dimension, fuente, this, panelDibujo));
        menu.add(new AddRelationshipButton("Relación", dimension, fuente, this, panelDibujo));
        menu.add(new AddDependencyButton("Dependencia", dimension, fuente, this, panelDibujo));

        menu.add(new AddHeriarchyButton("Jerarquía", dimension, fuente, this, panelDibujo));

        // Añade una nota al programa
        JButton botonAgregarNota = getNewButton("Nota", dimension, fuente);
        botonAgregarNota.addActionListener(_ -> {
            // Muestra una ventana emergente para ingresar el contenido de la nota
            String contenido = JOptionPane.showInputDialog(this, "Ingrese el contenido de la nueva nota");
            if (contenido != null) {
                // Si el usuario ingresó contenido, agrega una nueva nota con ese contenido
                panelDibujo.agregarNota(new Nota(contenido, 150, 150));
            }
        });
        menu.add(botonAgregarNota);

        /*
        // Crea el botón con el ícono de basura
        ImageIcon trashIcon = new ImageIcon("src/com/bdd/mer/interfaz/icons/trash.png");  // Reemplaza esto con la ruta a tu ícono
        JButton trashButton = new JButton(trashIcon);
        trashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes poner el código para realizar la acción de eliminar
            }
        });

        // Añade el botón a la ventana
        menu.add(trashButton, BorderLayout.CENTER);


         */
        // Agrega un botón para eliminar lo seleccionado
        JButton botonEliminar = getNewButton("Eliminar", dimension, fuente);
        botonEliminar.addActionListener(_ -> {
            // Solo procede si se ha seleccionado al menos una entidad
            if (!panelDibujo.getComponentesSeleccionadas().isEmpty()) {
                // Muestra una ventana emergente para ingresar el nombre de la relación
                int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro de que desea eliminar las componentes seleccionadas?");
                if (confirmacion == JOptionPane.YES_OPTION) {
                    List<Entidad> paraEliminarEntidad = new ArrayList<>();
                    List<Relacion> paraEliminarRelacion = new ArrayList<>();
                    // Encuentra los elementos para eliminar
                    for (Arrastrable a : panelDibujo.getComponentesSeleccionadas()) {
                        if (a.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                            paraEliminarRelacion.addAll(((Entidad) a).getRelaciones());
                            paraEliminarEntidad.add((Entidad) a);
                        } else {
                            paraEliminarRelacion.add((Relacion) a);
                        }
                    }

                    // Elimina los elementos
                    for (Relacion r : paraEliminarRelacion) {
                        panelDibujo.eliminarRelacion(r);
                    }
                    for (Entidad ent : paraEliminarEntidad) {
                        panelDibujo.eliminarEntidad(ent);
                    }

                    panelDibujo.limpiarEntidadesSeleccionadas();
                    panelDibujo.setSeleccionando(false);
                }

                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un elemento.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
        });
        menu.add(botonEliminar);

        // Agrega el panel de dibujo y el menú al marco
        add(panelDibujo, BorderLayout.CENTER);
        add(menu, BorderLayout.WEST);
        // Añade la barra de menú a la ventana
        setJMenuBar(menuBar);
    }

    private Dupla<Character, Character> ingresarCardinalidad(String nombre) {
        JTextField cardinalidadMinimaCampo = new JTextField(1);
        JTextField cardinalidadMaximaCampo = new JTextField(1);
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // Deshabilita el botón OK inicialmente

        JPanel miPanel = new JPanel();
        miPanel.add(new JLabel("Ingrese las cardinalidad para la entidad " + nombre + ": "));
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad mínima:"));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(Box.createHorizontalStrut(15)); // Espaciador
        miPanel.add(new JLabel("Cardinalidad máxima:"));
        miPanel.add(cardinalidadMaximaCampo);

        int resultado = JOptionPane.showConfirmDialog(null, miPanel,
                "Por favor ingrese dos valores", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            String cardinalidadMinima = cardinalidadMinimaCampo.getText();
            String cardinalidadMaxima = cardinalidadMaximaCampo.getText();

            if (cardinalidadMinima.isEmpty() || cardinalidadMaxima.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ambos campos deben tener un valor");
                return ingresarCardinalidad(nombre);
            } else {
                return new Dupla<>(cardinalidadMinima.charAt(0), cardinalidadMaxima.charAt(0));
            }
        } else {
            return null;
        }

    }

    public void seleccionarIdentificadorPrincipal() {

        List<Atributo> atributosEntidad = ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).getAtributos();
        Object[] opciones = new Object[atributosEntidad.size()];

        for (int i = 0; i < atributosEntidad.size(); i++) {
            opciones[i] = (atributosEntidad.get(i).getNombre());
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione el identificador unívoco", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion < atributosEntidad.size()) {
            atributosEntidad.get(seleccion).setTipo(TipoAtributo.MAIN);
            ((Entidad) panelDibujo.getComponentesSeleccionadas().getFirst()).setTieneIDPrincipal(Boolean.TRUE);
        }

    }

    public JButton getNewButton(String nombreBoton, Dimension dimension, String fuente) {
        JButton botonARetornar = new JButton(nombreBoton);

        botonARetornar.setMaximumSize(dimension);
        botonARetornar.setBackground(Color.WHITE);
        botonARetornar.setOpaque(Boolean.TRUE);
        botonARetornar.setFont(new Font(fuente, Font.BOLD, 12));

        return botonARetornar;
    }

    private void eliminarRelacion(Relacion relacion) {
        for (Dupla<Arrastrable, Dupla<Character, Character>> dupla : relacion.getEntidades()) {
            ((Entidad) dupla.getPrimero()).getRelaciones().remove(relacion);
        }
        panelDibujo.relaciones.remove(relacion);
    }

    public PanelDibujo getPanelDibujo() {
        return panelDibujo;
    }
}
