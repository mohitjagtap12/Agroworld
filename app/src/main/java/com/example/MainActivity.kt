package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        NavHost(
          navController = navController,
          startDestination = "splash",
          modifier = Modifier.fillMaxSize()
        ) {
          composable("splash") {
            SplashScreen(navController = navController)
          }
          composable("login") {
            LoginScreen(navController = navController)
          }
          composable("role_selection") {
            RoleSelectionScreen(navController = navController)
          }
          composable(
            route = "dashboard/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
          ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            if (role.lowercase() == "farmer") {
              FarmerPortalScreen(navController = navController)
            } else if (role.lowercase() == "customer") {
              CustomerPortalScreen(navController = navController)
            } else if (role.lowercase() == "broker") {
              BrokerPortalScreen(navController = navController)
            } else if (role.lowercase() == "seller") {
              SellerPortalScreen(navController = navController)
            } else if (role.lowercase() == "delivery") {
              DeliveryPartnerPortalScreen(navController = navController)
            } else {
              DashboardScreen(role = role, navController = navController)
            }
          }
        }
      }
    }
  }
}

// ------------------ SPLASH SCREEN ------------------
@Composable
fun SplashScreen(navController: NavController) {
  LaunchedEffect(Unit) {
    kotlinx.coroutines.delay(2500)
    navController.navigate("role_selection") {
      popUpTo("splash") { inclusive = true }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFFDFBFF),
            Color(0xFFE7F0FF),
            Color(0xFFD3E4FF)
          )
        )
      )
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      Box(
        modifier = Modifier
          .size(130.dp)
          .clip(CircleShape)
          .background(Color.White, shape = CircleShape)
          .drawBehind {
            drawCircle(
              color = Color(0xFF0061A4).copy(alpha = 0.15f),
              radius = size.maxDimension / 2 + 12.dp.toPx()
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Eco,
          contentDescription = "AgroWorld Leaf Logo",
          tint = Color(0xFF0061A4),
          modifier = Modifier.size(72.dp)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "AgroWorld",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0061A4),
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Connecting Farmers, Buyers & Agricultural Services",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF44474E),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.height(64.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
          color = Color(0xFF0061A4),
          strokeWidth = 2.5.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Initializing Field Access...",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF0061A4),
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

// ------------------ LOGIN SCREEN ------------------
@Composable
fun LoginScreen(navController: NavController) {
  var phoneNumber by remember { mutableStateOf("") }
  var selectedCountryCode by remember { mutableStateOf("+91") }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  val context = LocalContext.current

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFFDFBFF))
      .testTag("login_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .weight(1f)
          .wrapContentHeight(Alignment.CenterVertically)
      ) {
        Box(
          modifier = Modifier
            .size(96.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE7F0FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Agriculture,
            contentDescription = "AgroWorld Brand Icon",
            tint = Color(0xFF0061A4),
            modifier = Modifier.size(56.dp)
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
          text = "Welcome back",
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1A1C1E),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Empowering the future of sustainable farming.",
          fontSize = 16.sp,
          color = Color(0xFF44474E),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = "Mobile Number",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF44474E),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .width(100.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 0.dp))
                .background(Color(0xFFF0F4F9))
                .clickable { isDropdownExpanded = true }
                .padding(horizontal = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = selectedCountryCode,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF1A1C1E)
                )
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = "Select country code",
                  tint = Color(0xFF44474E)
                )
              }

              DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
              ) {
                listOf("+91", "+1", "+44", "+61", "+81", "+49").forEach { code ->
                  DropdownMenuItem(
                    text = { Text(code) },
                    onClick = {
                      selectedCountryCode = code
                      isDropdownExpanded = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { input ->
                if (input.all { it.isDigit() } && input.length <= 10) {
                  phoneNumber = input
                }
              },
              placeholder = { Text("Phone number", color = Color(0xFF44474E).copy(alpha = 0.6f)) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F4F9),
                unfocusedContainerColor = Color(0xFFF0F4F9),
                focusedBorderColor = Color(0xFF0061A4),
                unfocusedBorderColor = Color.Transparent
              ),
              shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("phone_input")
            )
          }
        }

      }

      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Button(
          onClick = {
            if (phoneNumber.length < 10) {
              Toast.makeText(context, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            } else {
              isLoading = true
              navController.navigate("role_selection") {
                popUpTo("login") { inclusive = true }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0061A4),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("send_otp_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "Send OTP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Proceed"
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Privacy Policy",
            fontSize = 12.sp,
            color = Color(0xFF44474E),
            modifier = Modifier.clickable { }
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = "•",
            fontSize = 12.sp,
            color = Color(0xFF44474E)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = "Terms & Conditions",
            fontSize = 12.sp,
            color = Color(0xFF44474E),
            modifier = Modifier.clickable { }
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "© 2026 AgroWorld. All rights reserved.",
          fontSize = 11.sp,
          color = Color(0xFF44474E)
        )
      }
    }
  }
}

