package org.example.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.models.Address
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DatabaseFactory {
    suspend fun init(url: String, driver: String, user: String, pass: String) = withContext(Dispatchers.IO) {
        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            password = pass
            maximumPoolSize = 10
        }
        Database.connect(HikariDataSource(config))
        newSuspendedTransaction {
            SchemaUtils.create(Users, CoffeeShopAddresses, Products, Orders, OrderItems)
            if (Products.selectAll().empty()) {
                predefinedProducts.forEach { product ->
                    Products.insert {
                        it[name] = product.productName
                        it[weightGrams] = product.weight.filter { it.isDigit() }.toIntOrNull() ?: 0
                        it[description] = product.description
                        it[fullDescription] = product.fullDescription
                        it[priceCents] = (product.price.filter { it.isDigit() || it == '.' }.toDouble() * 100).toInt()
                        it[imageName] = product.productImageName
                        it[productType] = product.productType
                        it[allergens] = Json.encodeToString(product.allergens)
                    }
                }
            }
            if (CoffeeShopAddresses.selectAll().empty()) {
                mockedAddresses.forEach { mockedAddress ->
                    CoffeeShopAddresses.insert {
                        it[address] = mockedAddress.address
                        it[city] = mockedAddress.city
                    }
                }
            }
//            exec("ALTER TABLE orders ALTER COLUMN order_state SET DEFAULT 'ORDERED';")
        }
    }
}

val mockedAddresses = listOf(
    Address(1, "Витебск", "ул. Ленина, 12"),
    Address(2, "Минск", "пр. Победы, 45"),
    Address(3, "Минск", "ул. Горького, 3"),
    Address(4, "Гродно", "наб. Реки, 7"),
    Address(5, "Полоцк", "пер. Цветочный, 9")
)

