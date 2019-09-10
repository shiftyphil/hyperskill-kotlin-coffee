package machine

import java.util.Scanner

class CoffeeMachine(val waterSupply: Int, val milkSupply: Int, val beanSupply: Int) {
    fun getPossibleCups(): Int {
        return minOf(waterSupply / 200, milkSupply / 50, beanSupply / 15)
    }
}


fun main() {
    val scanner = Scanner(System.`in`)

    println("Write how many ml of water the coffee machine has: ")
    val waterSupply = scanner.nextInt()
    println("Write how many ml of milk the coffee machine has: ")
    val milkSupply = scanner.nextInt()
    println("Write how many g of coffee beans the coffee machine has: ")
    val beanSupply = scanner.nextInt()

    val machine = CoffeeMachine(waterSupply, milkSupply, beanSupply)

    println("Write how many cups of coffee you will need: ")
    val requiredCups = scanner.nextInt()
    val possibleCups = machine.getPossibleCups()

    val difference = possibleCups - requiredCups

    println(when {
        difference > 0 -> "Yes, I can make that amount of coffee (and even $difference more than that)"
        difference < 0 -> "No, I can make only $possibleCups cups of coffee"
        else -> "Yes, I can make that amount of coffee"
    })
}