// ------------------ ROLE SELECTION SCREEN ------------------
data class RoleInfo(
  val id: String,
  val name: String,
  val description: String,
  val icon: ImageVector
)

@Composable
fun RoleSelectionScreen(navController: NavController) {
  var selectedRoleId by remember { mutableStateOf<String?>(null) }

  val roles = listOf(
    RoleInfo(
      id = "farmer",
      name = "Farmer",
      description = "Sell crops directly to the market and buy essential supplies for your farm.",
      icon = Icons.Default.Agriculture
    ),
    RoleInfo(
      id = "customer",
      name = "Customer",
      description = "Shop for fresh, farm-direct produce and premium organic agricultural products.",
      icon = Icons.Default.ShoppingCart
    ),
    RoleInfo(
      id = "broker",
      name = "Broker",
      description = "Facilitate large-scale trade between producers and distributors with expert market data.",
      icon = Icons.Default.TrendingUp
    ),
    RoleInfo(
      id = "seller",
      name = "Seller",
      description = "Manage your inventory, list products, and reach a wider audience of agricultural buyers.",
      icon = Icons.Default.Storefront
    ),
    RoleInfo(
      id = "delivery",
      name = "Delivery Partner",
      description = "Be the vital link that brings fresh farm products directly to the customer's doorstep with speed and care.",
      icon = Icons.Default.LocalShipping
    )
  )

  Scaffold(
    topBar = {
      Surface(
        tonalElevation = 0.dp,
        color = Color(0xFFFDFBFF),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Agriculture,
              contentDescription = "AgroWorld Brand Icon",
              tint = Color(0xFF0061A4),
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AgroWorld",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0061A4)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "Market",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF0061A4),
              modifier = Modifier.clickable { }
            )
            Text(
              text = "Help",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF44474E),
              modifier = Modifier.clickable { }
            )
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = Color(0xFFFDFBFF),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = {
              selectedRoleId?.let { roleId ->
                navController.navigate("dashboard/$roleId")
              }
            },
            enabled = selectedRoleId != null,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF0061A4),
              contentColor = Color.White,
              disabledContainerColor = Color(0xFFC4C6D0).copy(alpha = 0.4f),
              disabledContentColor = Color(0xFF1A1C1E).copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("continue_button")
          ) {
            Text(
              text = "Continue",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Already have an account? ",
              fontSize = 14.sp,
              color = Color(0xFF44474E)
            )
            Text(
              text = "Log In",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0061A4),
              modifier = Modifier.clickable { }
            )
          }
        }
      }
    },
    modifier = Modifier.testTag("role_selection_screen")
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFDFBFF))
        .padding(paddingValues)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Welcome to AgroWorld",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Select your role to continue your journey",
            fontSize = 16.sp,
            color = Color(0xFF44474E),
            textAlign = TextAlign.Center
          )
        }
      }

      items(roles) { role ->
        val isSelected = selectedRoleId == role.id
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE7F0FF) else Color(0xFFFFFFFF)
          ),
          border = if (isSelected) BorderStroke(1.5.dp, Color(0xFF0061A4)) else BorderStroke(1.dp, Color(0xFFC4C6D0)),
          elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedRoleId = role.id }
            .testTag("role_card_${role.id}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF0061A4) else Color(0xFFF2F0F4)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = role.icon,
                contentDescription = role.name,
                tint = if (isSelected) Color.White else Color(0xFF0061A4),
                modifier = Modifier.size(28.dp)
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = role.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF0061A4) else Color(0xFF1A1C1E)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = role.description,
                fontSize = 13.sp,
                color = Color(0xFF44474E),
                lineHeight = 18.sp
              )
            }

            if (isSelected) {
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF0061A4)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

