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
        /// 1. Verificamos que existe la estructura de carpetas
        /// La clase File nos permite trabajar con rutas del sistema de archivos
        ///  sin aún abrir ningún archivo.
        verificarEstructuraCarpetas();

        /// 2. Verificamos que el archivo de entrada existe antes de intentar abrirlo
        File archivoEntrada = new File(ARCHIVO_ENTRADA);

        if (!archivoEntrada.exists() || !archivoEntrada.isFile()) {
            System.err.println("ERROR: Archivo no encontrado -> " + ARCHIVO_ENTRADA);
            System.err.println("Ruta esperada: " + archivoEntrada.getAbsolutePath());
            System.err.println("Por favor, coloque el archivo en la carpeta 'datos/' y vuelva a ejecutar.");
            return;
        }

        System.out.println("Archivo de entrada encontrado: " + ARCHIVO_ENTRADA);
        System.out.println("Iniciando procesamiento...\n");

        ///  ------------------------------------
        // PASO 3: Aquí se implementaría la lógica de lectura del CSV, validación de datos,
        // para poder acceder a ellas después (en el caso de mover el archivo)

        // Variable para acumular el total de kilos procesados
        // Usamos double para permitir decimales, aunque en este caso podríamos usar int si solo se manejan kilos enteros
        double totalKilos = 0.0;

        // Contadores para el informe final de procesamiento
        int lineasProcesadas = 0;
        int lineasValidas = 0;
        int lineasInvalidas = 0;

        ///  PASO 4: try-with-resources para manejar la apertura y cierre automático de archivos
        /// Esta es la forma CORRECTA y MODERNA de trabajar con archivos en Java.
        /// Garantiza que los recursos se cierren automáticamente, incluso si ocurre una excepción.
        /// Sin try-with-resources, tendríamos que cerrar manualmente cada recurso, lo cual es propenso a errores (olvidar cerrar, cerrar en el orden incorrecto, etc.)

        try (
                /// ##### RECURSO 1: Archivo de entrada para lectura
                // FileReader - Abrir el archivo de entrada para lectura.
                // Es lento para archivos grandes porque hace una llamada al sistema operativo para cada
                // carácter leído. Por eso lo envolvemos en un BufferedReader.

                /// BufferedReader - Permite leer el archivo línea por línea, lo que es mucho más eficiente.
                /// Además, tiene un mé_todo readLine() que facilita la lectura de líneas completas.
                BufferedReader lector = new BufferedReader(
                        new FileReader(archivoEntrada, StandardCharsets.UTF_8)
                );


                /// #### RECURSO 2: PrintWriter para escribir el archivo de envíos válidos
                // PrintWriter - Permite escribir texto de forma sencilla. Tiene métodos como println() que facilitan la escritura de líneas completas.
                // Usamos UTF-8 para evitar problemas con caracteres especiales.

                /// FileWriter - Si quisiéramos escribir sin BufferedWriter, podríamos usar FileWriter directamente, pero PrintWriter nos da más comodidad para escribir texto.
                /// append = false (sobrescribe el archivo cada vez que se ejecuta el programa). Si fuera true, agregaría al final del archivo en lugar de sobrescribirlo.
                // autoFlush = true (hace que cada llamada a println() se escriba inmediatamente en el archivo, sin necesidad de llamar a flush() manualmente).
                PrintWriter escritorValidos = new PrintWriter(
                        new FileWriter(ARCHIVO_VALIDOS, java.nio.charset.StandardCharsets.UTF_8, false), true
                );

                /// #### RECURSO 3: PrintWriter para escribir el archivo de errores
                // Mismo concepto que el anterior. El segundo parámetro de FileWriter es (true)
                // para habilitar el modo append, lo que significa que si el archivo ya existe, se agregarán los nuevos errores al final del archivo en lugar de sobrescribirlo.
                PrintWriter escritorErrores = new PrintWriter(
                        new FileWriter(ARCHIVO_ERRORES, java.nio.charset.StandardCharsets.UTF_8, true), true
                );
        ) {
            /// --- INICIO DEL BLOQUE DE PROCESAMIENTO ---
            /// Escribimos una cabecera en el log de errores para identificar la sesión.
            // DateTimeFormatter define el PATRÓN de formato de la fecha
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaHoraActual = LocalDateTime.now().format(formato); // 2026-03-25 14:30:45

            escritorErrores.println("=================================================");
            escritorErrores.println("--- Procesamiento iniciado: " + fechaHoraActual + " ---");
            escritorErrores.println("=================================================");

            // Escribimos la cabecera del CSV de salida.
            escritorValidos.println("ID_Envio; Destino; Kilos; Estado");

            ///  BUCLE PRINCIPAL DE LECTURA
            ///  readLine() devuelve null cuando se llega al final del archivo, por eso usamos esa condición para terminar el bucle.
            //
            // El patrón (linea = br.readLine()) != null es común en Java para leer archivos línea por línea.
            // 1. Lee la línea y la asigna a la variable 'linea'.
            // 2. Comprueba si es null (fin del archivo)
            // 3. Si no es null, entra al bloque del bucle para procesar esa línea.

            String linea;
            int numeroLinea = 0; // Contador para llevar el número de línea actual (útil para mensajes de error)

            try {
                while ((linea = lector.readLine()) != null) {
                    numeroLinea++;

                    /// trim() elimina espacios en blanco al inicio y al final de la línea, lo que ayuda a evitar errores de formato.
                    linea = linea.trim();

                    // Si la línea está vacía (línea en blanco en el CSV), la ignoramos
                    if (linea.isEmpty()) {
                        continue; // Salta a la siguiente iteración del bucle
                    }

                    //Ignoramos la línea de cabecera si el CSV la tiene.
                    // Esto sirve para evitar que la cabecera se procese como un envío válido o inválido.
                    if (linea.startsWith("ID_Envio")) {
                        continue;
                    }

                    lineasProcesadas++;

                    /// ----- VALIDACIÓN DE CADA LÍNEA -----
                    Resultado resultado = validarYProcesarLinea(
                            linea, numeroLinea, escritorValidos, escritorErrores
                    );




                }
            } catch (Exception e) {
                escritorErrores.println("ERROR FATAL: Ocurrió un error inesperado durante el procesamiento.");
                escritorErrores.println("Detalles del error: " + "Línea de error " + numeroLinea + e.getMessage());
                e.printStackTrace(escritorErrores); // Escribe la traza completa del error en el log
            }

        } catch (Exception e) {
            throw new RuntimeException("Fallo al procesar el archivo de entrada '" + ARCHIVO_ENTRADA + "'", e);
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
        String destino = campos[1].trim();
        String kilosStr = campos[2].trim();
        String estado = campos[3].trim();


        /**
         * escribe una linea de error en el archivo errores.log con formato estandar
         *
         *
         *
         */
        /// VALIDACIÓN 2:

        if (idEnvio.isEmpty()){
                registrarError(escritorerrores, numerodelinea, linea, "idEnvio vacio");
                return new Resultado ( false, 0.0);
    }
        if (destino.isEmpty()){
            registrarError(escritorerrores, numerodelinea, linea, "Destino Vacio");
            return new Resultado ( false, 0.0);
        }

        if (kilosStr.isEmpty()){
            registrarError(escritorerrores, numerodelinea, linea, "kilos Str Vacio");
            return new Resultado ( false, 0.0);
        }

        if (estado.isEmpty()){
            registrarError(escritorerrores, numerodelinea, linea, "Estado Vacio");
            return new Resultado ( false, 0.0);
        }


                 /// VALIDACIÓN 3: CAMPO KILOS TIENE QUE SER VÁLIDO



            /*
            1. kILOSsTR
            2. Kilos= double
            3. kilos null, pero en try con los datos pareados de kilosStr
            4. kilos ! = null, si no que es un número decimal parseado de kilos, el catch es para que si peta se registre en la clase de los errores
             */
        double kilos;

        try {


                   kilos = Double.parseDouble(kilosStr);

                   if (kilos <=0) {
             registrarError( escritorerrores, numerodelinea, linea, "Kilos debe ser un número positivo");
               return new Resultado(false, 0.0);
            }

        } catch(NumberFormatException e){
            registrarError(escritorerrores, numerodelinea,linea, "Kilos no es un número válido");
            return  new Resultado(false, 0.0);
        }


        // Si llegamos aqui, la linea ha posado Todas las validaciones
        // La escribimos en el archivo de envíos válidos

        escritorValidos.println(idEnvio + SEPARADOR + destino +  SEPARADOR + kilos + SEPARADOR + estado);

        return new Resultado(true, kilos);

    }





    /**
     * Escribe una línea de error en el archivo errores.log con formato estándar.
     *
     * @param escritorErrores El PrintWriter para escribir errores
     * @param numeroLinea El número de línea donde ocurrió el error
     * @param lineaOriginal La línea original del CSV que causó el error
     * @param motivo El motivo del error (descripción del problema)
     */

    /// DRY -> DON'T REPEAT YOURSELF

    private static void registrarError(PrintWriter escritorErrores, int numeroLinea, String lineaOriginal, String motivo) {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = LocalDateTime.now().format(formato);
        //
        //        // Escribimos una línea de log con formato estructurado:
        // [TIMESTAMP] | Línea N | Motivo | Contenido original
        escritorErrores.println("[" + timestamp + "] | Línea " + numeroLinea
                + " | ERROR: " + motivo
                + " | Contenido: '" + lineaOriginal + "'");

        // También imprimimos un mensaje en la consola para informar al usuario
        System.out.println("  [!] Línea " + numeroLinea + " rechazada: " + motivo);
    }


    private static class Resultado{
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