package com.example.ui.farmer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.R;
import com.example.databinding.ActivityFarmerContainerBinding;
import com.example.ui.seller.SellerStoreFragment;

/**
 * Host Activity for the Java/XML Farmer Module with Bottom Navigation and Fragments.
 */
public class FarmerContainerActivity extends AppCompatActivity implements FarmerDashboardFragment.OnFarmerNavListener {

    private ActivityFarmerContainerBinding binding;

    public static final String EXTRA_DESTINATION = "extra_destination";
    public static final String EXTRA_CROP_NAME = "extra_crop_name";

    public static final String DEST_LABOUR_HUB = "labour_hub";
    public static final String DEST_LABOUR_POST = "labour_post";
    public static final String DEST_LABOUR_PORTAL = "labour_portal";
    public static final String DEST_AGRI_WASTE_FARMER = "agri_waste_farmer";
    public static final String DEST_AGRI_WASTE_MARKETPLACE = "agri_waste_marketplace";
    public static final String DEST_FARMING_PRODUCTS = "farming_products";
    public static final String DEST_FARMING_CART = "farming_cart";
    public static final String DEST_FARMER_ORDERS = "farmer_orders";
    public static final String DEST_SELLER_STORE = "seller_store";
    public static final String DEST_CONTRACT_FARMING = "contract_farming";
    public static final String DEST_COMPANY_PORTAL = "company_portal";
    public static final String DEST_BROKER_TRADING = "broker_trading";
    public static final String DEST_BROKER_PORTAL = "broker_portal";
    public static final String DEST_FARMER_PRODUCE = "farmer_produce";
    public static final String DEST_CUSTOMER_STORE = "customer_store";
    public static final String DEST_DELIVERY_PORTAL = "delivery_portal";
    public static final String DEST_DELIVERY_HISTORY = "delivery_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmerContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();

