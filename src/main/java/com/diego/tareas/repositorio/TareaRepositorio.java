package com.diego.tareas.repositorio;

import com.diego.tareas.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepositorio extends JpaRepository <Tarea, Integer> {
}
