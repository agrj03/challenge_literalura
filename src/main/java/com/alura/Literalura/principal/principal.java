package com.alura.Literalura.principal;

import com.alura.Literalura.model.Autor;
import com.alura.Literalura.model.Datos;
import com.alura.Literalura.model.Idioma;
import com.alura.Literalura.model.Libro;
import com.alura.Literalura.repository.LibroRepository;
import com.alura.Literalura.service.ConsumoAPI;
import com.alura.Literalura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class principal {
    private static final String BASEURL = "https://gutendex.com/books/?search=";
    private static final String START_AUTHOR = "author_year_start=";
    private static final String END_AUTHOR = "&author_year_end=";
    private final Scanner input = new Scanner(System.in);
    private final ConsumoAPI consultarAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private List<Libro> libros;
    private List<Autor> autores;
    private LibroRepository repositorio;

    public principal(LibroRepository libroRepository) {
        this.repositorio = libroRepository;
    }

    public void mostrarMenu(){
        String menu = """
                ******************************
                *
                *Elija la opción a través de su número
                * 1 - Buscar libro por titulo (Nuevo registro)
                * 2 - Listar libros registrados
                * 3 - Listar Autores registrados
                * 4 - Listar Autores vivos en un determinado año
                * 5 - Listar libros por idioma
                * 6 - Consultar libro por titulo (Buscar)
                * 7 - Top 10 Libros descargados (BD)
                * 8 - Top 10 Libros descargados + Estadísticas
                * 
                * 0 - Salir
                *
                ******************************
                """;
        int option = -1;

        while (option != 0){
            System.out.println(menu);
            try {
                option = Integer.valueOf(input.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Introduce un número");
            }

            switch (option){
                case 1:
                    buscarLibroNuevo();
                    break;
                case 2:
                    mostrarLibros();
                    break;
                case 3:
                    mostrarAutores();
                    break;
                case 4:
                    mostrarAutoresPorFecha();
                    break;
                case 5:
                    mostrarLibrosPorIdioma();
                    break;
                case 6:
                    buscarLibro();
                    break;
                case 7:
                    topDescargasLibros();
                    break;
                case 8:
                    top10ConEstadisticas();
                    break;
                case 0:
                    System.out.println("Terminando la ejecución del programa, espere....");
                    break;
                default:
                    System.out.println("Opción no valida");
            }
        }

    }

    private Datos getDatosLibro(){
        System.out.println("Escribe el nombre del libro");
        String buscar = URLEncoder.encode(input.nextLine(), StandardCharsets.UTF_8);
        try {
            var json = consultarAPI.obtenerDatos(BASEURL + buscar);
            return conversor.obtenerDatos(json, Datos.class);
        } catch (NoSuchElementException | NullPointerException e){
            System.out.println("Libro no encontrado.");
            e.getMessage();
        }
        return null;
    }

    private void buscarLibroNuevo(){
        Datos datos = getDatosLibro();
        try {
            Libro libro = new Libro(datos.Libros().getFirst());
            System.out.println("libro CONTIENE: " +libro);
            try {
                repositorio.save(libro);
            }catch (DataIntegrityViolationException e){
                System.out.println("Ya existe el registro.");
            }

            //la bd no asigna el libro_id!!!!!!!1

        } catch (NullPointerException | NoSuchElementException e){
            System.out.println("Libro no encontrado.");
            e.getMessage();
        }
        libros = datos.Libros().stream().map(l-> new Libro(l)).collect(Collectors.toList());
    }
    //mod
    private void mostrarLibros(){
        libros = repositorio.findAll();
        libros.stream().sorted(Comparator.comparing(Libro::getTitulo)).forEach(System.out::println);
    }
    //mod
    private void mostrarAutores(){
        autores = repositorio.buscarAutores();
        autores.forEach(System.out::println);
    }

    //Solucion cuestionable
    private void mostrarAutoresPorFecha(){
        System.out.println("Dígita el Año en el que deseas buscar:");
        try {
            int fecha = input.nextInt();
            input.nextLine();
            autores = repositorio.buscarAutoresPorFecha(fecha);
            autores.forEach(System.out::println);
        }catch (NumberFormatException e){
            System.out.println("Ingresa números.");
        }
    }

    private void mostrarLibrosPorIdioma(){
        System.out.println("""
                Escribe el código de letras para el idioma a buscar
                
                es - Español
                en - Inglés
                fr - Frances
                
                Buscar:
                """);
        try {
            String opcion = input.nextLine();
            Idioma lang = Idioma.fromEspanol(opcion);
            libros = repositorio.buscarLibroPorIdioma(lang);
            System.out.println("Total de libros encontrados = " + libros.size());
            libros.forEach(System.out::println);
        }catch (Exception e){
            System.out.println("Ocurrio un error. " + e.getMessage());
        }

    }

    private void buscarLibro(){
        System.out.println("Escribe el nombre del libro");
        try {
            String titulo = input.nextLine();
            Libro libroEncontrado = repositorio.buscarLibroPorTitulo(titulo);
            if (libroEncontrado != null ){
                System.out.println("Datos del libro \n"+libroEncontrado);
            }else {
                System.out.println("Libro no encontrado o no existe");
            }
        }catch (IllegalFormatException e){
            System.out.println(e.getMessage());
        }
    }

    private void topDescargasLibros(){
        System.out.println("Libros más descargados");
        libros = repositorio.top10LibrosDescargados();
        libros.forEach(System.out::println);
    }

    private void top10ConEstadisticas(){
        try {
            var json = consultarAPI.obtenerDatos("https://gutendex.com/books/?sort=popular");
            var datos = conversor.obtenerDatos(json, Datos.class);
            libros = datos.Libros().stream()
                    .map(l -> new Libro(l))
                    .limit(10).collect(Collectors.toList());

            System.out.println("Top 10 Libros");
            libros.forEach(System.out::println);

            //Obtener estadisticas
            DoubleSummaryStatistics Est = libros.stream()
                    .filter(libro -> libro.getDescargas() > 0)
                    .collect(Collectors.summarizingDouble(Libro::getDescargas));

            System.out.println("Máximo de descargas = "+ Est.getMax());
            System.out.println("Promedio de descargas = "+ Est.getAverage());
            System.out.println("Mínimo de descargas = "+ Est.getMin());
        } catch (NoSuchElementException | NullPointerException e){
            System.out.println("Libro no encontrado.");
            e.getMessage();
        }
    }
}
