package com.bdd.GUI;

import java.util.ArrayList;
import java.util.List;

class UndoManager {
    private List<Diagram> history;  // Historial de copias del diagrama
    private int maxHistory;  // Número máximo de copias a guardar
    private int currentIndex;  // Índice actual en el historial

    public UndoManager(int maxHistory) {
        this.history = new ArrayList<>();
        this.maxHistory = maxHistory;
        this.currentIndex = -1;
    }

    // Guardar una copia del diagrama en el historial
    public void saveState(Diagram diagram) {
        // Si ya tenemos el máximo de historial, eliminamos el más antiguo
        if (history.size() >= maxHistory) {
            history.removeFirst();
        }
        // Agregamos una copia del diagrama al historial
        history.add(diagram.clone());
        currentIndex = history.size() - 1;
    }

    // Deshacer: regresar a la copia anterior
    public Diagram undo() {
        if (currentIndex > 0) {
            currentIndex--;
            return history.get(currentIndex);
        } else {
            System.out.println("No hay más cambios para deshacer.");
            return null;
        }
    }

    // Rehacer: regresar a la siguiente copia
    public Diagram redo() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return history.get(currentIndex);
        } else {
            System.out.println("No hay más cambios para rehacer.");
            return null;
        }
    }

    // Obtener el estado actual del diagrama
    public Diagram getCurrentState() {
        if (currentIndex >= 0) {
            return history.get(currentIndex);
        } else {
            return null;
        }
    }
}

