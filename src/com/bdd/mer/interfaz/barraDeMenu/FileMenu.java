package com.bdd.mer.interfaz.barraDeMenu;

import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.barraDeMenu.exportacion.Archivo;
import com.bdd.mer.interfaz.barraDeMenu.exportacion.ExportPNG;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileMenu extends JMenu {

    FileMenu(MarcoPrincipal mainFrame, PanelDibujo panelDibujo, String texto) {

        // Defino el texto del menú
        setText("File");

        // Defino las opciones del menú
        // -> Exportar a PNG
        JMenuItem exportPNG = new JMenuItem("Exportar a PNG");
        exportPNG.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExportPNG.exportToPng(panelDibujo);
            }
        });

        // -> Guardar diagrama
        JMenuItem saveDiagram = new JMenuItem("Guardar diagrama");
        saveDiagram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Archivo.guardarDiagrama(panelDibujo);
            }
        });

        // -> Cargar diagrama
        JMenuItem loadDiagram = new JMenuItem("Cargar diagrama");
        loadDiagram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Archivo.cargarDiagrama(panelDibujo);
                mainFrame.repaint();
            }
        });

        // Añado las opciones al menú
        add(exportPNG);
        add(saveDiagram);
        add(loadDiagram);
    }
}
