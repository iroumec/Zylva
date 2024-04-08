package com.bdd.mer.interfaz.menuLateral;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddRelationshipButton extends JButton {
    public AddRelationshipButton(String name, Dimension dimension, String fuente, MarcoPrincipal mainFrame, PanelDibujo panelDibujo) {
        setText(name);
        setMaximumSize(dimension);
        setBackground(Color.WHITE);
        setOpaque(Boolean.TRUE);
        setFont(new Font(fuente, Font.BOLD, 12));

        // Defino la funcionalidad del botón
        addActionListener(_ -> {
            boolean creacionRelacionCancelada = false;

            // Solo procede si se han seleccionado entre 1 y 3 entidades
            if (panelDibujo.participaSoloEntidades() && !panelDibujo.getComponentesSeleccionadas().isEmpty() && panelDibujo.getComponentesSeleccionadas().size() <= 3) {
                // Muestra una ventana emergente para ingresar el nombre de la relación

                String nombre = JOptionPane.showInputDialog(mainFrame, "Ingrese el nombre de la nueva relación");
                if (nombre != null) {
                    // Si el usuario ingresó un nombre, agrega una nueva relación con ese nombre
                    List<Dupla<Arrastrable, Dupla<Character, Character>>> entidadesParticipantes = new ArrayList<>();
                    Dupla<Character, Character> cardinalidad;
                    for (Arrastrable entidad : panelDibujo.getComponentesSeleccionadas()) {
                        cardinalidad = ingresarCardinalidad(((Entidad) entidad).getNombre());
                        if (cardinalidad != null) {
                            entidadesParticipantes.add(new Dupla<>(entidad, cardinalidad));
                        } else {
                            creacionRelacionCancelada = true;
                            break;
                        }
                    }
                    if (!creacionRelacionCancelada) {
                        panelDibujo.agregarRelacion(new Relacion(nombre, entidadesParticipantes, 150, 150));
                        panelDibujo.limpiarEntidadesSeleccionadas();
                    }

                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Debe seleccionar entre 1 y 3 entidades para crear una relación.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
        });
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
}