// ------------------ DYNAMIC DASHBOARD SCREEN ------------------
@Composable
fun DashboardScreen(role: String, navController: NavController) {
  var activeTab by remember { mutableStateOf(0) }
  val context = LocalContext.current

  Scaffold(
    topBar = {
      Surface(
        tonalElevation = 0.dp,
        color = Color(0xFFFDFBFF),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
              navController.navigate("role_selection") {
                popUpTo("role_selection") { inclusive = true }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back to roles",
              tint = Color(0xFF0061A4)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "AgroWorld Portal",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0061A4)
            )
          }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFD6E2FF)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "JD",
              color = Color(0xFF001B3E),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = Color(0xFFF0F4F9),
        tonalElevation = 0.dp,
        modifier = Modifier.drawBehind {
          // Subtle top border matching bottom nav bar border-t border-[#DDE2EA]
          drawLine(
            color = Color(0xFFDDE2EA),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx()
          )
        }
      ) {
        NavigationBarItem(
          selected = activeTab == 0,
          onClick = { activeTab = 0 },
          icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home", fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF001D36),
            selectedTextColor = Color(0xFF001D36),
            unselectedIconColor = Color(0xFF44474E),
            unselectedTextColor = Color(0xFF44474E),
            indicatorColor = Color(0xFFD3E4FF)
          )
        )
        NavigationBarItem(
          selected = activeTab == 1,
          onClick = { activeTab = 1 },
          icon = { Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Analytics") },
          label = { Text("Insights", fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF001D36),
            selectedTextColor = Color(0xFF001D36),
            unselectedIconColor = Color(0xFF44474E),
            unselectedTextColor = Color(0xFF44474E),
            indicatorColor = Color(0xFFD3E4FF)
          )
        )
        NavigationBarItem(
          selected = activeTab == 2,
          onClick = { activeTab = 2 },
          icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
          label = { Text("Settings", fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF001D36),
            selectedTextColor = Color(0xFF001D36),
            unselectedIconColor = Color(0xFF44474E),
            unselectedTextColor = Color(0xFF44474E),
            indicatorColor = Color(0xFFD3E4FF)
          )
        )
      }
    },
    modifier = Modifier.testTag("dashboard_screen")
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFDFBFF))
        .padding(paddingValues)
    ) {
      if (activeTab == 0) {
        when (role.lowercase()) {
          "farmer" -> FarmerDashboardView()
          "customer" -> CustomerDashboardView()
          "broker" -> BrokerDashboardView()
          "seller" -> SellerDashboardView()
          "delivery" -> DeliveryDashboardView()
          else -> CustomerDashboardView()
        }
      } else if (activeTab == 1) {
        InsightsTabView(role = role)
      } else {
        SettingsTabView(role = role, navController = navController)
      }
    }
  }
}

