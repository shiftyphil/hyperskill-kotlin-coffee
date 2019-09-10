package machine

import java.util.Scanner

enum class Supply(val label: String, val refillUnit: String, val fillable: Boolean = true) {
    WATER("water", "ml of water"),
    MILK("milk", "ml of milk"),
    BEANS("coffee beans", "grams of coffee beans"),
    CUPS("disposable cups", "disposable cups of coffee"),
    MONEY("money", "", false);

    companion object {
        fun getFillable() = values().filter { it.fillable }
    }
}

enum class Coffee(val label: String, val num: Int, val supplies: Map<Supply, Int>) {
    ESPRESSO("espresso", 1, mapOf(
            Supply.WATER to 250,
            Supply.BEANS to 16,
            Supply.CUPS to 1,
            Supply.MONEY to -4)),
    LATTE("latte", 2, mapOf(
            Supply.WATER to 350,
            Supply.MILK to 75,
            Supply.BEANS to 20,
            Supply.CUPS to 1,
            Supply.MONEY to -7)),
    CAPPUCCINO("cappuccino", 3, mapOf(
            Supply.WATER to 200,
            Supply.MILK to 100,
            Supply.BEANS to 12,
            Supply.CUPS to 1,
            Supply.MONEY to -6)),
    POISON("poison", -1, mapOf());

    companion object {
        fun getByNum(num: Int): Coffee =
                values().find { it.num == num } ?: POISON
    }
}

class CoffeeMachine(supplyLevels: Map<Supply, Int>) {
    private val supplyLevels: MutableMap<Supply, Int> = supplyLevels.toMutableMap()

    private fun useSupply(amount: Int, type: Supply) {
        supplyLevels[type] = (supplyLevels[type] ?: 0) - amount
    }

    fun getSupplyLevelText(): Iterable<String> =
            supplyLevels.map { "${it.value} of ${it.key.label}" }

    fun makeCoffee(type: Coffee) {
        type.supplies.map { useSupply(it.value, it.key) }
    }

    fun refillSupply(amount: Int, type: Supply) {
        when (type) {
            in Supply.getFillable() -> supplyLevels[type] = (supplyLevels[type] ?: 0) + amount
            else -> error("Cannot refill that supply.")
        }
    }

    fun takeMoney(): Int {
        val amount = supplyLevels[Supply.MONEY] ?: 0
        supplyLevels[Supply.MONEY] = 0
        return amount
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    val machine = CoffeeMachine(mapOf(
            Supply.WATER to 1200,
            Supply.MILK to 540,
            Supply.BEANS to 120,
            Supply.CUPS to 9,
            Supply.MONEY to 550
    ))

    println("The coffee machine has:")
    machine.getSupplyLevelText().map { println(it) }

    println()
    println("Write action (buy, fill, take): ")
    val command = scanner.nextLine()

    when (command) {
        "buy" -> {
            print("What do you want to buy? ${Coffee.values().joinToString { "${it.num} - ${it.name}" }}: ")
            val choice = scanner.nextInt()
            machine.makeCoffee(Coffee.getByNum(choice))
        }
        "fill" -> {
            for (type in Supply.getFillable()) {
                print("Write how many ${type.refillUnit} do you want to add: ")
                val amount = scanner.nextInt()
                machine.refillSupply(amount, type)
            }
        }
        "take" -> {
            println("I gave you \$${machine.takeMoney()}")
        }
    }

    println()
    println("The coffee machine has:")
    machine.getSupplyLevelText().map { println(it) }
}