package org.example;

import java.io.File;
import java.nio.channels.ScatteringByteChannel;
import java.sql.SQLOutput;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    /// VARIABLES DE ACCESO
    // Indicar el dato que tiene que leer ( Input/Output)
            //  RUTAS DE ARCHIVO

    private static final String ARCHIVO_ENTRADA = "datos/envios_entrada.csv";
    private static final String ARCHIVO_VALIDOS = "datos/envios_validos.csv";
    private static final String ARCHIVO_ERRORES = "datos/errores.log";
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

            // PASO 3: Aqui se implementaria la lógixa de lectura del CSV, validacion de datos,
                // para poder acceder aL ARCHIVO
            // Variable para acu,mular el total de kilos procesados
            /// double para permitir decimales


            double totalKilos = 0.0; /// valor inicial
           int lineasProcesadas = 0;
           int lineasValidas = 0;
           int lineasInvalidas = 0;

           /// Porque no van a ser visible en las otras funciones
            ///  sin-with-respurces habria que cerrar manualmente cada recurso


            // Paso 4 : try-with-resources para manejar la apertura

            try(
                    //File reader - Abrir eñl archivo de antrada para la lectura
                    //Es lento para archivos grandes porque hace una llamada al sistema de archivos p
                    // para cada caracter leído. POr eso lo envolvemos en BufferedReader.

                    //  Bufferedreader - Permite leer el archivo línea por línea, lo que es mucho más eficiente.
                    //  Además, tiene un método readline() que facilita la lectura de líneas completas.


                    /// #### RECURSO 1: LEER ARCHIVO
                    BufferedReader lector = new BufferedReader(

                            new FileReader(archivoEntrada , StandardCharsets.UTF_8)

                    );


                    //// ### RECURSO 2: Print wirter para escrubur el archivo de envios válidos
                    //Printwriter - POermite escribir textoi de forma sencilla. Tiene métodos como print()
                    //Usados UFT_8 para evitar  problemas con caracteres en español


                    //FileWriter para escribir
                    // autoflush si es true cada llamada a println se escriba inmediatamente en el archivo, sin necesidad
                    //Append

                    ) {

                PrintWriter escritorValidos = new PrintWriter(
                   new FileWriter(ARCHIVO_VALIDOS, StandardCharsets.UTF_8, false)    , true

                );


                ///  #### RECRUSO 3: PrintWriter para escribir el archivo errores
               // Mismo concepto que el anterior. el segundo parametro de FilewRITER es true
                //


                PrintWriter escritorErrores= new PrintWriter(
                        new FileWriter(ARCHIVO_ERRORES, StandardCharsets.UTF_8, true)    , false

                );



                      /// --------INICIO DEL BLOQUE DE PROCESAMIENTO ---
            /// Escribimos una cabecera en el log de errores para identidicar la sesión.
            // DateTimeFormatear  define el PATRON de formato de la fecha

                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM -dd HH: mm:ss");

                String fechaHoraActual = LocalDateTime.now().format(formato); // 2026-03-25 14:20

                escritorErrores.println("==============================");
                escritorErrores.println("========== Proceso Inicado ==========");
                escritorErrores.println("==============================");



            } catch (Exception e){
                throw new RuntimeException(e);
                }
            }


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









