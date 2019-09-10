package machine

import java.util.*

enum class Supply(val label: String, val refillUnit: String, val fillable: Boolean = true, val unit: String = "") {
    WATER("water", "ml of water"),
    MILK("milk", "ml of milk"),
    BEANS("coffee beans", "grams of coffee beans"),
    CUPS("disposable cups", "disposable cups of coffee"),
    MONEY("money", "", false, "$");

    companion object {
        fun getFillable() = values().filter { it.fillable }
    }
}

enum class Coffee(val label: String, val key: String, val supplies: Map<Supply, Int>) {
    ESPRESSO("espresso", "1", mapOf(
            Supply.WATER to 250,
            Supply.BEANS to 16,
            Supply.CUPS to 1,
            Supply.MONEY to -4)),
    LATTE("latte", "2", mapOf(
            Supply.WATER to 350,
            Supply.MILK to 75,
            Supply.BEANS to 20,
            Supply.CUPS to 1,
            Supply.MONEY to -7)),
    CAPPUCCINO("cappuccino", "3", mapOf(
            Supply.WATER to 200,
            Supply.MILK to 100,
            Supply.BEANS to 12,
            Supply.CUPS to 1,
            Supply.MONEY to -6)),
    BACK("to main menu", "back", mapOf());

    companion object {
        fun getByKey(key: String): Coffee? =
                values().find { it.key == key }
    }
}

class CoffeeMachine(supplyLevels: Map<Supply, Int>) {
    private val supplyLevels: MutableMap<Supply, Int> = supplyLevels.toMutableMap()

    private fun useSupply(amount: Int, type: Supply) {
        supplyLevels[type] = (supplyLevels[type] ?: 0) - amount
    }

    fun getSupplyLevelText(): Iterable<String> =
            supplyLevels.map { "${it.key.unit}${it.value} of ${it.key.label}" }

    fun canMakeCoffee(type: Coffee): Boolean =
            type.supplies.all { it.value <= (supplyLevels[it.key] ?: 0) }

    fun shortSupplies(type: Coffee) =
            type.supplies.filter { it.value > (supplyLevels[it.key] ?: 0) }.map { it.key.name }

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
            Supply.WATER to 400,
            Supply.MILK to 540,
            Supply.BEANS to 120,
            Supply.CUPS to 9,
            Supply.MONEY to 550
    ))

    mainloop@ while (true) {
        print("Write action (buy, fill, take, remaining, exit): ")

        when (scanner.next()) {
            "buy" -> {
                println()
                print("What do you want to buy? ${Coffee.values().joinToString { "${it.key} - ${it.label}" }}: ")
                val choice = Coffee.getByKey(scanner.next())
                println()
                when (choice) {
                    null, Coffee.BACK -> continue@mainloop
                    else -> {
                        if (machine.canMakeCoffee(choice)) {
                            println("I have enough resources, making you a coffee!")
                            machine.makeCoffee(choice)
                        } else {
                            machine.shortSupplies(choice).map { println("Sorry, not enough ${it}!") }
                        }
                    }
                }
            }
            "fill" -> {
                for (type in Supply.getFillable()) {
                    println()
                    print("Write how many ${type.refillUnit} do you want to add: ")
                    val amount = scanner.nextInt()
                    machine.refillSupply(amount, type)
                }
            }
            "take" -> {
                println()
                println("I gave you \$${machine.takeMoney()}")
            }
            "remaining" -> {
                println()
                println("The coffee machine has:")
                machine.getSupplyLevelText().map { println(it) }
            }
            "exit" -> {
                return
            }
            else -> println("I don't understand.")
        }
    }
}