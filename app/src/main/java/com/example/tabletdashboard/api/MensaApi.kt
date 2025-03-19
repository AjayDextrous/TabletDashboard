package com.example.tabletdashboard.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

data class MensaMenuResponse(
    @SerializedName("number") val number: Int,
    @SerializedName("year") val year: Int,
    @SerializedName("days") val days: List<MenuDay>,
    @SerializedName("version") val version: String
)

data class MenuDay(
    @SerializedName("date") val date: String,
    @SerializedName("dishes") val dishes: List<Dish>
)

data class Dish(
    @SerializedName("name") val name: String,
    @SerializedName("prices") val prices: Prices,
    @SerializedName("labels") val labels: List<Label>,
    @SerializedName("dish_type") val dishType: DishType
)

data class Prices(
    @SerializedName("students") val students: PriceDetail,
    @SerializedName("staff") val staff: PriceDetail,
    @SerializedName("guests") val guests: PriceDetail
)

data class PriceDetail(
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("price_per_unit") val pricePerUnit: Double,
    @SerializedName("unit") val unit: String
)

enum class Label {
    @SerializedName("CEREAL") CEREAL,
    @SerializedName("GLUTEN") GLUTEN,
    @SerializedName("LACTOSE") LACTOSE,
    @SerializedName("MILK") MILK,
    @SerializedName("SESAME") SESAME,
    @SerializedName("SHELL_FRUITS") SHELL_FRUITS,
    @SerializedName("VEGETARIAN") VEGETARIAN,
    @SerializedName("WHEAT") WHEAT,
    @SerializedName("ALCOHOL") ALCOHOL,
    @SerializedName("CELERY") CELERY,
    @SerializedName("DYESTUFF") DYESTUFF,
    @SerializedName("FISH") FISH,
    @SerializedName("PRESERVATIVES") PRESERVATIVES,
    @SerializedName("SULFITES") SULFITES,
    @SerializedName("SULPHURS") SULPHURS,
    @SerializedName("ANTIOXIDANTS") ANTIOXIDANTS,
    @SerializedName("CHICKEN_EGGS") CHICKEN_EGGS,
    @SerializedName("MUSTARD") MUSTARD,
    @SerializedName("SWEETENERS") SWEETENERS,
    @SerializedName("BEEF") BEEF,
    @SerializedName("GARLIC") GARLIC,
    @SerializedName("MEAT") MEAT,
    @SerializedName("SOY") SOY,
    @SerializedName("BARLEY") BARLEY,
    @SerializedName("PHOSPHATES") PHOSPHATES,
    @SerializedName("PORK") PORK,
    @SerializedName("VEGAN") VEGAN,
    OTHER
}

enum class DishType {
    @SerializedName("Tagessupe, Brot, Obst") DAILY_SOUP,
    @SerializedName("Fisch") FISH,
    @SerializedName("Süßspeise") SWEET_DISH,
    @SerializedName("Fleisch") MEAT,
    @SerializedName("Pasta") PASTA,
    @SerializedName("Pizza") PIZZA,
    @SerializedName("Wok") WOK,
    @SerializedName("Dessert (Glas)") DESSERT,
    @SerializedName("Studitopf") STUDITOPF,
    OTHER
}

class EnumTypeAdapter<T : Enum<T>>(private val enumClass: Class<T>) : JsonDeserializer<T>,
    JsonSerializer<T> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {
        val value = json?.asString
        return try {
            enumClass.enumConstants!!.first { it.name.equals(value, ignoreCase = true) }
        } catch (e: Exception) {
            enumClass.enumConstants!!.first { it.name == "OTHER" }
        }
    }

    override fun serialize(src: T, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.name)
    }
}

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Label::class.java, EnumTypeAdapter(Label::class.java))
    .registerTypeAdapter(DishType::class.java, EnumTypeAdapter(DishType::class.java))
    .create()


interface MensaApi {
// https://tum-dev.github.io/eat-api/<canteen>/<year>/<week-number>.json

    @GET("{canteen}/{year}/{week-number}.json")
    suspend fun getMenu(
        @Path("canteen") canteen: String,
        @Path("year") year: Int,
        @Path("week-number") weekNumber: Int
    ): MensaMenuResponse
}

object MensaRetrofitInstance {

    val api: MensaApi by lazy {

        val loggingInterceptor = HttpLoggingInterceptor { message -> Log.d(TAG, message) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()

        retrofit2.Retrofit.Builder()
            .baseUrl("https://tum-dev.github.io/eat-api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(MensaApi::class.java)
    }

    private const val TAG = "MensaRetrofitInstance"
}