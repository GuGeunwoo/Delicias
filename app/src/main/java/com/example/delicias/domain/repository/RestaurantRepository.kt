package com.example.delicias.domain.repository

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.RestaurantMinimal
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getRestaurantsFromRemote() : Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>
    fun getAllRestaurantMinimals(): Flow<List<RestaurantMinimal>>
    fun getRestaurantById(id: Long): Flow<Restaurant>
    suspend fun insertRestaurant(restaurant: Restaurant)
    suspend fun deleteRestaurant(restaurant: Restaurant)
    suspend fun deleteAllRestaurant()
    suspend fun updateRestaurantMinimals(restaurantMinimals: List<RestaurantMinimal>)
    suspend fun updateDistanceOrderOfRestaurantById(id: Long, distanceOrder: Int)
    fun getAllFavoriteBreakfast(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteLunch(): Flow<List<RestaurantMinimal>>
    fun getAllFavoriteDinner(): Flow<List<RestaurantMinimal>>
    fun searchForBreakfast(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForLunch(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchForDinner(searchquery: String): Flow<List<RestaurantMinimal>>
    fun searchRestaurant(searchquery: String): Flow<List<Restaurant>>
    fun getRestaurantMinimalOrderByName(): Flow<List<RestaurantMinimal>>
    fun getRestaurantMinimalOrderByDistance(): Flow<List<RestaurantMinimal>>
    suspend fun updateBreakfast()
    suspend fun updateLunch()
    suspend fun updateDinner()
}