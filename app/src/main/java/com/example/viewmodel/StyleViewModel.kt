package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class AppScreen {
    Landing,
    Login,
    Dashboard,
    OutfitGenerator,
    ColorAnalysis,
    HairstyleAdvisor,
    MakeupAdvisor,
    WardrobeManager,
    ShoppingAssistant,
    EventStylist,
    TrendExplorer,
    DailyChallenge,
    Profile
}

class StyleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = StyleDatabase.getDatabase(application)
    private val dao = db.styleDao()

    // Screen State
    var currentScreen by mutableStateOf(AppScreen.Landing)
    var previousScreen by mutableStateOf(AppScreen.Landing)

    // User authentication status (simulated login/out with local profile persistence)
    var isLoggedIn by mutableStateOf(false)
    var userEmail by mutableStateOf("")
    var selectedAvatarIndex by mutableStateOf(1)

    // Observable Flows from Database
    val userProfile: StateFlow<UserProfile> = dao.getUserProfileFlow()
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val wardrobeItems: StateFlow<List<WardrobeItem>> = dao.getAllWardrobeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedLooks: StateFlow<List<SavedLook>> = dao.getAllSavedLooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Loading & UI States ---
    var isGeneratingOutfit by mutableStateOf(false)
    var outfitError by mutableStateOf<String?>(null)
    var lastGeneratedOutfit by mutableStateOf<SavedLook?>(null)

    var isAnalyzingColor by mutableStateOf(false)
    var colorAnalysisResult by mutableStateOf<String?>(null)

    var isAdvisingHair by mutableStateOf(false)
    var hairAdviceResult by mutableStateOf<String?>(null)

    var isAdvisingMakeup by mutableStateOf(false)
    var makeupAdviceResult by mutableStateOf<String?>(null)

    var isShoppingLoading by mutableStateOf(false)
    var shoppingResult by mutableStateOf<String?>(null)

    var isEventStylingLoading by mutableStateOf(false)
    var eventStylingResult by mutableStateOf<String?>(null)

    var isTrendsLoading by mutableStateOf(false)
    var trendsResult by mutableStateOf<String?>(null)

    var isDailyChallengeLoading by mutableStateOf(false)
    var dailyChallengeResult by mutableStateOf<String?>(null)

    // --- App Initializer ---
    init {
        // Prepopulate a default profile and wardrobe items if empty
        viewModelScope.launch {
            val existingProfile = dao.getUserProfileDirect()
            if (existingProfile == null) {
                dao.insertUserProfile(UserProfile())
            }

            dao.getAllWardrobeItems().first().let { list ->
                if (list.isEmpty()) {
                    dao.insertWardrobeItem(WardrobeItem(name = "Classic White Linen Shirt", category = "Top", color = "White", notes = "Light, breathable, tailored fit"))
                    dao.insertWardrobeItem(WardrobeItem(name = "Indigo Slim Fit Denim", category = "Bottom", color = "Indigo", notes = "Raw denim, vintage stretch"))
                    dao.insertWardrobeItem(WardrobeItem(name = "Minimalist Leather Sneakers", category = "Shoes", color = "White", notes = "Low top premium calfskin"))
                    dao.insertWardrobeItem(WardrobeItem(name = "Camel Double-Breasted Trench Coat", category = "Outerwear", color = "Beige", notes = "Water-resistant, classic length"))
                    dao.insertWardrobeItem(WardrobeItem(name = "Black Silk Slip Dress", category = "Dress", color = "Black", notes = "Premium mulberry silk"))
                    dao.insertWardrobeItem(WardrobeItem(name = "Gold Statement Hoop Earrings", category = "Accessory", color = "Gold", notes = "Chunky minimalist hoops"))
                }
            }
        }
    }

    // --- Actions ---

    fun navigateTo(screen: AppScreen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    fun handleLogin(email: String) {
        userEmail = email
        isLoggedIn = true
        navigateTo(AppScreen.Dashboard)
    }

    fun handleLogout() {
        isLoggedIn = false
        userEmail = ""
        navigateTo(AppScreen.Landing)
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            dao.insertUserProfile(profile)
        }
    }

    // --- Wardrobe Operations ---
    fun addWardrobeItem(name: String, category: String, color: String, notes: String) {
        viewModelScope.launch {
            dao.insertWardrobeItem(
                WardrobeItem(name = name, category = category, color = color, notes = notes)
            )
        }
    }

    fun deleteWardrobeItem(item: WardrobeItem) {
        viewModelScope.launch {
            dao.deleteWardrobeItem(item)
        }
    }

    // --- Saved Looks Operations ---
    fun saveLook(look: SavedLook) {
        viewModelScope.launch {
            dao.insertSavedLook(look)
        }
    }

    fun deleteLook(look: SavedLook) {
        viewModelScope.launch {
            dao.deleteSavedLook(look)
        }
    }

    fun toggleFavoriteLook(look: SavedLook) {
        viewModelScope.launch {
            dao.updateSavedLook(look.copy(isFavorite = !look.isFavorite))
        }
    }

    // --- AI Outfit Generation ---
    fun generateOutfit(
        occasion: String,
        weather: String,
        useWardrobe: Boolean,
        customBudget: String = ""
    ) {
        isGeneratingOutfit = true
        outfitError = null
        lastGeneratedOutfit = null

        viewModelScope.launch {
            val p = userProfile.value
            val wardrobeList = if (useWardrobe) {
                wardrobeItems.value.joinToString { "${it.category}: ${it.name} (${it.color})" }
            } else {
                "None"
            }

            val budget = customBudget.ifEmpty { "Moderate ($100 - $300)" }

            val systemPrompt = """
                You are "StyleSense AI", an elite personal fashion stylist and image consultant. 
                Your purpose is to provide stunning, luxurious fashion recommendations tailored perfectly to the user's physical characteristics, preferences, event type, and budget.
                You always return a highly structured JSON response with exactly the following keys, containing stylish and descriptive text:
                {
                  "title": "A catchy luxury style name",
                  "top": "Detailed recommendation for top",
                  "bottom": "Detailed recommendation for bottom",
                  "shoes": "Detailed footwear recommendation",
                  "accessories": "Detailed jewelry, glasses, or belt recommendation",
                  "bag": "Recommended handbag, clutch or backpack",
                  "watch": "Recommended timepiece",
                  "jewelry": "Recommended specific jewelry details",
                  "whyItWorks": "Elaborate explanation of why these colors, fits, and fabrics look impeccable together",
                  "matchingColors": "Comma-separated list of coordinating accent colors",
                  "stylingTips": "Professional style advisor secrets (e.g. cuffing, tucking, layering, makeup matching)",
                  "confidenceRating": 95,
                  "budgetEstimate": "Total budget range matching user preferences",
                  "alternativeSuggestions": "Swap options for weather or formality changes"
                }
                Be creative, minimal, and high-end. Avoid boring advice. Ensure color harmony. Do not include markdown code block syntax around the JSON, just the raw JSON object.
            """.trimIndent()

            val prompt = """
                Generate a perfect outfit recommendation based on the following:
                - Gender: ${p.gender}
                - Age: ${p.age}
                - Height: ${p.height}
                - Weight: ${p.weight}
                - Body Shape: ${p.bodyShape}
                - Skin Tone: ${p.skinTone}
                - Hair Color: ${p.hairColor}
                - Eye Color: ${p.eyeColor}
                - Occasion: $occasion
                - Weather conditions: $weather
                - Style Preference: ${p.preferredStyle}
                - Favorite Brands: ${p.favoriteBrands}
                - Budget: $budget
                - Favorite Colors: ${p.favoriteColors}
                - Disliked Colors to Avoid: ${p.dislikedColors}
                - Use Existing Wardrobe Items: ${if (useWardrobe) "Yes" else "No"}
                - User's Wardrobe Inventory: $wardrobeList
                
                Provide stylish details for all fields. If using existing wardrobe items, blend them naturally with 1-2 new additions.
            """.trimIndent()

            val resultText = GeminiClient.queryGemini(prompt, systemPrompt, jsonOutput = true)

            if (resultText == "ERROR_API_KEY_MISSING") {
                outfitError = "Please configure your GEMINI_API_KEY in the Secrets panel."
                isGeneratingOutfit = false
                return@launch
            }

            try {
                val look = parseSavedLook(resultText, "$occasion Style", p.gender, occasion)
                lastGeneratedOutfit = look
            } catch (e: Exception) {
                outfitError = "Stylist API parsing error: ${e.message}"
            } finally {
                isGeneratingOutfit = false
            }
        }
    }

    private fun parseSavedLook(jsonStr: String, defaultTitle: String, gender: String, occasion: String): SavedLook {
        try {
            val startIndex = jsonStr.indexOf("{")
            val endIndex = jsonStr.lastIndexOf("}")
            if (startIndex != -1 && endIndex != -1 && endIndex >= startIndex) {
                val cleaned = jsonStr.substring(startIndex, endIndex + 1)
                val json = JSONObject(cleaned)
                return SavedLook(
                    title = json.optString("title", defaultTitle),
                    gender = gender,
                    occasion = occasion,
                    top = json.optString("top", "Recommended Luxury Top"),
                    bottom = json.optString("bottom", "Tailored Pleated Bottoms"),
                    shoes = json.optString("shoes", "Premium Footwear"),
                    accessories = json.optString("accessories", "Signature Sunglasses & Belt"),
                    bag = json.optString("bag", "Luxury Bag Accent"),
                    watch = json.optString("watch", "Classic Quartz Watch"),
                    jewelry = json.optString("jewelry", "Dainty Metallic Jewelry"),
                    whyItWorks = json.optString("whyItWorks", "The perfect marriage of comfort and haute style."),
                    matchingColors = json.optString("matchingColors", "Lavender, Off-white, Metallic Silver"),
                    stylingTips = json.optString("stylingTips", "Roll up the sleeves loosely and tuck the hem slightly to emphasize proportions."),
                    confidenceRating = json.optInt("confidenceRating", 95),
                    budgetEstimate = json.optString("budgetEstimate", "$150 - $250"),
                    alternativeSuggestions = json.optString("alternativeSuggestions", "Swap heels for sneakers for daytime city walks.")
                )
            }
        } catch (e: Exception) {
            // Ignored, fallback below
        }
        return SavedLook(
            title = defaultTitle,
            gender = gender,
            occasion = occasion,
            top = "Luxe Fitted Top",
            bottom = "Modern High-Waisted Bottom",
            shoes = "Chic Comfort Footwear",
            accessories = "Classic Accessories",
            whyItWorks = jsonStr,
            matchingColors = "Brand Tones",
            stylingTips = "Focus on color blocking and minimal proportions."
        )
    }

    // --- AI Color Analysis ---
    fun analyzeColorPalette() {
        isAnalyzingColor = true
        colorAnalysisResult = null

        viewModelScope.launch {
            val p = userProfile.value
            val systemPrompt = "You are an expert Color Theory Stylist. Provide fashion palettes in markdown."
            val prompt = """
                Perform an advanced Color Analysis based on the following physical features:
                - Skin Tone: ${p.skinTone}
                - Hair Color: ${p.hairColor}
                - Eye Color: ${p.eyeColor}
                
                Please generate:
                1. **Your Season Color Palette** (e.g., Deep Autumn, Cool Summer, Bright Spring).
                2. **Best Colors to Wear**: Specific shades that elevate skin radiance and match eye color. Include Hex codes.
                3. **Colors to Avoid**: Shades that might wash out or clash with the undertones.
                4. **Perfect Hair Dye Colors**: Highlighting shades.
                5. **Makeup Color Matching**: Best Lipstick, Blush, and Eyeshadow tones.
                6. **Jewelry Recommendations**: Yellow Gold, White Gold/Silver, or Rose Gold suitability.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            colorAnalysisResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isAnalyzingColor = false
        }
    }

    // --- AI Hairstyle Advisor ---
    fun getHairstyleAdvice(faceShape: String, desiredLength: String, occasion: String) {
        isAdvisingHair = true
        hairAdviceResult = null

        viewModelScope.launch {
            val p = userProfile.value
            val systemPrompt = "You are a Master Hair Stylist. Recommend the absolute best cuts and styles."
            val prompt = """
                Recommend the absolute best hairstyles based on:
                - Gender: ${p.gender}
                - Face Shape: $faceShape
                - Hair Texture / Color: ${p.hairColor}
                - Preferred Hair Length: $desiredLength
                - Style Occasion: $occasion
                
                Please recommend:
                1. **3 Iconic Haircuts**: For Long, Medium, and Short variations suited to this face shape.
                2. **Styling Guide**: How to set it, what products to use, and volume control.
                3. **Formality Adaptations**: Transforming from Professional to Party or Wedding style.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            hairAdviceResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isAdvisingHair = false
        }
    }

    // --- AI Makeup Advisor ---
    fun getMakeupAdvice(lookFormality: String, undertone: String) {
        isAdvisingMakeup = true
        makeupAdviceResult = null

        viewModelScope.launch {
            val p = userProfile.value
            val systemPrompt = "You are a professional Celebrity Makeup Artist. Suggest high-end makeup routines."
            val prompt = """
                Create a custom Makeup Advisory for:
                - Skin Tone: ${p.skinTone}
                - Eye Color: ${p.eyeColor}
                - Hair Color: ${p.hairColor}
                - Theme/Occasion: $lookFormality
                - Skin Undertone: $undertone
                
                Provide detailed, premium suggestions for:
                1. **Foundation & Concealer Shade**: Undertone balancing guidelines.
                2. **Eyeshadow & Eyeliner combos**: Color match to make eyes pop.
                3. **Lipstick & Blush**: Perfect matching shades.
                4. **Highlighter & Contour**: Sculpting instructions.
                5. **Mascara & Brow styling**.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            makeupAdviceResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isAdvisingMakeup = false
        }
    }

    // --- AI Shopping Assistant ---
    fun getShoppingRecommendations(styleSearchQuery: String, maxBudget: String) {
        isShoppingLoading = true
        shoppingResult = null

        viewModelScope.launch {
            val systemPrompt = "You are an elite Personal Shopping Assistant. Recommend real high-quality brands and products."
            val prompt = """
                The user wants shopping recommendations for: "$styleSearchQuery"
                - Style Preferences: ${userProfile.value.preferredStyle}
                - Maximum Budget: $maxBudget
                
                Recommend:
                1. **Brand Tiers**: High-end designer vs. Quality-value brands.
                2. **Specific Items**: Exact styling recommendations to look for.
                3. **Smart Investment advice**: Which parts are worth spending more on, and where to save.
                4. **Alternative Options**: Versatile replacements.
                5. **Smart Shopping List**: Items to check off.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            shoppingResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isShoppingLoading = false
        }
    }

    // --- AI Event Stylist ---
    fun getEventStyling(event: String) {
        isEventStylingLoading = true
        eventStylingResult = null

        viewModelScope.launch {
            val p = userProfile.value
            val systemPrompt = "You are a world-class Event Stylist. Create immaculate lookbooks for special gatherings."
            val prompt = """
                Design a custom lookbook for a: "$event" event.
                - Gender: ${p.gender}
                - Body Shape: ${p.bodyShape}
                - Preferred Vibe: ${p.preferredStyle}
                
                Include:
                1. **The Hero Outfit**: Detailed description of the centerpiece.
                2. **Accessory Coordination**: Bags, watch, jewelry, scarves.
                3. **Footwear Selection**: Balancing elegance and comfort.
                4. **Grooming/Beauty Guide**: Hairstyles and makeup.
                5. **Etiquette & Formality Tips**: Styling boundaries for this specific event.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            eventStylingResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isEventStylingLoading = false
        }
    }

    // --- AI Trend Explorer ---
    fun exploreTrends() {
        isTrendsLoading = true
        trendsResult = null

        viewModelScope.launch {
            val systemPrompt = "You are a Fashion Trend Forecaster. Present hot seasonal trends in an exciting, premium format."
            val prompt = """
                Analyze the absolute latest global fashion trends for this season:
                
                Describe:
                1. **Colors of the Season**: The trending shades dominating the runways and streets.
                2. **Must-Have Wardrobe Staples**: 3 items everyone is wearing.
                3. **Shoe & Accessory Directions**: Footwear styles and jewelry shapes.
                4. **Celebrity Street Style**: Break down a hot celebrity outfit look.
                5. **Style Philosophy**: The mindset shift behind these trends (e.g., quiet luxury, retro revival).
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            trendsResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isTrendsLoading = false
        }
    }

    // --- Daily Style Challenge ---
    fun getDailyChallenge() {
        isDailyChallengeLoading = true
        dailyChallengeResult = null

        viewModelScope.launch {
            val p = userProfile.value
            val systemPrompt = "You are a Fashion Director setting a fun daily fashion challenge."
            val prompt = """
                Generate today's Daily Style Challenge for a: ${p.gender} (Style: ${p.preferredStyle})
                
                Recommend:
                1. **Today's Look Challenge**: A themed daily outfit challenge (e.g., "The Monochromatic Power Layer").
                2. **Color of the Day**: The lucky hue to wear today, and why it works.
                3. **Accessory Spotlight**: How to highlight a specific accessory today.
                4. **Stylist Pro Tip**: A micro-skill styling hack (e.g., French tuck, sleeve bunching, collar folding).
                5. **Confidence Mantra**: A beautiful styling quote to boost self-esteem.
            """.trimIndent()

            val res = GeminiClient.queryGemini(prompt, systemPrompt)
            dailyChallengeResult = if (res == "ERROR_API_KEY_MISSING") {
                "Please configure your GEMINI_API_KEY in the Secrets panel."
            } else res
            isDailyChallengeLoading = false
        }
    }
}

class StyleViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StyleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StyleViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
