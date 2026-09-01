package com.example.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.adapter.ServiceGridAdapter;
import com.example.databinding.FragmentFarmerDashboardBinding;
import com.example.model.FarmerServiceItem;
import com.example.repository.FarmerRepository;
import com.example.viewmodel.FarmerViewModel;

/**
 * Main dashboard screen for the Farmer Module.
 */
public class FarmerDashboardFragment extends Fragment implements ServiceGridAdapter.OnServiceClickListener {

    public interface OnFarmerNavListener {
        void onNavigateToCrops();
        void onNavigateToAiDisease(String cropId, String cropName);
        void onNavigateToActivities();
        void onNavigateToProfile();
        void onOpenRolePortal(String role);
    }

    private FragmentFarmerDashboardBinding binding;
    private FarmerViewModel viewModel;
    private ServiceGridAdapter serviceAdapter;

    public FarmerDashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmerDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        setupRecyclerView();
        setupHeader();
        observeViewModel();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        serviceAdapter = new ServiceGridAdapter(this);
        binding.rvFarmerServices.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvFarmerServices.setAdapter(serviceAdapter);
    }

    private void setupHeader() {
        FarmerRepository repo = FarmerRepository.getInstance();
        binding.tvFarmerName.setText(repo.getFarmerName());
        binding.tvFarmerLocation.setText("📍 " + repo.getVillage() + ", " + repo.getTaluka() + " (" + repo.getDistrict() + ") • " + repo.getTotalLandAcres() + " Acres");
    }

    private void observeViewModel() {
        viewModel.getServicesLiveData().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                serviceAdapter.submitList(services);
            }
        });

        viewModel.getCropsLiveData().observe(getViewLifecycleOwner(), crops -> {
            if (crops != null) {
                binding.tvCountCrops.setText(String.valueOf(crops.size()));
            }
        });

        viewModel.getActivitiesLiveData().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                int pendingCount = 0;
                for (int i = 0; i < activities.size(); i++) {
                    if ("Pending".equalsIgnoreCase(activities.get(i).getStatus()) || "Open".equalsIgnoreCase(activities.get(i).getStatus())) {
                        pendingCount++;
                    }
                }
                binding.tvCountActivities.setText(String.valueOf(pendingCount));
            }
        });
    }

    private void setupClickListeners() {
        binding.cardQuickAiScan.setOnClickListener(v -> {
            if (getActivity() instanceof OnFarmerNavListener) {
                ((OnFarmerNavListener) getActivity()).onNavigateToAiDisease(null, "Tomato");
            }
        });

        binding.btnQuickScan.setOnClickListener(v -> {
            if (getActivity() instanceof OnFarmerNavListener) {
                ((OnFarmerNavListener) getActivity()).onNavigateToAiDisease(null, "Tomato");
            }
        });

        binding.cardMetricCrops.setOnClickListener(v -> {
            if (getActivity() instanceof OnFarmerNavListener) {
                ((OnFarmerNavListener) getActivity()).onNavigateToCrops();
            }
        });

        binding.cardMetricActivities.setOnClickListener(v -> {
            if (getActivity() instanceof OnFarmerNavListener) {
                ((OnFarmerNavListener) getActivity()).onNavigateToActivities();
            }
        });

        binding.cardMetricLabour.setOnClickListener(v -> {
            if (getActivity() instanceof OnFarmerNavListener) {
                ((OnFarmerNavListener) getActivity()).onOpenRolePortal("labour");
            }
        });

        binding.ivNotificationBell.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notifications: You have 3 active updates", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onServiceClick(FarmerServiceItem service) {
        if (getActivity() instanceof OnFarmerNavListener) {
            OnFarmerNavListener nav = (OnFarmerNavListener) getActivity();
            switch (service.getActionKey()) {
                case FarmerServiceItem.ACTION_MY_CROPS:
                    nav.onNavigateToCrops();
                    break;
                case FarmerServiceItem.ACTION_AI_DISEASE:
                    nav.onNavigateToAiDisease(null, "Tomato");
                    break;
                case FarmerServiceItem.ACTION_HIRE_LABOUR:
                    nav.onOpenRolePortal("labour");
                    break;
                case FarmerServiceItem.ACTION_BUY_PRODUCTS:
                    nav.onOpenRolePortal("seller");
                    break;
                case FarmerServiceItem.ACTION_CONTRACT_FARMING:
                    nav.onOpenRolePortal("contract_farming");
                    break;
                case FarmerServiceItem.ACTION_LIST_AGRI_WASTE:
                    nav.onOpenRolePortal("waste");
                    break;
                case FarmerServiceItem.ACTION_BROKER_TRADING:
                    nav.onOpenRolePortal("broker");
                    break;
                case FarmerServiceItem.ACTION_SELL_PRODUCE:
                    nav.onOpenRolePortal("sell_produce");
                    break;
                case FarmerServiceItem.ACTION_DELIVERY_PORTAL:
                    nav.onOpenRolePortal("delivery");
                    break;
                case FarmerServiceItem.ACTION_MY_ACTIVITIES:
                    nav.onNavigateToActivities();
                    break;
                default:
                    Toast.makeText(getContext(), service.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
