package org.example

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main(args: Array<String>) {
    try {
        val carpeta = File(args[1])
        val todasLasPalindromas = mutableSetOf<String>()
        carpeta.listFiles()?.forEach { archivo ->
            if (archivo.isFile) {
                println("Creando proceso para archivo: ${archivo.name}")
                val classPath = System.getProperty("java.class.path")
                val processBuilder = ProcessBuilder("java", "-cp", classPath, "org.example.Subproceso", archivo.absolutePath)
                val process = processBuilder.start()
                val pid = process.pid()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val output = reader.readText()
                println("Salida del subproceso ${pid} para ${archivo.name}: \n$output")
                process.waitFor()
                println("Subproceso ${pid} para ${archivo.name} finalizado")
            }
        }
        val outputDirectory = File("./FicherosGenerados")
        outputDirectory.listFiles()?.forEach { resultFile ->
            if (resultFile.isFile) {
                println("Leyendo fichero generado: ${resultFile.name}")
                resultFile.forEachLine { line ->
                    todasLasPalindromas.addAll(line.split(", ").filter { it.isNotEmpty() })
                }
            }
        }
        println("Palabras palíndromas encontradas en todos los ficheros:")
        println(todasLasPalindromas.joinToString(", "))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
class Subproceso {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val archivo = File(args[0])
            procesarArchivo(archivo.absolutePath)
        }
        fun procesarArchivo(inputFilePath: String) {
            val outputDirectory = File("./FicherosGenerados")
            val outputFilePath = "${outputDirectory.absolutePath}/${ProcessHandle.current().pid()}.txt"
            val outputFile = File(outputFilePath)
            outputFile.printWriter().use { writer ->
                val palindromas = mutableSetOf<String>()
                File(inputFilePath).forEachLine { line ->
                    val words = line.split("\\s+".toRegex())
                    words.forEach { word ->
                        if (isPalindrome(word)) {
                            val cleanword = word.replace(Regex("[^a-zA-Z]"), "")
                            palindromas.add(cleanword)
                        }
                    }
                }
                val result = palindromas.joinToString(", ")
                println("Palíndromas en $inputFilePath: $result")
                writer.println(result)
            }
        }
        fun isPalindrome(word: String): Boolean {
            val cleanedWord = word.lowercase().replace(Regex("[^a-zA-Z]"), "")
            return cleanedWord == cleanedWord.reversed() && cleanedWord.length > 1
        }
    }
}
