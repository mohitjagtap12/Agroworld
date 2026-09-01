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

import com.example.databinding.FragmentFarmerProfileBinding;
import com.example.repository.FarmerRepository;
import com.example.viewmodel.FarmerViewModel;

/**
 * Fragment displaying and managing the Farmer's Profile, KCC limit, and field details.
 */
public class FarmerProfileFragment extends Fragment {

    private FragmentFarmerProfileBinding binding;
    private FarmerViewModel viewModel;

    public FarmerProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmerProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        loadProfileData();
        setupClickListeners();
    }

    private void loadProfileData() {
        FarmerRepository repo = FarmerRepository.getInstance();
        binding.tvProfileName.setText(repo.getFarmerName());
        binding.tvProfilePhone.setText(repo.getFarmerPhone());
        binding.tvProfileLocationBadge.setText("📍 " + repo.getVillage() + ", " + repo.getTaluka() + " (" + repo.getDistrict() + ") • " + repo.getTotalLandAcres() + " Acres");

        binding.etProfileName.setText(repo.getFarmerName());
        binding.etProfilePhone.setText(repo.getFarmerPhone());
        binding.etProfileVillage.setText(repo.getVillage());
        binding.etProfileTaluka.setText(repo.getTaluka());
        binding.etProfileDistrict.setText(repo.getDistrict());
        binding.etProfileLandArea.setText(repo.getTotalLandAcres());
    }

    private void setupClickListeners() {
        binding.btnSaveProfile.setOnClickListener(v -> {
            String name = binding.etProfileName.getText() != null ? binding.etProfileName.getText().toString().trim() : "";
            String phone = binding.etProfilePhone.getText() != null ? binding.etProfilePhone.getText().toString().trim() : "";
            String village = binding.etProfileVillage.getText() != null ? binding.etProfileVillage.getText().toString().trim() : "";
            String taluka = binding.etProfileTaluka.getText() != null ? binding.etProfileTaluka.getText().toString().trim() : "";
            String district = binding.etProfileDistrict.getText() != null ? binding.etProfileDistrict.getText().toString().trim() : "";
            String landArea = binding.etProfileLandArea.getText() != null ? binding.etProfileLandArea.getText().toString().trim() : "";

            if (name.isEmpty()) {
                binding.etProfileName.setError("Name is required");
                return;
            }

            viewModel.updateProfile(name, phone, village, taluka, district, landArea);
            loadProfileData();
            Toast.makeText(getContext(), "Farmer profile updated successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
