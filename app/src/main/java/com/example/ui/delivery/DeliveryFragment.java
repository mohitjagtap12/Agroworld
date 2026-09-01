package com.example.ui.delivery;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.adapter.DeliveryAdapter;
import com.example.model.DeliveryJob;
import com.example.repository.DeliveryRepository;
import com.example.ui.farmer.FarmerContainerActivity;
import com.example.viewmodel.DeliveryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Common Delivery Partner Dashboard Fragment.
 * Unified portal for all agricultural logistics jobs (Seller Products, Farm Produce, Agri Waste, Broker Deals).
 */
public class DeliveryFragment extends Fragment implements DeliveryAdapter.OnDeliveryActionListener {

    private DeliveryViewModel viewModel;
    private DeliveryAdapter adapter;

    private TextView tvStatTodayJobs;
    private TextView tvStatTotalEarnings;
    private TextView tvStatPendingRequests;
    private TextView tvStatActiveDeliveries;
    private TextView tvStatCompletedDeliveries;
    private TextView tvJobCountBadge;
    private TextView tvOnlineStatusText;
    private SwitchMaterial switchPartnerAvailability;
    private TextInputEditText etSearchJobs;
    private ChipGroup chipGroupStatus;
    private ChipGroup chipGroupOrderType;
    private View layoutEmptyDelivery;
    private RecyclerView rvDeliveryJobs;
    private MaterialButton btnViewDeliveryHistory;
    private MaterialButton btnPartnerProfile;

    public static DeliveryFragment newInstance() {
        return new DeliveryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_portal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupFilters();
        setupActions();
        observeViewModel();
    }

    private void initViews(View root) {
        tvStatTodayJobs = root.findViewById(R.id.tvStatTodayJobs);
        tvStatTotalEarnings = root.findViewById(R.id.tvStatTotalEarnings);
        tvStatPendingRequests = root.findViewById(R.id.tvStatPendingRequests);
        tvStatActiveDeliveries = root.findViewById(R.id.tvStatActiveDeliveries);
        tvStatCompletedDeliveries = root.findViewById(R.id.tvStatCompletedDeliveries);
        tvJobCountBadge = root.findViewById(R.id.tvJobCountBadge);
        tvOnlineStatusText = root.findViewById(R.id.tvOnlineStatusText);
        switchPartnerAvailability = root.findViewById(R.id.switchPartnerAvailability);
        etSearchJobs = root.findViewById(R.id.etSearchJobs);
        chipGroupStatus = root.findViewById(R.id.chipGroupStatus);
        chipGroupOrderType = root.findViewById(R.id.chipGroupOrderType);
        layoutEmptyDelivery = root.findViewById(R.id.layoutEmptyDelivery);
        rvDeliveryJobs = root.findViewById(R.id.rvDeliveryJobs);
        btnViewDeliveryHistory = root.findViewById(R.id.btnViewDeliveryHistory);
        btnPartnerProfile = root.findViewById(R.id.btnPartnerProfile);
    }

    private void setupRecyclerView() {
        adapter = new DeliveryAdapter(requireContext(), this);
        rvDeliveryJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDeliveryJobs.setAdapter(adapter);
    }