// ------------------ FARMER DASHBOARD ------------------
@Composable
fun FarmerDashboardView() {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(24.dp)) {
          Text("Welcome, Agro-Farmer!", color = Color(0xFF0061A4), fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text("My Harvest Portal", color = Color(0xFF001B3E), fontSize = 24.sp, fontWeight = FontWeight.Light)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Total Active Crop Listings: 4 Crops",
            color = Color(0xFF001B3E),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF0061A4))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Location: Maharashtra Region • Soil Index: Excellent",
              color = Color(0xFF0061A4),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    item {
      Text(
        text = "Crop Live Market Prices",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1C1E)
      )
    }

    val crops = listOf(
      Triple("Premium Basmati Rice", "₹42,500 / Ton", "+4.2%"),
      Triple("Organic Durum Wheat", "₹26,800 / Ton", "+1.8%"),
      Triple("High-Grade Soybean", "₹38,200 / Ton", "-0.5%"),
      Triple("White Sweet Corn", "₹18,400 / Ton", "+5.1%")
    )

    items(crops) { crop ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F0F4)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Crop",
                tint = Color(0xFF0061A4)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(crop.first, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
              Text("AgroWorld Verified", fontSize = 12.sp, color = Color(0xFF44474E))
            }
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(crop.second, fontWeight = FontWeight.Bold, color = Color(0xFF0061A4))
            Text(
              text = crop.third,
              fontSize = 12.sp,
              color = if (crop.third.startsWith("+")) Color(0xFF0061A4) else Color(0xFFBA1A1A),
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add listing")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Create New Crop Listing", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ------------------ CUSTOMER DASHBOARD ------------------
@Composable
fun CustomerDashboardView() {
  var cartCount by remember { mutableStateOf(0) }
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Fresh Farm-Direct Produce", color = Color(0xFF0061A4), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Healthy Organic Table", color = Color(0xFF001B3E), fontSize = 22.sp, fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cart Items: $cartCount selected", color = Color(0xFF001B3E), fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(Color(0xFFD3E4FF))
              .clickable {
                Toast
                  .makeText(context, "Proceeding to checkout with $cartCount items!", Toast.LENGTH_SHORT)
                  .show()
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBasket,
              contentDescription = "Cart",
              tint = Color(0xFF001B3E),
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Organic Field Offerings",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1A1C1E)
        )
        Text(
          text = "View All",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0061A4),
          modifier = Modifier.clickable {}
        )
      }
    }

    val products = listOf(
      Triple("Organic Red Apples", "₹180 / Kg", "Freshly Picked Himachal Orchard"),
      Triple("Farm-Fresh Sweet Corn", "₹60 / Bunch", "Harvested Today from Pune"),
      Triple("Premium Basmati Grains", "₹120 / Kg", "Dehradun Special Aged 1 Year"),
      Triple("Pure Raw Forest Honey", "₹380 / Bottle", "Natures Pure Untampered")
    )

    items(products) { prod ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(prod.first, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(prod.third, fontSize = 12.sp, color = Color(0xFF44474E))
            Spacer(modifier = Modifier.height(4.dp))
            Text(prod.second, fontWeight = FontWeight.Bold, color = Color(0xFF0061A4))
          }

          Button(
            onClick = {
              cartCount++
              Toast.makeText(context, "Added ${prod.first} to cart!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFE7F0FF),
              contentColor = Color(0xFF0061A4)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add",
              tint = Color(0xFF0061A4),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", color = Color(0xFF0061A4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ------------------ BROKER DASHBOARD ------------------
@Composable
fun BrokerDashboardView() {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1E8)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(24.dp)) {
          Text("Distribution Hub Terminal", color = Color(0xFFFF8D4D), fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Wholesale Exchange Platform", color = Color(0xFF1A1C1E), fontSize = 22.sp, fontWeight = FontWeight.Light)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Pending Wholesale Contracts: 3 Agreements", color = Color(0xFF1A1C1E), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
      }
    }

    item {
      Text(
        text = "Active High-Volume Trades",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1C1E)
      )
    }

    val trades = listOf(
      Triple("Punjab Grains -> Mumbai Wholesalers", "Wheat 15 Tons", "Awaiting Broker Escrow"),
      Triple("Nashik Farms -> Delhi Supermarkets", "Onions 8 Tons", "In Route Logistics"),
      Triple("Shimla Orchard Co -> South Fruits Corp", "Apples 12 Tons", "Authorized & Cleared")
    )

    items(trades) { trade ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(trade.first, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF1E8))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Broker Safe",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF8D4D)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(trade.second, fontWeight = FontWeight.Medium, color = Color(0xFF0061A4), fontSize = 14.sp)
          Text(trade.third, fontSize = 12.sp, color = Color(0xFF44474E))
        }
      }
    }
  }
}

