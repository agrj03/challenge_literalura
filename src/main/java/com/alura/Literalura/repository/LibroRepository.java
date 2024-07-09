package com.alura.Literalura.repository;

import com.alura.Literalura.model.Autor;
import com.alura.Literalura.model.Libro;
import com.alura.Literalura.model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT a FROM Autor a ORDER BY nombre")
    List<Autor> buscarAutores();

    @Query("SELECT a FROM Autor a WHERE fechaNacimiento >= :fecha ORDER BY fechaNacimiento ASC")
    List<Autor> buscarAutoresPorFecha(Integer fecha);

    @Query("SELECT l FROM Libro l WHERE l.titulo ILIKE %:titulo% ORDER BY l.titulo")
    Libro buscarLibroPorTitulo(String titulo);

    @Query("SELECT l FROM Libro l WHERE idioma = :idioma ")
    List<Libro> buscarLibroPorIdioma(Idioma idioma);

    @Query("SELECT l FROM Libro l WHERE l.descargas > 0 ORDER BY l.descargas DESC")
    List<Libro> top10LibrosDescargados();

    @Query("SELECT AVG(l.descargas) FROM Libro l")
    Double promedioDescargas();

    @Query("SELECT a, l.titulo FROM Libro l JOIN l.autor a")
    List<Autor> temporal();
}