val predefinedProducts = listOf(

    // --- ГОРЯЧИЙ КОФЕ ---
    ProductEntity(
        productName = "Эспрессо",
        weight = "60 мл",
        description = "Горячий напиток",
        fullDescription = "молотый кофе, вода.",
        allergens = emptyList(),
        price = "2.50 BYN",
        productImageName = "coffee_espresso",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Американо",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "молотый кофе, вода.",
        allergens = emptyList(),
        price = "2.80 BYN",
        productImageName = "coffee_americano",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Капучино",
        weight = "300 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, молочная пена.",
        allergens = listOf("молочные продукты"),
        price = "3.20 BYN",
        productImageName = "coffee_cappuccino",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Латте",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        productImageName = "coffee_latte",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Мокко",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, шоколадный сироп.",
        allergens = listOf("молочные продукты"),
        price = "3.80 BYN",
        productImageName = "coffee_mocha",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Флэт Уайт",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "двойной эспрессо, молоко.",
        allergens = listOf("молочные продукты"),
        price = "3.60 BYN",
        productImageName = "coffee_flatwhite",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Раф кофе",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, сливки, ванильный сахар.",
        allergens = listOf("молочные продукты"),
        price = "3.90 BYN",
        productImageName = "coffee_raf",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Айс Латте",
        weight = "400 мл",
        description = "Холодный кофе",
        fullDescription = "эспрессо, молоко, лёд.",
        allergens = listOf("молочные продукты"),
        price = "3.70 BYN",
        productImageName = "coffee_ice_latte",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Карамельный макиато",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, карамельный сироп.",
        allergens = listOf("молочные продукты"),
        price = "4.00 BYN",
        productImageName = "coffee_caramel_macchiato",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Кофе по-ирландски",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "кофе, сливки, сахар, ирландский ликёр.",
        allergens = listOf("молочные продукты", "Алкоголь"),
        price = "4.50 BYN",
        productImageName = "coffee_irish",
        productType = ProductType.DRINK,
    ),

    // --- ХОЛОДНЫЕ НАПИТКИ ---
    ProductEntity(
        productName = "Холодный чай с лимоном",
        weight = "400 мл",
        description = "Холодный напиток",
        fullDescription = "чай, лимон, сахар, лёд.",
        allergens = listOf("лимон"),
        price = "2.50 BYN",
        productImageName = "cold_tea_lemon",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Домашний лимонад",
        weight = "450 мл",
        description = "Холодный напиток",
        fullDescription = "вода, лимон, сахар, мята.",
        allergens = listOf("лимон", "мята"),
        price = "2.80 BYN",
        productImageName = "cold_lemonade",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Матча латте (холодный)",
        weight = "350 мл",
        description = "Холодный напиток",
        fullDescription = "матча, молоко, лёд.",
        allergens = listOf("молочные продукты"),
        price = "4.00 BYN",
        productImageName = "cold_matcha_latte",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Молочный коктейль ванильный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "молоко, мороженое, ваниль.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        productImageName = "milkshake_vanilla",
        productType = ProductType.DRINK,
    ),
    ProductEntity(
        productName = "Молочный коктейль шоколадный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "молоко, мороженое, шоколадный сироп.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        productImageName = "milkshake_chocolate",
        productType = ProductType.DRINK,
    ),

    // --- ДЕСЕРТЫ ---
    ProductEntity(
        productName = "Чизкейк Нью-Йорк",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "сливочный сыр, печенье, яйца, сахар.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "4.20 BYN",
        productImageName = "dessert_cheesecake_ny",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Тирамису",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "маскарпоне, яйца, сахар, кофе, печенье савоярди.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "4.50 BYN",
        productImageName = "dessert_tiramisu",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Маффин с черникой",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "мука, яйца, сахар, масло, черника.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "2.20 BYN",
        productImageName = "dessert_muffin_blueberry",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Круассан с миндалём",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "слоёное тесто, миндальная начинка.",
        allergens = listOf("молочные продукты", "глютен", "орехи"),
        price = "2.80 BYN",
        productImageName = "croissant_almond",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Шоколадный брауни",
        weight = "110 г",
        description = "Десерт",
        fullDescription = "шоколад, мука, сахар, масло, яйца.",
        allergens = listOf("молочные продукты", "глютен", "орехи"),
        price = "3.00 BYN",
        productImageName = "dessert_brownie",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Пирог с яблоками и корицей",
        weight = "130 г",
        description = "Выпечка",
        fullDescription = "мука, яблоки, корица, сахар.",
        allergens = listOf("глютен", "яблоки"),
        price = "3.20 BYN",
        productImageName = "pie_apple_cinnamon",
        productType = ProductType.FLOUR,
    ),

    // --- ЗАВТРАКИ / СЭНДВИЧИ ---
    ProductEntity(
        productName = "Сэндвич с ветчиной и сыром",
        weight = "180 г",
        description = "Сэндвич",
        fullDescription = "хлеб, ветчина, сыр, салат.",
        allergens = listOf("молочные продукты", "глютен"),
        price = "3.80 BYN",
        productImageName = "sandwich_ham_cheese",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Бейгл с лососем и сливочным сыром",
        weight = "200 г",
        description = "Бейгл",
        fullDescription = "бейгл, лосось, сливочный сыр, салат.",
        allergens = listOf("молочные продукты", "глютен", "рыба"),
        price = "4.50 BYN",
        productImageName = "bagel_salmon",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Тост с авокадо",
        weight = "160 г",
        description = "Завтрак",
        fullDescription = "хлеб, авокадо, специи.",
        allergens = listOf("глютен"),
        price = "3.90 BYN",
        productImageName = "toast_avocado",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Овсяная каша с ягодами",
        weight = "250 г",
        description = "Завтрак",
        fullDescription = "овсянка, молоко, ягоды.",
        allergens = listOf("молочные продукты", "глютен"),
        price = "3.00 BYN",
        productImageName = "oatmeal_berries",
        productType = ProductType.FLOUR,
    ),
    ProductEntity(
        productName = "Йогурт с гранолой",
        weight = "200 г",
        description = "Завтрак",
        fullDescription = "йогурт, гранола, ягоды.",
        allergens = listOf("молочные продукты", "орехи (в граноле)"),
        price = "2.80 BYN",
        productImageName = "yogurt_granola",
        productType = ProductType.FLOUR,
    ),
)

data class ProductEntity(
    val productName: String,
    val price: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val allergens: List<String>,
    val productType: ProductType,
    val productImageName: String,
)