// ------------------ SELLER DASHBOARD ------------------
@Composable
fun SellerDashboardView() {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(24.dp)) {
          Text("Agro-Merchant Center", color = Color(0xFF0061A4), fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Inventory & Supply Dashboard", color = Color(0xFF001B3E), fontSize = 22.sp, fontWeight = FontWeight.Light)
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Active Stocks: 140 Packages", color = Color(0xFF001B3E), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("Revenue: ₹2,45,000", color = Color(0xFF0061A4), fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      Text(
        text = "Farming Supplies Catalog",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1C1E)
      )
    }

    val inventory = listOf(
      Triple("High-Yield Paddy Seeds", "45 Boxes in Stock", "₹1,200 / Box"),
      Triple("Organic Potassium Fertilizer", "80 Bags in Stock", "₹850 / Bag"),
      Triple("Premium Agricultural Drip Pipes", "12 Roll Reels left", "₹4,500 / Reel"),
      Triple("Manual Seed Sowing Tool", "6 Units remaining", "₹2,200 / Unit")
    )

    items(inventory) { item ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(item.first, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(item.second, fontSize = 12.sp, color = Color(0xFF44474E))
          }
          Text(item.third, fontWeight = FontWeight.Bold, color = Color(0xFF0061A4))
        }
      }
    }
  }
}

// ------------------ DELIVERY PARTNER DASHBOARD ------------------
@Composable
fun DeliveryDashboardView() {
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(24.dp)) {
          Text("Logistics Route Command", color = Color(0xFF0061A4), fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Express Farm Freight Network", color = Color(0xFF1A1C1E), fontSize = 22.sp, fontWeight = FontWeight.Light)
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Available Deliveries: 4 Nearby", color = Color(0xFF1A1C1E), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("Completed Today: 3 Jobs", color = Color(0xFF0061A4), fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      Text(
        text = "Available Pickups Nearby",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1C1E)
      )
    }

    val jobs = listOf(
      Triple("Nashik Farm -> Mumbai Cold Storage", "Grapes 400 Kg • ₹3,200 payout", "Distance: 120 Km"),
      Triple("Green-Orchard -> Local Retail Market", "Apples 180 Kg • ₹1,200 payout", "Distance: 24 Km"),
      Triple("Soil-Bio Warehouse -> Agro-Coop Center", "Seed Bags 1.2 Tons • ₹4,500 payout", "Distance: 85 Km")
    )

    items(jobs) { job ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(job.first, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(job.third, fontSize = 12.sp, color = Color(0xFF44474E))
            Spacer(modifier = Modifier.height(4.dp))
            Text(job.second, fontWeight = FontWeight.Bold, color = Color(0xFF0061A4), fontSize = 14.sp)
          }

          Button(
            onClick = {
              Toast.makeText(context, "Delivery Job Accepted! Navigate to location.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.height(36.dp)
          ) {
            Text("Accept", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ------------------ TAB COMPONENT: INSIGHTS ------------------
@Composable
fun InsightsTabView(role: String) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Text(
      text = "${role.replaceFirstChar { it.uppercase() }} Industry Analytics",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF1A1C1E)
    )

    Text(
      text = "AgroWorld provides comprehensive, real-time market insights utilizing historical field datasets.",
      fontSize = 14.sp,
      color = Color(0xFF44474E)
    )

    Card(
      shape = RoundedCornerShape(30.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.TrendingUp,
          contentDescription = "Trending",
          tint = Color(0xFF0061A4),
          modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Market Outlook: Highly Bullish",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF001B3E)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Agricultural crop demands in urban regions are expected to increase by 14% over the coming weeks due to seasonal changes. Harvest planning is strongly recommended.",
          fontSize = 13.sp,
          color = Color(0xFF44474E),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

// ------------------ TAB COMPONENT: SETTINGS ------------------
@Composable
fun SettingsTabView(role: String, navController: NavController) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "Account settings",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1C1E)
      )

      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Account Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
          Spacer(modifier = Modifier.height(4.dp))
          Text("Logged in role: ${role.replaceFirstChar { it.uppercase() }}", fontSize = 14.sp, color = Color(0xFF44474E))
        }
      }

      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC4C6D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
          Spacer(modifier = Modifier.height(4.dp))
          Text("Push messages & crop alert signals enabled", fontSize = 14.sp, color = Color(0xFF44474E))
        }
      }
    }

    Button(
      onClick = {
        navController.navigate("role_selection") {
          popUpTo("role_selection") { inclusive = true }
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Log out", tint = Color.White)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Exit Portal", fontWeight = FontWeight.Bold, color = Color.White)
    }
  }
}
