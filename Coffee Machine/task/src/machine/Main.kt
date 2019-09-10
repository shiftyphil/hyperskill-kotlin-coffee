package machine

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("Write how many cups of coffee you will need: ")
    val numCups = scanner.nextInt()

    println("For $numCups of coffee you will need:")
    println("${numCups * 200} ml of water")
    println("${numCups * 50} ml of milk")
    println("${numCups * 15} g of coffee beans")
}