    private void setupFilters() {
        // Status Filter Chips
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipStatusAll) {
                viewModel.setStatusFilter("ALL");
            } else if (id == R.id.chipStatusNew) {
                viewModel.setStatusFilter("NEW");
            } else if (id == R.id.chipStatusAssigned) {
                viewModel.setStatusFilter("ASSIGNED");
            } else if (id == R.id.chipStatusPickup) {
                viewModel.setStatusFilter("PICKUP");
            } else if (id == R.id.chipStatusInTransit) {
                viewModel.setStatusFilter("IN_TRANSIT");
            } else if (id == R.id.chipStatusCompleted) {
                viewModel.setStatusFilter("COMPLETED");
            }
        });

        // Order Type Filter Chips
        chipGroupOrderType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTypeAll) {
                viewModel.setTypeFilter("ALL");
            } else if (id == R.id.chipTypeSeller) {
                viewModel.setTypeFilter(DeliveryJob.TYPE_SELLER_PRODUCT);
            } else if (id == R.id.chipTypeProduce) {
                viewModel.setTypeFilter(DeliveryJob.TYPE_FARM_PRODUCE);
            } else if (id == R.id.chipTypeWaste) {
                viewModel.setTypeFilter(DeliveryJob.TYPE_AGRI_WASTE);
            } else if (id == R.id.chipTypeBroker) {
                viewModel.setTypeFilter(DeliveryJob.TYPE_BROKER_DEAL);
            }
        });

        // Search Input
        etSearchJobs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupActions() {
        // Online / Offline Switch
        switchPartnerAvailability.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.toggleAvailability();
            }
        });

        // Delivery History
        btnViewDeliveryHistory.setOnClickListener(v -> {
            if (getActivity() instanceof FarmerContainerActivity) {
                ((FarmerContainerActivity) getActivity()).loadFragment(DeliveryHistoryFragment.newInstance(), true);
            } else if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.farmerFragmentContainer, DeliveryHistoryFragment.newInstance())
                        .addToBackStack("DeliveryHistory")
                        .commit();
            }
        });

        // Vehicle Profile
        btnPartnerProfile.setOnClickListener(v -> showProfileDialog());
    }

    private void observeViewModel() {
        viewModel.getFilteredJobs().observe(getViewLifecycleOwner(), jobs -> {
            adapter.setJobs(jobs);
            if (jobs == null || jobs.isEmpty()) {
                layoutEmptyDelivery.setVisibility(View.VISIBLE);
                rvDeliveryJobs.setVisibility(View.GONE);
                tvJobCountBadge.setText("0 Jobs");
            } else {
                layoutEmptyDelivery.setVisibility(View.GONE);
                rvDeliveryJobs.setVisibility(View.VISIBLE);
                tvJobCountBadge.setText(jobs.size() + " Jobs");
            }
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                tvStatTodayJobs.setText(stats.todayJobs + " Jobs");
                tvStatTotalEarnings.setText("₹" + String.format("%.2f", stats.totalEarnings));
                tvStatPendingRequests.setText(stats.pendingRequests + " Available");
                tvStatActiveDeliveries.setText(stats.activeDeliveries + " Active");
                tvStatCompletedDeliveries.setText(stats.completedDeliveries + " Done");
            }
        });

        viewModel.getIsAvailable().observe(getViewLifecycleOwner(), isAvailable -> {
            switchPartnerAvailability.setChecked(Boolean.TRUE.equals(isAvailable));
            if (Boolean.TRUE.equals(isAvailable)) {
                tvOnlineStatusText.setText("ONLINE");
                tvOnlineStatusText.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tvOnlineStatusText.setText("OFFLINE");
                tvOnlineStatusText.setTextColor(android.graphics.Color.parseColor("#E0E0E0"));
            }
        });

        viewModel.getActionMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // DELIVERY ACTION CALLBACKS
    // ==========================================

    @Override
    public void onAccept(DeliveryJob job) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Accept Delivery Job")
                .setMessage("Are you sure you want to accept delivery for " + job.getItemsSummary() + " (Fee: ₹" + String.format("%.0f", job.getDeliveryFee()) + ")?")
                .setPositiveButton("Accept & Assign", (dialog, which) -> viewModel.acceptJob(job.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDecline(DeliveryJob job) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Decline Delivery")
                .setMessage("Decline this job? It will be made available to other logistics partners.")
                .setPositiveButton("Decline", (dialog, which) -> viewModel.rejectJob(job.getId()))
                .setNegativeButton("Keep", null)
                .show();
    }

    @Override
    public void onSchedulePickup(DeliveryJob job) {
        final String[] options = new String[]{
                "In 30 Minutes",
                "In 1 Hour",
                "Today Evening (05:00 PM)",
                "Tomorrow Morning (08:00 AM)"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Schedule Pickup Time")
                .setItems(options, (dialog, which) -> viewModel.schedulePickup(job.getId(), options[which]))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onMarkPickedUp(DeliveryJob job) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Goods Picked Up")
                .setMessage("Have you verified the parcel items at " + job.getSourceName() + " and loaded them into your vehicle?")
                .setPositiveButton("Yes, Goods Loaded", (dialog, which) -> viewModel.markPickedUp(job.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onStartInTransit(DeliveryJob job) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Start In-Transit Route")
                .setMessage("Start navigation towards destination:\n" + job.getDestinationLocation() + "?")
                .setPositiveButton("Start Transit", (dialog, which) -> viewModel.markInTransit(job.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDeliver(DeliveryJob job) {
        showDeliveryConfirmationDialog(job);
    }

    @Override
    public void onCallPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Calling: " + phone, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(DeliveryJob job) {
        showJobDetailsDialog(job);
    }

    // ==========================================
    // DIALOGS & BOTTOM SHEETS
    // ==========================================

    private void showDeliveryConfirmationDialog(DeliveryJob job) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delivery_confirmation, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        TextView tvSummary = dialogView.findViewById(R.id.tvDialogDeliverySummary);
        TextInputEditText etRecipient = dialogView.findViewById(R.id.etRecipientName);
        RadioGroup rgMethod = dialogView.findViewById(R.id.rgConfirmationMethod);
        TextInputEditText etRemarks = dialogView.findViewById(R.id.etDeliveryRemarks);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelDialog);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirmDeliverySubmit);

        tvSummary.setText(job.getId() + " • " + job.getItemsSummary() + "\nDrop: " + job.getDestinationLocation());
        etRecipient.setText(job.getDestinationName());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String recipient = etRecipient.getText() != null ? etRecipient.getText().toString().trim() : "";
            if (recipient.isEmpty()) {
                etRecipient.setError("Please enter recipient name");
                return;
            }

            int selectedRadioId = rgMethod.getCheckedRadioButtonId();
            String method = "OTP Verified";
            if (selectedRadioId == R.id.rbDirectHandover) {
                method = "Direct Handover";
            } else if (selectedRadioId == R.id.rbFieldGateDrop) {
                method = "Field Gate Drop";
            }

            String remarks = etRemarks.getText() != null ? etRemarks.getText().toString().trim() : "";
            viewModel.markDelivered(job.getId(), recipient, method, remarks);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showJobDetailsDialog(DeliveryJob job) {
        StringBuilder sb = new StringBuilder();
        sb.append("📦 Order Reference: ").append(job.getOrderId()).append("\n");
        sb.append("🏷 Type: ").append(job.getOrderTypeLabel()).append("\n");
        sb.append("💰 Delivery Payout: ₹").append(String.format("%.2f", job.getDeliveryFee())).append("\n");
        sb.append("⚖️ Weight: ").append(job.getQuantity()).append(" ").append(job.getUnit()).append("\n\n");
        sb.append("📍 PICKUP:\n").append(job.getSourceName()).append("\n").append(job.getSourceLocation()).append("\n📞 ").append(job.getSourcePhone()).append("\n\n");
        sb.append("📍 DESTINATION:\n").append(job.getDestinationName()).append("\n").append(job.getDestinationLocation()).append("\n📞 ").append(job.getDestinationPhone()).append("\n\n");
        sb.append("🚦 Current Status: ").append(job.getStatus()).append("\n");
        if (job.getCompletedAt() != null) {
            sb.append("✅ Completed At: ").append(job.getCompletedAt()).append("\n");
            sb.append("👤 Received By: ").append(job.getRecipientName()).append(" (").append(job.getConfirmationMethod()).append(")\n");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(job.getId() + " - " + job.getItemsSummary())
                .setMessage(sb.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private void showProfileDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delivery_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        DeliveryRepository repo = DeliveryRepository.getInstance();

        TextView tvName = dialogView.findViewById(R.id.tvProfileName);
        TextView tvPhone = dialogView.findViewById(R.id.tvProfilePhone);
        TextView tvVehicle = dialogView.findViewById(R.id.tvProfileVehicle);
        TextView tvCapacity = dialogView.findViewById(R.id.tvProfileCapacity);
        TextView tvServiceArea = dialogView.findViewById(R.id.tvProfileServiceArea);
        TextView tvCompleted = dialogView.findViewById(R.id.tvProfileTotalCompleted);
        TextView tvPayout = dialogView.findViewById(R.id.tvProfileTotalPayout);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseProfileDialog);

        tvName.setText(repo.getPartnerName());
        tvPhone.setText(repo.getPartnerPhone() + " • Verified Partner");
        tvVehicle.setText("🚚 " + repo.getVehicleType() + " • " + repo.getVehicleNumber());
        tvCapacity.setText("Max Payload Capacity: " + String.format("%.0f", repo.getVehicleCapacityKg()) + " kg");
        tvServiceArea.setText("📍 " + repo.getServiceArea());
        tvCompleted.setText(String.valueOf(repo.getCompletedDeliveriesCount()));
        tvPayout.setText("₹" + String.format("%.0f", repo.getTotalEarnings()));

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }
}