        if (savedInstanceState == null) {
            String destination = getIntent().getStringExtra(EXTRA_DESTINATION);
            String cropFilter = getIntent().getStringExtra(EXTRA_CROP_NAME);

            if (DEST_LABOUR_HUB.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.labour.LabourHubFragment.newInstance(), false);
            } else if (DEST_LABOUR_POST.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.labour.PostLabourRequirementFragment.newInstance(), false);
            } else if (DEST_LABOUR_PORTAL.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.labour.LabourPortalFragment.newInstance(), false);
            } else if (DEST_AGRI_WASTE_FARMER.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.agriwaste.FarmerAgriWasteFragment.newInstance(), false);
            } else if (DEST_AGRI_WASTE_MARKETPLACE.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.agriwaste.AgriWasteMarketplaceFragment.newInstance(), false);
            } else if (DEST_FARMING_PRODUCTS.equalsIgnoreCase(destination)) {
                loadFragment(FarmingProductsFragment.newInstance(cropFilter), false);
            } else if (DEST_FARMING_CART.equalsIgnoreCase(destination)) {
                loadFragment(CartFragment.newInstance(), false);
            } else if (DEST_FARMER_ORDERS.equalsIgnoreCase(destination)) {
                loadFragment(FarmerProductOrdersFragment.newInstance(), false);
            } else if (DEST_SELLER_STORE.equalsIgnoreCase(destination)) {
                loadFragment(SellerStoreFragment.newInstance(), false);
            } else if (DEST_CONTRACT_FARMING.equalsIgnoreCase(destination)) {
                loadFragment(new ContractFarmingFragment(), false);
            } else if (DEST_COMPANY_PORTAL.equalsIgnoreCase(destination)) {
                loadFragment(new CompanyFragment(), false);
            } else if (DEST_BROKER_TRADING.equalsIgnoreCase(destination)) {
                loadFragment(BrokerTradingFragment.newInstance(), false);
            } else if (DEST_BROKER_PORTAL.equalsIgnoreCase(destination)) {
                loadFragment(BrokerFragment.newInstance(), false);
            } else if (DEST_FARMER_PRODUCE.equalsIgnoreCase(destination)) {
                loadFragment(FarmerProduceFragment.newInstance(), false);
            } else if (DEST_CUSTOMER_STORE.equalsIgnoreCase(destination)) {
                loadFragment(com.example.ui.customer.CustomerFragment.newInstance(), false);
            } else {
                loadFragment(new FarmerDashboardFragment(), false);
            }
        }
    }

    private void setupBottomNavigation() {
        binding.farmerBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_farmer_home) {
                loadFragment(new FarmerDashboardFragment(), false);
                return true;
            } else if (itemId == R.id.nav_farmer_crops) {
                loadFragment(new MyCropsFragment(), false);
                return true;
            } else if (itemId == R.id.nav_farmer_activities) {
                loadFragment(new FarmerActivitiesFragment(), false);
                return true;
            } else if (itemId == R.id.nav_farmer_profile) {
                loadFragment(new FarmerProfileFragment(), false);
                return true;
            }
            return false;
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.farmerFragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToDestination(String destination) {
        navigateToDestination(destination, null);
    }

    public void navigateToDestination(String destination, String cropFilter) {
        if (DEST_FARMING_PRODUCTS.equalsIgnoreCase(destination)) {
            loadFragment(FarmingProductsFragment.newInstance(cropFilter), true);
        } else if (DEST_FARMING_CART.equalsIgnoreCase(destination)) {
            loadFragment(CartFragment.newInstance(), true);
        } else if (DEST_FARMER_ORDERS.equalsIgnoreCase(destination)) {
            loadFragment(FarmerProductOrdersFragment.newInstance(), true);
        } else if (DEST_SELLER_STORE.equalsIgnoreCase(destination)) {
            loadFragment(SellerStoreFragment.newInstance(), true);
        } else if (DEST_LABOUR_HUB.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.labour.LabourHubFragment.newInstance(), true);
        } else if (DEST_AGRI_WASTE_FARMER.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.agriwaste.FarmerAgriWasteFragment.newInstance(), true);
        } else if (DEST_AGRI_WASTE_MARKETPLACE.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.agriwaste.AgriWasteMarketplaceFragment.newInstance(), true);
        } else if (DEST_CONTRACT_FARMING.equalsIgnoreCase(destination)) {
            loadFragment(new ContractFarmingFragment(), true);
        } else if (DEST_COMPANY_PORTAL.equalsIgnoreCase(destination)) {
            loadFragment(new CompanyFragment(), true);
        } else if (DEST_BROKER_TRADING.equalsIgnoreCase(destination)) {
            loadFragment(BrokerTradingFragment.newInstance(), true);
        } else if (DEST_BROKER_PORTAL.equalsIgnoreCase(destination)) {
            loadFragment(BrokerFragment.newInstance(), true);
        } else if (DEST_FARMER_PRODUCE.equalsIgnoreCase(destination)) {
            loadFragment(FarmerProduceFragment.newInstance(), true);
        } else if (DEST_CUSTOMER_STORE.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.customer.CustomerFragment.newInstance(), true);
        } else if (DEST_DELIVERY_PORTAL.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.delivery.DeliveryFragment.newInstance(), true);
        } else if (DEST_DELIVERY_HISTORY.equalsIgnoreCase(destination)) {
            loadFragment(com.example.ui.delivery.DeliveryHistoryFragment.newInstance(), true);
        }
    }

    @Override
    public void onNavigateToCrops() {
        binding.farmerBottomNav.setSelectedItemId(R.id.nav_farmer_crops);
    }

    @Override
    public void onNavigateToAiDisease(String cropId, String cropName) {
        loadFragment(AiDiseaseFragment.newInstance(cropId, cropName), true);
    }

    @Override
    public void onNavigateToActivities() {
        binding.farmerBottomNav.setSelectedItemId(R.id.nav_farmer_activities);
    }

    @Override
    public void onNavigateToProfile() {
        binding.farmerBottomNav.setSelectedItemId(R.id.nav_farmer_profile);
    }

    @Override
    public void onOpenRolePortal(String role) {
        if ("labour".equalsIgnoreCase(role) || "hire_labour".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.labour.LabourHubFragment.newInstance(), true);
        } else if ("waste".equalsIgnoreCase(role) || "agri_waste".equalsIgnoreCase(role) || "list_waste".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.agriwaste.FarmerAgriWasteFragment.newInstance(), true);
        } else if ("waste_buyer".equalsIgnoreCase(role) || "marketplace_waste".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.agriwaste.AgriWasteMarketplaceFragment.newInstance(), true);
        } else if ("seller".equalsIgnoreCase(role) || "buy_products".equalsIgnoreCase(role) || "marketplace_products".equalsIgnoreCase(role)) {
            loadFragment(FarmingProductsFragment.newInstance(), true);
        } else if ("seller_store".equalsIgnoreCase(role) || "seller_portal".equalsIgnoreCase(role)) {
            loadFragment(SellerStoreFragment.newInstance(), true);
        } else if ("cart".equalsIgnoreCase(role) || "shopping_cart".equalsIgnoreCase(role)) {
            loadFragment(CartFragment.newInstance(), true);
        } else if ("orders".equalsIgnoreCase(role) || "farmer_orders".equalsIgnoreCase(role)) {
            loadFragment(FarmerProductOrdersFragment.newInstance(), true);
        } else if ("contract".equalsIgnoreCase(role) || "contract_farming".equalsIgnoreCase(role) || "contracts".equalsIgnoreCase(role)) {
            loadFragment(new ContractFarmingFragment(), true);
        } else if ("company".equalsIgnoreCase(role) || "company_portal".equalsIgnoreCase(role) || "corporate".equalsIgnoreCase(role)) {
            loadFragment(new CompanyFragment(), true);
        } else if ("broker".equalsIgnoreCase(role) || "broker_trading".equalsIgnoreCase(role) || "trading".equalsIgnoreCase(role) || "bulk_trading".equalsIgnoreCase(role)) {
            loadFragment(BrokerTradingFragment.newInstance(), true);
        } else if ("broker_portal".equalsIgnoreCase(role) || "broker_dashboard".equalsIgnoreCase(role)) {
            loadFragment(BrokerFragment.newInstance(), true);
        } else if ("sell_produce".equalsIgnoreCase(role) || "farmer_produce".equalsIgnoreCase(role) || "produce_selling".equalsIgnoreCase(role)) {
            loadFragment(FarmerProduceFragment.newInstance(), true);
        } else if ("customer".equalsIgnoreCase(role) || "customer_store".equalsIgnoreCase(role) || "buy_produce".equalsIgnoreCase(role) || "fresh_produce".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.customer.CustomerFragment.newInstance(), true);
        } else if ("delivery".equalsIgnoreCase(role) || "delivery_portal".equalsIgnoreCase(role) || "logistics".equalsIgnoreCase(role) || "delivery_partner".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.delivery.DeliveryFragment.newInstance(), true);
        } else if ("delivery_history".equalsIgnoreCase(role)) {
            loadFragment(com.example.ui.delivery.DeliveryHistoryFragment.newInstance(), true);
        } else {
            Toast.makeText(this, "Opening " + role + " module...", Toast.LENGTH_SHORT).show();
        }
    }
}
