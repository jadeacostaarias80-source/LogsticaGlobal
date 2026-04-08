package org.example;

import javax.xml.transform.Result;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final String ARCHIVO_ENTRADA = "src/main/java/org/datos/envios_entrada.csv";
    private static final String ARCHIVO_VALIDOS = "src/main/java/org/datos/envios_validos.csv";
    private static final String ARCHIVO_ERRORES = "src/main/java/org/datos/errores.log";
    // Ruta del archivo de texto con el resumen de kilos
    private static final String ARCHIVO_RESUMEN = "src/main/java/org/datos/resumen_kilos.txt";
    // Nombre de la carpeta donde moveremos el archivo una vez procesado
    private static final String CARPETA_PROCESADOS = "src/main/java/org/datos/procesados";
    ///  Caracter separador del CSV. En este proyecto usaremos ";" en vez de ","
    private static final String SEPARADOR = ";";


    static void main(String[] args) {

        System.out.println("===============================================");
        System.out.println("Sistema de Procesamiento - Logistica Global S.A");
        System.out.println("===============================================");


        procesarArchivo();


    }

    /// METODO PRINCIPAL DE PROCESAMIENTO

    private static void procesarArchivo() {

        //Verificamos que exuste la estructura de carpetas
        // La clase File nos permite trabajar con rutas del sistema de archivos
        // Sin aún abrir ningun ARCHIVO

        verificarEstructuraCarpetas();

        /// ES PARA PODER MODIFICARLO MANIPULAR Y SABER LA RUTA

        File archivoEntrada = new File(ARCHIVO_ENTRADA);

        if (!archivoEntrada.exists()) {
            System.out.println("ERROR: Archivo no encontrado -> " + ARCHIVO_ENTRADA);
            System.out.println("Por favor, coloque el archivo en la carpeta 'datos/'");
        }

        System.out.println("Archivo de entrada encontrada:" + ARCHIVO_ENTRADA);
        System.out.println("Iniciando procesamiento.....\n");

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

        try (
                //File reader - Abrir eñl archivo de antrada para la lectura
                //Es lento para archivos grandes porque hace una llamada al sistema de archivos p
                // para cada caracter leído. POr eso lo envolvemos en BufferedReader.

                //  Bufferedreader - Permite leer el archivo línea por línea, lo que es mucho más eficiente.
                //  Además, tiene un método readline() que facilita la lectura de líneas completas.


                /// #### RECURSO 1: LEER ARCHIVO
                BufferedReader lector = new BufferedReader(

                        new FileReader(archivoEntrada, StandardCharsets.UTF_8)

                );


                //// ### RECURSO 2: Print wirter para escrubur el archivo de envios válidos
                //Printwriter - POermite escribir textoi de forma sencilla. Tiene métodos como print()
                //Usados UFT_8 para evitar  problemas con caracteres en español


                //FileWriter para escribir
                // autoflush si es true cada llamada a println se escriba inmediatamente en el archivo, sin necesidad
                //Append

        ) {

            PrintWriter escritorValidos = new PrintWriter(
                    new FileWriter(ARCHIVO_VALIDOS, StandardCharsets.UTF_8, false), true

            );


            ///  #### RECRUSO 3: PrintWriter para escribir el archivo errores
            // Mismo concepto que el anterior. el segundo parametro de FilewRITER es true
            //


            PrintWriter escritorErrores = new PrintWriter(
                    new FileWriter(ARCHIVO_ERRORES, StandardCharsets.UTF_8, true), false

            );


            /// --------INICIO DEL BLOQUE DE PROCESAMIENTO ---
            /// Escribimos una cabecera en el log de errores para identidicar la sesión.
            // DateTimeFormatear  define el PATRON de formato de la fecha

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM -dd HH: mm:ss");

            String fechaHoraActual = LocalDateTime.now().format(formato); // 2026-03-25 14:20

            escritorErrores.println("=================================================");
            escritorErrores.println("--- Procesamiento iniciado: " + fechaHoraActual + " ---");
            escritorErrores.println("=================================================");

            // Escribimos la cabecera del CSV de salida.
            escritorValidos.println("ID_Envio; Destino; Kilos; Estado");

            int numeroLinea = 0; //contador para llevar el número de liune
            String linea;
            //contador para llevar el número de linea actual

            try {

                while ((linea = lector.readLine()) != null) {
                    numeroLinea++;

                    linea = linea.trim();

                    // Ignorar líneas vacías
                    if (linea.isEmpty()) {
                        continue;
                    }

                    // Ignorar cabecera
                    if (linea.startsWith("ID_Envio")) {
                        continue;
                    }

                    // Procesamiento real
                    System.out.println("Procesando línea " + numeroLinea + ": " + linea);
                }

            } catch (Exception e) {
                e.printStackTrace(); // mejor para depurar que RuntimeException
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void verificarEstructuraCarpetas() {

        /// IMPORTANTE: FILE solo representa la  no abre ni crea

        File carpetaDatos = new File("datos");

        if (!carpetaDatos.exists() || !carpetaDatos.isDirectory()) {
            boolean creada = carpetaDatos.mkdir();

            if (creada) {
                System.out.println("Carpeta 'datos/' creada automaticamente' ");

            } else {
                System.err.println("ADVERTENCIA: No se pudo crear la carpeta ' datos' ");
            }

            /// HOLAAAAAAAAAAAAAAAAAAAA

            /*  adeuuuuuuuuuuuuuuuuuu


             */
        }
    }


    private static Resultado validarYProcesarLinea(String linea, int numerodelinea, PrintWriter escritorValidos, PrintWriter escritorerrores) {


        String[] campos = linea.split(SEPARADOR);

        if (campos.length != 4) {

            return new Resultado(false, 0.0);
        }

       String idEnvio = campos[0].trim();
        String destino = campos [1].trim();
        String kilosStr = campos[2].trim();
        String estado = campos[3].trim();






        /**
         * escribe una linea de error en el archivo errores.log con formato estandar
         *
         *
         *
         */
        }




    /**
     * Escribe una línea de error en el archivo errores.log con formato estándar.
     *
     * @param escritorErrores El PrintWriter para escribir errores
     * @param numeroLinea El número de línea donde ocurrió el error
     * @param lineaOriginal La línea original del CSV que causó el error
     * @param motivo El motivo del error (descripción del problema)
     */
    private static void registrarError(PrintWriter escritorErrores, int numeroLinea, String lineaOriginal, String motivo) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formato);

        // Escribimos una línea de log con formato estructurado:
        // [TIMESTAMP] | Línea N | Motivo | Contenido original
        escritorErrores.println("[" + timestamp + "] | Línea " + numeroLinea
                + " | ERROR: " + motivo
                + " | Contenido: '" + lineaOriginal + "'");

        // También imprimimos un mensaje en la consola para informar al usuario
        System.out.println("  [!] Línea " + numeroLinea + " rechazada: " + motivo);
    }


    private class Resultado{
        boolean esValida;
        double kilos;


        public Resultado(boolean esValida, double kilos) {
            this.esValida = esValida;
            this.kilos = kilos;
        }
    }




}











///Bucle principal de LEXCTURA
// readline () devuelve null cuando se llega al final del archivo, por eso usamos
//
// El patron (lineabr