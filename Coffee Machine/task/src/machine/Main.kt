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

enum class CoffeeMachineState {
    MAIN_MENU,
    BUY_MENU,
    FILL_MENU,
    POWER_OFF
}

class CoffeeMachineInterface(val machine: CoffeeMachine) {
    private var state = CoffeeMachineState.MAIN_MENU
    private var fillItem: Supply = Supply.WATER
    fun running() = state != CoffeeMachineState.POWER_OFF

    fun prompt(): String =
            when (state) {
                CoffeeMachineState.MAIN_MENU ->
                    "Write action (buy, fill, take, remaining, exit): "
                CoffeeMachineState.BUY_MENU ->
                    "What do you want to buy? ${Coffee.values().joinToString { "${it.key} - ${it.label}" }}: "
                CoffeeMachineState.FILL_MENU ->
                    "Write how many ${fillItem.refillUnit} do you want to add: "
                else -> "?"
            }

    fun handleInput(input: String): String =
            when (state) {
                CoffeeMachineState.MAIN_MENU ->
                    when (input) {
                        "buy" -> {
                            state = CoffeeMachineState.BUY_MENU
                            ""
                        }
                        "fill" -> {
                            fillItem = Supply.WATER
                            state = CoffeeMachineState.FILL_MENU
                            ""
                        }
                        "take" -> "I gave you \$${machine.takeMoney()}"
                        "remaining" ->
                            "The coffee machine has:\n" + machine.getSupplyLevelText().joinToString("\n") { it }
                        "exit" -> {
                            state = CoffeeMachineState.POWER_OFF
                            ""
                        }
                        else -> "I don't understand."
                    }
                CoffeeMachineState.BUY_MENU -> {
                    state = CoffeeMachineState.MAIN_MENU
                    when (val choice = Coffee.getByKey(input)) {
                        null, Coffee.BACK -> {
                            ""
                        }
                        else -> {
                            if (machine.canMakeCoffee(choice)) {
                                machine.makeCoffee(choice)
                                "I have enough resources, making you a coffee!"
                            } else {
                                machine.shortSupplies(choice).joinToString("\n") { "Sorry, not enough ${it}!" }
                            }
                        }
                    }
                }
                CoffeeMachineState.FILL_MENU -> {
                    val amount = input.toInt()
                    machine.refillSupply(amount, fillItem)
                    when (fillItem) {
                        Supply.WATER -> fillItem = Supply.MILK
                        Supply.MILK -> fillItem = Supply.BEANS
                        Supply.BEANS -> fillItem = Supply.CUPS
                        Supply.CUPS -> state = CoffeeMachineState.MAIN_MENU
                        else -> fillItem = Supply.MILK
                    }
                    ""
                }
                else -> ""
            }
}

fun main() {
    val scanner = Scanner(System.`in`)

    val machine = CoffeeMachineInterface(CoffeeMachine(mapOf(
            Supply.WATER to 400,
            Supply.MILK to 540,
            Supply.BEANS to 120,
            Supply.CUPS to 9,
            Supply.MONEY to 550
    )))

    while (machine.running()) {
        print(machine.prompt())
        val input = scanner.next()
        println()
        print(machine.handleInput(input))
        println("\n")
    }
}