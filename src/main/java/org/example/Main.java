package org.example;

import java.io.File;
import java.sql.SQLOutput;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    /// VARIABLES DE ACCESO
    // Indicar el dato que tiene que leer ( Input/Output)
            //  RUTAS DE ARCHIVO

    private static final String ARCHIVO_ENTRADA = "datos/envios_entrada.csv";
    private static final String ARCHIVO_VALIDOS = "datos/envios_validos.csv";
    private static final String ARCHIVO_ERRORES = "datos/envios_errores.log";
    private static final String ARCHIVO_RESUMEN = "datos/resumen_kilos.txt";
    private static final String CARPETA_PROCESADOS = "datos/procesados";

    //CARACTER SEPARADOR , EN ESTE CASO ; Indicar como se esta separando el csv

    private static final String SEPARADOR = ";";

    static void main() {

        System.out.println( "==================================");
        System.out.println( "Sistema de Procesamiento - Logistica Global S.A");
        System.out.println( "==================================");




         procesarArchivo();




        }
        /// METODO PRINCIPAL DE PROCESAMIENTO

        private static void procesarArchivo(){

            //Verificamos que exuste la estructura de carpetas
            // La clase File nos permite trabajar con rutas del sistema de archivos
            // Sin aún abrir ningun ARCHIVO

            verificarEstructuraCarpetas();

            /// ES PARA PODER MODIFICARLO MANIPULAR Y SABER LA RUTA

            File archivoEntrada = new File (ARCHIVO_ENTRADA);

            if ( !archivoEntrada.exists()){
                System.out.println( "ERROR: Archivo no encontrado -> " + ARCHIVO_ENTRADA);
                System.out.println( "Por favor, coloque el archivo en la carpeta 'datos/'");
            }

            System.out.println("Archivo de entrada encontrada:" + ARCHIVO_ENTRADA);
            System.out.println( "Iniciando procesamiento.....\n");







        }

        private static void verificarEstructuraCarpetas(){

            /// IMPORTANTE: FILE solo representa la  no abre ni crea

            File carpetaDatos = new File ("datos");

            if ( !carpetaDatos.exists() || !carpetaDatos.isDirectory()){
                boolean creada = carpetaDatos.mkdir();

                if (creada){
                    System.out.println( "Carpeta 'datos/' creada automaticamente' ");

                } else {
                    System.err.println("ADVERTENCIA: No se pudo crear la carpeta ' datos' " );
                }

                /// HOLAAAAAAAAAAAAAAAAAAAA

                /*  adeuuuuuuuuuuuuuuuuuu







                 */
            }

        }



    }

