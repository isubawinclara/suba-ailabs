package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.R
import com.example.data.SavedLook
import com.example.data.UserProfile
import com.example.data.WardrobeItem
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StyleBackgroundDark
import com.example.ui.theme.StyleBackgroundLight
import com.example.ui.theme.StyleIndigo
import com.example.ui.theme.StyleLavender
import com.example.ui.theme.StyleLavenderDark
import com.example.ui.theme.StylePink
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StyleViewModel
import com.example.viewmodel.StyleViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StyleViewModel = viewModel(factory = StyleViewModelFactory(application))
            var isDarkTheme by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppLayout(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(
    viewModel: StyleViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Scaffold(
        bottomBar = {
            if (viewModel.isLoggedIn && shouldShowBottomBar(viewModel.currentScreen)) {
                StyleBottomNavBar(viewModel = viewModel)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background ambient glows (startup luxury design)
            AmbientGlows(isDarkTheme = isDarkTheme)

            // Animated Screen Switching
            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.Landing -> LandingScreen(viewModel = viewModel)
                    AppScreen.Login -> AuthScreen(viewModel = viewModel)
                    AppScreen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle
                    )
                    AppScreen.OutfitGenerator -> OutfitGeneratorScreen(viewModel = viewModel)
                    AppScreen.ColorAnalysis -> ColorAnalysisScreen(viewModel = viewModel)
                    AppScreen.HairstyleAdvisor -> HairstyleAdvisorScreen(viewModel = viewModel)
                    AppScreen.MakeupAdvisor -> MakeupAdvisorScreen(viewModel = viewModel)
                    AppScreen.WardrobeManager -> WardrobeManagerScreen(viewModel = viewModel)
                    AppScreen.ShoppingAssistant -> ShoppingAssistantScreen(viewModel = viewModel)
                    AppScreen.EventStylist -> EventStylistScreen(viewModel = viewModel)
                    AppScreen.TrendExplorer -> TrendExplorerScreen(viewModel = viewModel)
                    AppScreen.DailyChallenge -> DailyChallengeScreen(viewModel = viewModel)
                    AppScreen.Profile -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

fun shouldShowBottomBar(screen: AppScreen): Boolean {
    return when (screen) {
        AppScreen.Dashboard,
        AppScreen.WardrobeManager,
        AppScreen.Profile -> true
        else -> false
    }
}

// --- Frosted Glass Helper Methods ---
@Composable
fun isAppInDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.primary == StyleLavenderDark
}

@Composable
fun glassCardColors(): CardColors {
    return CardDefaults.cardColors(
        containerColor = if (isAppInDarkTheme()) Color(0x4D151E33) else Color(0x99FFFFFF)
    )
}

@Composable
fun glassBorder(): BorderStroke {
    return BorderStroke(
        width = 1.dp,
        color = if (isAppInDarkTheme()) Color(0x26FFFFFF) else Color(0xB3FFFFFF)
    )
}

val GlassCardShape = RoundedCornerShape(24.dp)

// --- Background Decorative Elements ---
@Composable
fun AmbientGlows(isDarkTheme: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        val color1 = if (isDarkTheme) Color(0x2B7C3AED) else Color(0x1A7C3AED)
        val color2 = if (isDarkTheme) Color(0x2BEC4899) else Color(0x1AEC4899)
        val color3 = if (isDarkTheme) Color(0x2B4F46E5) else Color(0x1A4F46E5)

        Box(
            modifier = Modifier
                .size(450.dp)
                .offset(x = (-120).dp, y = (-120).dp)
                .background(Brush.radialGradient(listOf(color1, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .background(Brush.radialGradient(listOf(color3, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(450.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-150).dp, y = 150.dp)
                .background(Brush.radialGradient(listOf(color2, Color.Transparent)))
        )
    }
}

// --- Bottom Navigation Bar ---
@Composable
fun StyleBottomNavBar(viewModel: StyleViewModel) {
    val isDark = isAppInDarkTheme()
    NavigationBar(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                1.dp,
                if (isDark) Color(0x26FFFFFF) else Color(0xCCFFFFFF),
                RoundedCornerShape(24.dp)
            )
            .testTag("style_bottom_nav_bar"),
        containerColor = if (isDark) Color(0x59151E33) else Color(0x99FFFFFF),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(AppScreen.Dashboard, Icons.Rounded.Dashboard, "Advisor"),
            Triple(AppScreen.WardrobeManager, Icons.Rounded.ShoppingBag, "Wardrobe"),
            Triple(AppScreen.Profile, Icons.Rounded.Person, "Profile")
        )

        items.forEach { (screen, icon, label) ->
            val isSelected = viewModel.currentScreen == screen || 
                             (screen == AppScreen.Dashboard && viewModel.currentScreen != AppScreen.WardrobeManager && viewModel.currentScreen != AppScreen.Profile)
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StyleLavender,
                    selectedTextColor = StyleLavender,
                    indicatorColor = StyleLavender.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_item_${label.lowercase()}")
            )
        }
    }
}

// --- LANDING SCREEN ---
@Composable
fun LandingScreen(viewModel: StyleViewModel) {
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("landing_screen_scroll"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            // Logo & Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(StyleLavender.copy(alpha = 0.1f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = StyleLavender,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "StyleSense AI",
                    color = StyleLavender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Text
            Text(
                text = "Your Personal AI\nFashion Stylist",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Discover outfits, makeup routines, and hair styling curated to your unique physique using advanced AI intelligence.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            Button(
                onClick = { viewModel.navigateTo(AppScreen.Login) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("get_started_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StyleLavender
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.ArrowForward, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Hero Banner Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_fashion_banner),
                    contentDescription = "StyleSense Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Core Features Section Header
            SectionHeader(title = "Expert AI Advisory Suite")

            val landingFeatures = listOf(
                Triple(Icons.Rounded.Checkroom, "Outfit Generator", "Curates full wardrobe stylings for any weather & budget."),
                Triple(Icons.Rounded.ColorLens, "Color Analysis", "Detects seasonal color palettes matching skin undertones."),
                Triple(Icons.Rounded.ContentCut, "Hairstyle Advisor", "Suggests the most flattering hair cuts and styling shapes."),
                Triple(Icons.Rounded.Brush, "Makeup Artist", "Tailors foundations, eye palettes, and bold lips.")
            )

            landingFeatures.forEach { (icon, title, desc) ->
                FeatureLandingCard(icon = icon, title = title, description = desc)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Testimonials
            SectionHeader(title = "Styling Testimonials")

            TestimonialCard(
                quote = "It’s like having a Vogue stylist directly in my pocket. The color analysis correctly identified me as a Cool Summer, which totally transformed how I buy shirts!",
                author = "Sophia K.",
                title = "Vogue Enthusiast"
            )
            Spacer(modifier = Modifier.height(12.dp))
            TestimonialCard(
                quote = "The Wardrobe integration is brilliant. I uploaded my white shirt and navy jacket, and the AI outfit generator styled 5 gorgeous event looks. 10/10!",
                author = "Marcus T.",
                title = "Product Manager"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Pricing
            SectionHeader(title = "Simple Pricing Plans")

            PricingCard(
                planName = "Free Stylist Tier",
                price = "$0",
                features = listOf("Daily Style Challenge", "Wardrobe Manager (up to 15 items)", "Basic Hairstyle tips"),
                isActive = true,
                onSelect = { viewModel.navigateTo(AppScreen.Login) }
            )
            Spacer(modifier = Modifier.height(14.dp))
            PricingCard(
                planName = "StyleSense Elite Premium",
                price = "$9.99 / mo",
                features = listOf("Unlimited AI Outfit Generation", "Detailed Color Analysis", "Event Lookbook Stylist", "Full Shopping list helper"),
                isActive = false,
                isPremium = true,
                onSelect = { viewModel.navigateTo(AppScreen.Login) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // FAQ
            SectionHeader(title = "Frequently Asked Questions")

            val faqs = listOf(
                "How does the AI color analysis work?" to "Our AI compares your skin, eye, and hair descriptions with standard season undertone palettes to determine your ideal seasonal coloring.",
                "Can I integrate my actual wardrobe?" to "Yes! Our Wardrobe Manager allows you to log existing clothes, and our generator will prioritize them when assembling custom outfits.",
                "Is StyleSense available offline?" to "While you can browse your saved looks and manage your wardrobe offline, generating new styling advice requires an active internet connection."
            )

            faqs.forEachIndexed { index, (q, a) ->
                FaqItem(
                    question = q,
                    answer = a,
                    isExpanded = expandedFaqIndex == index,
                    onToggle = { expandedFaqIndex = if (expandedFaqIndex == index) null else index }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Footer
            Text(
                "StyleSense AI © 2026. Made with luxury styling principles.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun FeatureLandingCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StyleLavender.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = StyleLavender)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun TestimonialCard(quote: String, author: String, title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Rounded.FormatQuote, contentDescription = null, tint = StylePink.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
            Text(
                quote,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StylePink.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(author.take(1), fontWeight = FontWeight.Bold, color = StylePink)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(author, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun PricingCard(
    planName: String,
    price: String,
    features: List<String>,
    isActive: Boolean,
    isPremium: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassCardShape,
        colors = if (isPremium) {
            CardDefaults.cardColors(containerColor = StyleLavender.copy(alpha = 0.08f))
        } else {
            glassCardColors()
        },
        border = if (isPremium) {
            BorderStroke(2.dp, StyleLavender)
        } else {
            glassBorder()
        }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(planName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = if (isPremium) StyleLavender else MaterialTheme.colorScheme.onSurface)
                if (isPremium) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StylePink)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("POPULAR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(price, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(16.dp))
            features.forEach { feat ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = if (isPremium) StylePink else StyleLavender, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feat, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPremium) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    contentColor = if (isPremium) Color.White else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Select Plan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String, isExpanded: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(16.dp),
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(question, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(0.9f))
                Icon(
                    if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.weight(0.1f)
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    answer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}

// --- AUTH SCREEN ---
@Composable
fun AuthScreen(viewModel: StyleViewModel) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var forgotPasswordRequested by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "StyleSense AI",
                    style = MaterialTheme.typography.titleLarge,
                    color = StyleLavender,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (isSignUp) "Create your stylist account" else "Login to your style portfolio",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(4.dp)
                ) {
                    val activeWeight = 1f
                    Button(
                        onClick = { isSignUp = false },
                        modifier = Modifier
                            .weight(activeWeight)
                            .testTag("tab_login"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent,
                            contentColor = if (!isSignUp) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text("Login", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { isSignUp = true },
                        modifier = Modifier
                            .weight(activeWeight)
                            .testTag("tab_signup"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent,
                            contentColor = if (isSignUp) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text("Sign Up", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Inputs
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isSignUp) {
                    Text(
                        "Forgot Password?",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { forgotPasswordRequested = true }
                            .testTag("forgot_password_link"),
                        color = StyleIndigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button
                Button(
                    onClick = {
                        if (email.isEmpty()) {
                            Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.handleLogin(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("auth_action_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender)
                ) {
                    Text(if (isSignUp) "Register" else "Access Stylist", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("OR", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(16.dp))

                // Google Login
                OutlinedButton(
                    onClick = { viewModel.handleLogin("google_user@gmail.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("google_login_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = null,
                        tint = StylePink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Continue with Google", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (forgotPasswordRequested) {
        AlertDialog(
            onDismissRequest = { forgotPasswordRequested = false },
            title = { Text("Password Recovery") },
            text = { Text("A secure style password reset link has been dispatched to your designated email portfolio inbox.") },
            confirmButton = {
                TextButton(
                    onClick = { forgotPasswordRequested = false },
                    modifier = Modifier.testTag("dialog_dismiss_button")
                ) {
                    Text("Dismiss", color = StyleLavender, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- MAIN DASHBOARD SCREEN ---
@Composable
fun DashboardScreen(
    viewModel: StyleViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAdminPanel by remember { mutableStateOf(false) }

    val coreAdvisors = listOf(
        Triple(AppScreen.OutfitGenerator, "Outfit Generator", Icons.Rounded.Checkroom),
        Triple(AppScreen.ColorAnalysis, "Color Analysis", Icons.Rounded.ColorLens),
        Triple(AppScreen.HairstyleAdvisor, "Hairstyle Advisor", Icons.Rounded.ContentCut),
        Triple(AppScreen.MakeupAdvisor, "Makeup Advisor", Icons.Rounded.Brush),
        Triple(AppScreen.WardrobeManager, "Wardrobe Manager", Icons.Rounded.FolderSpecial),
        Triple(AppScreen.ShoppingAssistant, "Shopping Assistant", Icons.Rounded.LocalMall),
        Triple(AppScreen.EventStylist, "Event Stylist", Icons.Rounded.Celebration),
        Triple(AppScreen.TrendExplorer, "Trend Explorer", Icons.Rounded.TrendingUp),
        Triple(AppScreen.DailyChallenge, "Daily Challenge", Icons.Rounded.WorkspacePremium)
    )

    val filteredAdvisors = if (searchQuery.isEmpty()) coreAdvisors else {
        coreAdvisors.filter { it.second.lowercase().contains(searchQuery.lowercase()) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_scroll")
    ) {
        // Custom Top Navigation
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STYLESENSE AI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    val displayName = if (viewModel.userEmail.isNotEmpty()) {
                        viewModel.userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                    } else "Sophia"
                    Text(
                        text = "Good morning, $displayName",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = StyleIndigo
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showAdminPanel = !showAdminPanel },
                        modifier = Modifier.testTag("admin_panel_toggle")
                    ) {
                        Icon(
                            if (showAdminPanel) Icons.Rounded.HomeMini else Icons.Rounded.AdminPanelSettings,
                            contentDescription = "Admin Toggle",
                            tint = StylePink
                        )
                    }
                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = StyleIndigo
                        )
                    }
                    IconButton(
                        onClick = { viewModel.handleLogout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(Icons.Rounded.Logout, contentDescription = "Log Out")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, StyleIndigo.copy(alpha = 0.2f), CircleShape)
                            .background(StylePink),
                        contentAlignment = Alignment.Center
                    ) {
                        val firstLetter = if (viewModel.userEmail.isNotEmpty()) {
                            viewModel.userEmail.substringBefore("@").take(1).uppercase()
                        } else "S"
                        Text(
                            text = firstLetter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search specific fashion advisor modules...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_search_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StyleLavender,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showAdminPanel) {
            item {
                AdminPanelScreen(viewModel = viewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Style Profile Overview Quick-Widget
            item {
                ProfileQuickWidget(viewModel = viewModel)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Virtual Try-On Highlight Banner
            item {
                VirtualTryOnBanner(
                    onClick = {
                        // Launch Custom Try-On View inside a simple dialog/sheet
                        Toast.makeText(viewModel.getApplication(), "Launching Virtual Try-On Studio!", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Access Saved Looks Drawer
            item {
                SavedLooksSection(viewModel = viewModel)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Core AI Suite grid
            item {
                Text("Your Style Consulting Suites", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(380.dp)
                        .testTag("suite_grid"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAdvisors) { (screen, title, icon) ->
                        AdvisorSuiteCard(
                            title = title,
                            icon = icon,
                            onClick = { viewModel.navigateTo(screen) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileQuickWidget(viewModel: StyleViewModel) {
    val profile by viewModel.userProfile.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StyleLavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Style, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Style Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Personalized settings active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
                TextButton(
                    onClick = { viewModel.navigateTo(AppScreen.Profile) },
                    modifier = Modifier.testTag("edit_profile_widget_button")
                ) {
                    Text("Edit Info", color = StyleLavender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProfileTag(label = "Shape", value = profile.bodyShape)
                ProfileTag(label = "Skin Tone", value = profile.skinTone)
                ProfileTag(label = "Preference", value = profile.preferredStyle)
            }
        }
    }
}

@Composable
fun ProfileTag(label: String, value: String) {
    Column {
        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), letterSpacing = 0.5.sp)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun VirtualTryOnBanner(onClick: () -> Unit) {
    var openTryOnModal by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openTryOnModal = true }
            .testTag("virtual_try_on_banner"),
        shape = GlassCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(GlassCardShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            StyleIndigo,
                            StylePink
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.3f), GlassCardShape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("BETA EXPERIMENTAL", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("AI Virtual Try-On Studio", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("See how outfits overlay on your photo instantly.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Camera, contentDescription = null, tint = StyleIndigo)
                }
            }
        }
    }

    if (openTryOnModal) {
        MockVirtualTryOnDialog(onDismiss = { openTryOnModal = false })
    }
}

@Composable
fun MockVirtualTryOnDialog(onDismiss: () -> Unit) {
    var hasPhoto by remember { mutableStateOf(false) }
    var selectedClothingType by remember { mutableStateOf("Luxury Evening Gown") }
    val clothingOptions = listOf("Luxury Evening Gown", "Tailored Summer Suit", "Chic Cozy Sweater combo", "Traditional Festive Attire")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Virtual Try-On Studio", fontWeight = FontWeight.Bold, color = StyleLavender) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Upload an upright body photo, select any outfit recommendation, and let AI generate a preview overlay.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .border(2.dp, Brush.linearGradient(listOf(StyleLavender, StylePink)), RoundedCornerShape(16.dp))
                        .clickable { hasPhoto = !hasPhoto },
                    contentAlignment = Alignment.Center
                ) {
                    if (hasPhoto) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Face, contentDescription = null, tint = StyleLavender, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Selfie_Styling_Portrait.png", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Click to swap photo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.UploadFile, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Upload Styling Portrait", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Select Style Overlay Outfit:", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow {
                    items(clothingOptions) { option ->
                        val isSel = option == selectedClothingType
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .clickable { selectedClothingType = option }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(option, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StyleLavender)
            ) {
                Text("Render Live Try-On", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdvisorSuiteCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("advisor_card_${title.lowercase().replace(" ", "_")}"),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StyleIndigo.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = StyleIndigo, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Personal Advisor AI",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

// --- SAVED LOOKS DRAWER SECTION ---
@Composable
fun SavedLooksSection(viewModel: StyleViewModel) {
    val looks by viewModel.savedLooks.collectAsState()

    Column {
        Text(
            "Your Curated Wardrobe Lookbooks",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (looks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Style, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("No saved looks yet. Curate outfits to build lookbooks!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.testTag("saved_looks_row")
            ) {
                items(looks) { look ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .testTag("saved_look_card_${look.id}"),
                        shape = GlassCardShape,
                        colors = glassCardColors(),
                        border = glassBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StyleLavender.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(look.occasion, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StyleLavender)
                                }
                                IconButton(
                                    onClick = { viewModel.toggleFavoriteLook(look) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        if (look.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = "Fav",
                                        tint = StylePink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(look.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Top: ${look.top}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Bottom: ${look.bottom}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(look.budgetEstimate, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StyleIndigo)
                                IconButton(
                                    onClick = { viewModel.deleteLook(look) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- AI OUTFIT GENERATOR SCREEN ---
@Composable
fun OutfitGeneratorScreen(viewModel: StyleViewModel) {
    var occasion by remember { mutableStateOf("Casual Friday Picnic") }
    var weather by remember { mutableStateOf("Warm Sunny 22C") }
    var useWardrobe by remember { mutableStateOf(false) }
    var budget by remember { mutableStateOf("Moderate ($100-$300)") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("outfit_generator_screen")
    ) {
        // Back Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Outfit Curations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Curator Specifications", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Occasion
                OutlinedTextField(
                    value = occasion,
                    onValueChange = { occasion = it },
                    label = { Text("What is the occasion / event?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("occasion_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weather
                OutlinedTextField(
                    value = weather,
                    onValueChange = { weather = it },
                    label = { Text("What is the weather like?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weather_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Budget Tier
                Text("Select Budget Profile", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                val budgets = listOf("Value Smart ($0 - $50)", "Moderate ($100 - $300)", "Luxury Custom ($500+)")
                Row {
                    budgets.forEach { b ->
                        val isSel = budget == b
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .clickable { budget = b }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(b.substringBefore(" "), fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Use Wardrobe Items Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text("Integrate My Wardrobe Inventory", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("AI prioritizes using clothes you already own.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    Switch(
                        checked = useWardrobe,
                        onCheckedChange = { useWardrobe = it },
                        modifier = Modifier.testTag("use_wardrobe_switch")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.generateOutfit(occasion, weather, useWardrobe, budget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_outfit_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isGeneratingOutfit
                ) {
                    if (viewModel.isGeneratingOutfit) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Design Custom Lookbook", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Output Result Section
        if (viewModel.isGeneratingOutfit) {
            SkeletonLoadingPulse()
        } else if (viewModel.outfitError != null) {
            ErrorWidget(error = viewModel.outfitError!!)
        } else if (viewModel.lastGeneratedOutfit != null) {
            val outfit = viewModel.lastGeneratedOutfit!!
            OutfitResultCard(outfit = outfit, onSave = { viewModel.saveLook(outfit) })
        }
    }
}

@Composable
fun SkeletonLoadingPulse() {
    val infiniteTransition = rememberInfiniteTransition(label = "Skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SkeletonAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(140.dp, 20.dp).background(Color.Gray.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(Color.Gray.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp).background(Color.Gray.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color.Gray.copy(alpha = 0.3f)))
        }
    }
}

@Composable
fun ErrorWidget(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        border = glassBorder()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ErrorOutline, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(12.dp))
            Text(error, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
        }
    }
}

@Composable
fun OutfitResultCard(outfit: SavedLook, onSave: () -> Unit) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("outfit_result_card"),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(outfit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = StyleLavender)
                    Text("Curated Look for ${outfit.occasion}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(StylePink.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("${outfit.confidenceRating}% Fit", color = StylePink, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            // Clothes Layout
            StyleItemRow(label = "Top", value = outfit.top, icon = Icons.Rounded.Checkroom)
            StyleItemRow(label = "Bottom", value = outfit.bottom, icon = Icons.Rounded.Splitscreen)
            StyleItemRow(label = "Shoes", value = outfit.shoes, icon = Icons.Rounded.TrendingFlat)
            StyleItemRow(label = "Bag Accent", value = outfit.bag, icon = Icons.Rounded.ShoppingBag)
            StyleItemRow(label = "Watch Dial", value = outfit.watch, icon = Icons.Rounded.Watch)
            StyleItemRow(label = "Jewelry set", value = outfit.jewelry, icon = Icons.Rounded.AutoAwesome)
            StyleItemRow(label = "Accessories", value = outfit.accessories, icon = Icons.Rounded.ShoppingBag)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Stylist Rationale", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(outfit.whyItWorks, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Text("Accent Harmony Palette", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                outfit.matchingColors.split(",").forEach { colorName ->
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StyleLavender.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(colorName.trim(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StyleLavender)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Pro Styling Secrets", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(outfit.stylingTips, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(14.dp))

            Text("Alternative Quick-Swaps", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(outfit.alternativeSuggestions, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (PDF download mock, share mock, save DB)
            Row {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "Exporting Lookbook PDF...", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
                OutlinedButton(
                    onClick = { Toast.makeText(context, "Link Copied! Share with friends.", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
                Button(
                    onClick = {
                        onSave()
                        isSaved = true
                        Toast.makeText(context, "Look saved to lookbook!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(start = 4.dp)
                        .testTag("save_lookbook_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSaved) Color.Gray else StyleLavender),
                    enabled = !isSaved
                ) {
                    Icon(if (isSaved) Icons.Rounded.Check else Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSaved) "Saved" else "Save Look")
                }
            }
        }
    }
}

@Composable
fun StyleItemRow(label: String, value: String, icon: ImageVector) {
    if (value.isNotEmpty() && value != "null") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = StyleLavender.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

// --- AI COLOR ANALYSIS SCREEN ---
@Composable
fun ColorAnalysisScreen(viewModel: StyleViewModel) {
    var hasSelfie by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("color_analysis_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Color Analysis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Analyze Color Palette", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                Text("Our AI analyzes your skin tone undertones, eyes, and hair features to detect your seasonal harmony palette.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(StylePink.copy(alpha = 0.05f))
                        .border(1.5.dp, StylePink.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { hasSelfie = !hasSelfie }
                        .testTag("color_analysis_selfie_box"),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasSelfie) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = StylePink, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Selfie Loaded (Simulated Portrait)", fontWeight = FontWeight.Bold)
                            Text("Click to clear portrait", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = StylePink.copy(alpha = 0.6f), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Click to Simulate Selfie Upload", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.analyzeColorPalette() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("analyze_color_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isAnalyzingColor
                ) {
                    if (viewModel.isAnalyzingColor) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.ColorLens, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Seasonal Undertones", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.isAnalyzingColor) {
            SkeletonLoadingPulse()
        } else if (viewModel.colorAnalysisResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("color_analysis_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = StylePink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seasonal Harmony Palette", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.colorAnalysisResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// --- AI HAIRSTYLE ADVISOR SCREEN ---
@Composable
fun HairstyleAdvisorScreen(viewModel: StyleViewModel) {
    var faceShape by remember { mutableStateOf("Oval") }
    var desiredLength by remember { mutableStateOf("Medium") }
    var occasion by remember { mutableStateOf("Wedding Elegance") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("hairstyle_advisor_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Hairstyle Advisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hairstyle Specifications", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Face Shape
                Text("Select Face Shape:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                val shapes = listOf("Oval", "Round", "Square", "Chiseled")
                Row {
                    shapes.forEach { s ->
                        val isSel = faceShape == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .clickable { faceShape = s }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s, fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Desired Length
                Text("Select Desired Length:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                val lengths = listOf("Short", "Medium", "Long")
                Row {
                    lengths.forEach { l ->
                        val isSel = desiredLength == l
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .clickable { desiredLength = l }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(l, fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Occasion
                OutlinedTextField(
                    value = occasion,
                    onValueChange = { occasion = it },
                    label = { Text("Styling occasion (e.g. Wedding, Business)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hair_occasion_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.getHairstyleAdvice(faceShape, desiredLength, occasion) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("hairstyle_advisor_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isAdvisingHair
                ) {
                    if (viewModel.isAdvisingHair) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.ContentCut, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Hairstyle Advice", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.isAdvisingHair) {
            SkeletonLoadingPulse()
        } else if (viewModel.hairAdviceResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hairstyle_advisor_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ContentCut, contentDescription = null, tint = StyleLavender)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Curated Hair Cut Suggestions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.hairAdviceResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// --- AI MAKEUP ADVISOR SCREEN ---
@Composable
fun MakeupAdvisorScreen(viewModel: StyleViewModel) {
    var formality by remember { mutableStateOf("Glam Party Makeup") }
    var undertone by remember { mutableStateOf("Cool Pink") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("makeup_advisor_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Makeup Advisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Makeup Look specs", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Formality
                OutlinedTextField(
                    value = formality,
                    onValueChange = { formality = it },
                    label = { Text("Desired Makeup Theme (e.g. Natural Glam)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("makeup_formality_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Undertone
                OutlinedTextField(
                    value = undertone,
                    onValueChange = { undertone = it },
                    label = { Text("Your skin undertone (e.g. Warm Olive, Cool)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("makeup_undertone_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.getMakeupAdvice(formality, undertone) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("makeup_advisor_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isAdvisingMakeup
                ) {
                    if (viewModel.isAdvisingMakeup) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.Brush, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Consult AI Makeup Artist", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.isAdvisingMakeup) {
            SkeletonLoadingPulse()
        } else if (viewModel.makeupAdviceResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("makeup_advisor_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Brush, contentDescription = null, tint = StylePink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cosmetics & Application Routine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.makeupAdviceResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// --- WARDROBE MANAGER SCREEN ---
@Composable
fun WardrobeManagerScreen(viewModel: StyleViewModel) {
    val items by viewModel.wardrobeItems.collectAsState()
    var selectedCategoryTab by remember { mutableStateOf("All") }
    val categories = listOf("All", "Top", "Bottom", "Shoes", "Accessory", "Outerwear", "Dress")

    val filteredItems = if (selectedCategoryTab == "All") items else {
        items.filter { it.category == selectedCategoryTab }
    }

    var showAddItemDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("wardrobe_manager_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Wardrobe Manager", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Button(
                onClick = { showAddItemDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                modifier = Modifier.testTag("add_clothing_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        LazyRow {
            items(categories) { cat ->
                val isSel = cat == selectedCategoryTab
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .clickable { selectedCategoryTab = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("wardrobe_tab_${cat.lowercase()}")
                ) {
                    Text(
                        cat,
                        fontSize = 12.sp,
                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.Checkroom,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No wardrobe items logged.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text("Add your clothes to let StyleSense build outfits!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag("wardrobe_items_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wardrobe_item_${item.id}"),
                        shape = GlassCardShape,
                        colors = glassCardColors(),
                        border = glassBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StyleLavender.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (item.category) {
                                            "Top" -> Icons.Rounded.Checkroom
                                            "Bottom" -> Icons.Rounded.Splitscreen
                                            "Shoes" -> Icons.Rounded.TrendingFlat
                                            "Accessory" -> Icons.Rounded.AutoAwesome
                                            "Outerwear" -> Icons.Rounded.FolderSpecial
                                            else -> Icons.Rounded.FolderSpecial
                                        },
                                        contentDescription = null,
                                        tint = StyleLavender
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(StyleIndigo.copy(alpha = 0.1f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(item.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StyleIndigo)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Color: ${item.color}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            IconButton(
                                onClick = { viewModel.deleteWardrobeItem(item) },
                                modifier = Modifier.testTag("delete_item_button_${item.id}")
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        var name by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf("Top") }
        var color by remember { mutableStateOf("White") }
        var notes by remember { mutableStateOf("") }

        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Log Wardrobe Clothing", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Clothing Name (e.g. Silk Shirt)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_item_name_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Category:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow {
                        items(categories.filter { it != "All" }) { itemCat ->
                            val isSel = cat == itemCat
                            Box(
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                    .clickable { cat = itemCat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(itemCat, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Primary Color") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_item_color_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Fit notes or Fabric (optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_item_notes_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isEmpty()) {
                            Toast.makeText(context, "Please enter item name", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addWardrobeItem(name, cat, color, notes)
                            showAddItemDialog = false
                            Toast.makeText(context, "$name logged into Wardrobe!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_item_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender)
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- PROFILE SCREEN ---
@Composable
fun ProfileScreen(viewModel: StyleViewModel) {
    val profile by viewModel.userProfile.collectAsState()

    var gender by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bodyShape by remember { mutableStateOf("") }
    var skinTone by remember { mutableStateOf("") }
    var hairColor by remember { mutableStateOf("") }
    var eyeColor by remember { mutableStateOf("") }
    var preferredStyle by remember { mutableStateOf("") }
    var favoriteBrands by remember { mutableStateOf("") }
    var favoriteColors by remember { mutableStateOf("") }
    var dislikedColors by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        gender = profile.gender
        ageStr = profile.age.toString()
        height = profile.height
        weight = profile.weight
        bodyShape = profile.bodyShape
        skinTone = profile.skinTone
        hairColor = profile.hairColor
        eyeColor = profile.eyeColor
        preferredStyle = profile.preferredStyle
        favoriteBrands = profile.favoriteBrands
        favoriteColors = profile.favoriteColors
        dislikedColors = profile.dislikedColors
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        Text("Your Style Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text("Our AI uses this physical blueprint to calibrate custom lookbooks and avoid colors or cuts that wash you out.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Gender
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Identity Gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_gender_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Age
                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text("Age") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_age_input"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Proportions Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .testTag("profile_height_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .testTag("profile_weight_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Body Shape
                OutlinedTextField(
                    value = bodyShape,
                    onValueChange = { bodyShape = it },
                    label = { Text("Body Shape (Hourglass, Rectangle, Triangle)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_body_shape_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Skin Tone
                OutlinedTextField(
                    value = skinTone,
                    onValueChange = { skinTone = it },
                    label = { Text("Skin Tone / Undertone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_skin_tone_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Eye and Hair colors
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = hairColor,
                        onValueChange = { hairColor = it },
                        label = { Text("Hair Color") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .testTag("profile_hair_color_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eyeColor,
                        onValueChange = { eyeColor = it },
                        label = { Text("Eye Color") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .testTag("profile_eye_color_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preferred Style
                OutlinedTextField(
                    value = preferredStyle,
                    onValueChange = { preferredStyle = it },
                    label = { Text("Preferred Style (e.g. Quiet Luxury)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pref_style_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brands
                OutlinedTextField(
                    value = favoriteBrands,
                    onValueChange = { favoriteBrands = it },
                    label = { Text("Favorite Brands") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_brands_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Colors
                OutlinedTextField(
                    value = favoriteColors,
                    onValueChange = { favoriteColors = it },
                    label = { Text("Favorite Colors to Accent") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_fav_colors_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dislikedColors,
                    onValueChange = { dislikedColors = it },
                    label = { Text("Disliked Colors to Avoid") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_disliked_colors_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val age = ageStr.toIntOrNull() ?: 24
                        viewModel.updateProfile(
                            UserProfile(
                                gender = gender,
                                age = age,
                                height = height,
                                weight = weight,
                                bodyShape = bodyShape,
                                skinTone = skinTone,
                                hairColor = hairColor,
                                eyeColor = eyeColor,
                                preferredStyle = preferredStyle,
                                favoriteBrands = favoriteBrands,
                                favoriteColors = favoriteColors,
                                dislikedColors = dislikedColors
                            )
                        )
                        Toast.makeText(context, "Style Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender)
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Style Blueprint", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// --- OTHER AI FEATURES (SHOPPING, EVENTS, TRENDS, DAILY CHALLENGE) ---

@Composable
fun ShoppingAssistantScreen(viewModel: StyleViewModel) {
    var query by remember { mutableStateOf("Beige trench coat alternative") }
    var maxBudget by remember { mutableStateOf("$200") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("shopping_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Shopping Assistant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Search Shopping Recommendations", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("What item are you hunting for?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shopping_query_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = maxBudget,
                    onValueChange = { maxBudget = it },
                    label = { Text("Max Budget Limit") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shopping_budget_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.getShoppingRecommendations(query, maxBudget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("shopping_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isShoppingLoading
                ) {
                    if (viewModel.isShoppingLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Recommendations", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.isShoppingLoading) {
            SkeletonLoadingPulse()
        } else if (viewModel.shoppingResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shopping_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocalMall, contentDescription = null, tint = StyleLavender)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Brand Comparison & Choices", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.shoppingResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun EventStylistScreen(viewModel: StyleViewModel) {
    var selectedEvent by remember { mutableStateOf("Summer Wedding") }
    val events = listOf("Summer Wedding", "Corporate Interview", "Chic Dinner Date", "Rooftop Party", "Music Festival")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("event_stylist_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Event Stylist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = GlassCardShape,
            colors = glassCardColors(),
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Styling Event", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))

                events.forEach { ev ->
                    val isSel = ev == selectedEvent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) StyleLavender.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { selectedEvent = ev }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ev, fontWeight = FontWeight.Medium, color = if (isSel) StyleLavender else MaterialTheme.colorScheme.onSurface)
                        RadioButton(
                            selected = isSel,
                            onClick = { selectedEvent = ev },
                            colors = RadioButtonDefaults.colors(selectedColor = StyleLavender)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.getEventStyling(selectedEvent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("event_stylist_submit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StyleLavender),
                    enabled = !viewModel.isEventStylingLoading
                ) {
                    if (viewModel.isEventStylingLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.Celebration, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Curate Custom Event Lookbook", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.isEventStylingLoading) {
            SkeletonLoadingPulse()
        } else if (viewModel.eventStylingResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("event_stylist_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Celebration, contentDescription = null, tint = StylePink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Curated Lookbook Specs", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.eventStylingResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun TrendExplorerScreen(viewModel: StyleViewModel) {
    LaunchedEffect(Unit) {
        if (viewModel.trendsResult == null) {
            viewModel.exploreTrends()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("trend_explorer_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Trend Explorer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isTrendsLoading) {
            SkeletonLoadingPulse()
        } else if (viewModel.trendsResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trends_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.TrendingUp, contentDescription = null, tint = StyleLavender)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Global Fashion Forecasts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.trendsResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun DailyChallengeScreen(viewModel: StyleViewModel) {
    LaunchedEffect(Unit) {
        if (viewModel.dailyChallengeResult == null) {
            viewModel.getDailyChallenge()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("daily_challenge_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.Dashboard) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Daily Style Challenge", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isDailyChallengeLoading) {
            SkeletonLoadingPulse()
        } else if (viewModel.dailyChallengeResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_challenge_result_card"),
                shape = GlassCardShape,
                colors = glassCardColors(),
                border = glassBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = StylePink)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Today's Curated Styling Hack", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.dailyChallengeResult!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// --- ADMIN PANEL SCREEN ---
@Composable
fun AdminPanelScreen(viewModel: StyleViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_panel"),
        shape = GlassCardShape,
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, tint = StylePink)
                Spacer(modifier = Modifier.width(10.dp))
                Text("StyleSense Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Live administrative usage & category parameters.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))

            // Analytics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsStatCard(label = "Active Stylists", value = "1,420", modifier = Modifier.weight(1f))
                AnalyticsStatCard(label = "Queries Logged", value = "28,450", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsStatCard(label = "Wardrobes Saved", value = "4,120 items", modifier = Modifier.weight(1f))
                AnalyticsStatCard(label = "Top Category", value = "Quiet Luxury", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Trending Styling Queries", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))

            TrendingQueryItem(rank = 1, query = "Summer Pastel Suit Combinations", queriesCount = "12.4k")
            TrendingQueryItem(rank = 2, query = "Cool Summer Makeup undertone shades", queriesCount = "9.8k")
            TrendingQueryItem(rank = 3, query = "Minimalist Linen dress styling rules", queriesCount = "8.1k")
            TrendingQueryItem(rank = 4, query = "Traditional Festival modern fusion", queriesCount = "4.2k")
        }
    }
}

@Composable
fun AnalyticsStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = glassCardColors(),
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StyleLavender)
        }
    }
}

@Composable
fun TrendingQueryItem(rank: Int, query: String, queriesCount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(StylePink.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(rank.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StylePink)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(query, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(queriesCount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}
