package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single profile row
    val gender: String = "Female",
    val age: Int = 24,
    val height: String = "165 cm",
    val weight: String = "55 kg",
    val bodyShape: String = "Hourglass",
    val skinTone: String = "Fair",
    val hairColor: String = "Brown",
    val eyeColor: String = "Brown",
    val preferredStyle: String = "Minimalist Elegant",
    val favoriteBrands: String = "Zara, Mango, COS",
    val favoriteColors: String = "Lavender, White, Sage Green",
    val dislikedColors: String = "Neon Orange, Yellow"
)

@Entity(tableName = "wardrobe_items")
data class WardrobeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Top, Bottom, Shoes, Accessory, Outerwear, Dress
    val color: String, // Hex string or Color Name
    val imageUrl: String? = null, // Path to local photo or placeholder
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_looks")
data class SavedLook(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val gender: String,
    val occasion: String,
    val top: String,
    val bottom: String,
    val shoes: String,
    val accessories: String,
    val bag: String = "",
    val watch: String = "",
    val jewelry: String = "",
    val whyItWorks: String,
    val matchingColors: String,
    val stylingTips: String,
    val confidenceRating: Int = 95,
    val budgetEstimate: String = "$150",
    val alternativeSuggestions: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
