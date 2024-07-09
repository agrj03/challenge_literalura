package com.alura.Literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private int descargas;
    @ManyToOne(cascade = CascadeType.ALL)
    private Autor autor;

    public Libro(){}

    public Libro(DatosLibro l) {
        this.titulo = l.titulo();
        this.autor = l.autores().getFirst();
        //this.idioma = l.idioma().getFirst();
        try {
            this.idioma = Idioma.fromString(l.idioma().getFirst());
        }catch (IllegalArgumentException e){
            this.idioma = null;
        }
        this.descargas = l.descargas();
    }

    @Override
    public String toString() {
        return String.format("""
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Descargas: %d
                        """,
                titulo, autor, idioma, descargas);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }
}