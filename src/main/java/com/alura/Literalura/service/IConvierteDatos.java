package com.alura.Literalura.service;

//Comienza con 'i' mayuscula ya que hace referencia a que es una Interfaz
public interface IConvierteDatos {
    // T indica un tipo de dato generico | método
    <T> T obtenerDatos(String json, Class<T> clase);
    /*
    * <T> Indica cualquier tipo de dato
    * T Es el retorno de dicho tipo de dato generico
    * Nombre del metodo
    * Class<T> permite recibir cualquier tipo de clase, en este caso, un record para mapear datos JSON
    * */
}
