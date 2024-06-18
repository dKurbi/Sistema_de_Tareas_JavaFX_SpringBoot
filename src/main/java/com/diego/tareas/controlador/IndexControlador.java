package com.diego.tareas.controlador;

import com.diego.tareas.modelo.Tarea;
import com.diego.tareas.servicio.TareaServicio;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class IndexControlador implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @Autowired
    private TareaServicio tareaServicio;

    @FXML
    private TableView<Tarea> tareaTabla;

    @FXML
    private TableColumn<Tarea, Integer> idTareaColumna;

    @FXML
    private TableColumn<Tarea, String> nombreTareaColumna;

    @FXML
    private TableColumn<Tarea, String> responsableTareaColumna;

    @FXML
    private TableColumn<Tarea, String> statusTareaColumna;

    private final ObservableList<Tarea> tareaLista = FXCollections.observableArrayList();

    @FXML
    private TextField nombreTareaTexto;

    @FXML
    private TextField responsableTareaTexto;

    @FXML
    private TextField statusTareaTexto;

    private Integer idTareaInterno;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configurarColumnas();
        listarTareas();


    }

    private void listarTareas() {
        tareaLista.clear();
        tareaLista.addAll(tareaServicio.listarTareas());
        tareaTabla.setItems(tareaLista);
    }

    private void configurarColumnas() {
        idTareaColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        nombreTareaColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        responsableTareaColumna.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        statusTareaColumna.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void agregarTarea(ActionEvent actionEvent) {
        if (checkFormulario()) {
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea);
            if (idTareaInterno != null && !esTareaDiferente(tarea)) {
                mostrarMensaje("Informacion", "La tarea que quiere agregar ya existe");
                return;
            }
            tarea.setIdTarea(null);
            tareaServicio.guardarTarea(tarea);
            limpiarFormulario();
            listarTareas();
            mostrarMensaje("Informacion", "Tarea agregada");
        }
    }

    public void eliminarTarea(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null){
            logger.info("Registro a eliminar" + tarea.toString());
            tareaServicio.eliminarTarea(tarea);
            limpiarFormulario();
            listarTareas();
            mostrarMensaje("Informacion", "Tarea eliminada: " + tarea.getIdTarea());
        }
        else
            mostrarMensaje("Error", "No se ha seleccionado ninguna tarea");
    }

    public void cargarTareaFormulario() {
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null) {
            idTareaInterno = tarea.getIdTarea();
            nombreTareaTexto.setText(tarea.getNombreTarea());
            responsableTareaTexto.setText(tarea.getResponsable());
            statusTareaTexto.setText(tarea.getStatus());
        }
    }

    public void modificarTarea() {
        if (idTareaInterno == null) {
            mostrarMensaje("Informacion", "Debe seleccionar una tarea");
            return;
        }
        if (checkFormulario() == false) {
            return;
        }
        var tarea = new Tarea();
        recolectarDatosFormulario(tarea);
        if (esTareaDiferente(tarea)) {
            tareaServicio.guardarTarea(tarea);
            limpiarFormulario();
            listarTareas();
            mostrarMensaje("Informacion", "Tarea modificada");
        }

    }

    private boolean esTareaDiferente(Tarea tarea) {
        var nuevaTarea = new Tarea();
        nuevaTarea = tareaServicio.buscarTareaPorId(tarea.getIdTarea());
        boolean nombreDiferente = (tarea.getNombreTarea() != null && !tarea.getNombreTarea().equals(nuevaTarea.getNombreTarea())) ||
                (tarea.getNombreTarea() == null && nuevaTarea.getNombreTarea() != null);
        boolean statusDiferente = (tarea.getStatus() != null && !tarea.getStatus().equals(nuevaTarea.getStatus())) ||
                (tarea.getStatus() == null && nuevaTarea.getStatus() != null);
        boolean responsableDiferente = (tarea.getResponsable() != null && !tarea.getResponsable().equals(nuevaTarea.getResponsable())) ||
                (tarea.getResponsable() == null && nuevaTarea.getResponsable() != null);
        return nombreDiferente || statusDiferente || responsableDiferente;
    }

    private boolean checkFormulario() {
        if (nombreTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error de Validacion", "Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return false;
        }
        if (responsableTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error de Validacion", "Debe proporcionar un reponsable");
            responsableTareaTexto.requestFocus();
            return false;
        }
        if (statusTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error de Validacion", "Debe proporcionar un estado");
            statusTareaTexto.requestFocus();
            return false;
        }
        return true;
    }


    public void limpiarFormulario() {
        nombreTareaTexto.clear();
        responsableTareaTexto.clear();
        statusTareaTexto.clear();
        idTareaInterno = null;
    }

    private void recolectarDatosFormulario(Tarea tarea) {
        if (idTareaInterno != null)
            tarea.setIdTarea(idTareaInterno);
        tarea.setNombreTarea(nombreTareaTexto.getText());
        tarea.setResponsable(responsableTareaTexto.getText());
        tarea.setStatus(statusTareaTexto.getText());
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
