package com.example.ui.delivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.adapter.DeliveryHistoryAdapter;
import com.example.model.DeliveryJob;
import com.example.repository.DeliveryRepository;

import java.util.List;

/**
 * Fragment displaying completed delivery history, receipts, and payout summaries.
 */
public class DeliveryHistoryFragment extends Fragment {

    private RecyclerView rvDeliveryHistory;
    private DeliveryHistoryAdapter adapter;
    private View layoutEmptyHistory;
    private TextView tvHistTotalTrips;
    private TextView tvHistTotalPayout;
    private ImageButton btnBackFromHistory;

    public static DeliveryHistoryFragment newInstance() {
        return new DeliveryHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadHistoryData();
    }

    private void initViews(View root) {
        rvDeliveryHistory = root.findViewById(R.id.rvDeliveryHistory);
        layoutEmptyHistory = root.findViewById(R.id.layoutEmptyHistory);
        tvHistTotalTrips = root.findViewById(R.id.tvHistTotalTrips);
        tvHistTotalPayout = root.findViewById(R.id.tvHistTotalPayout);
        btnBackFromHistory = root.findViewById(R.id.btnBackFromHistory);

        btnBackFromHistory.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new DeliveryHistoryAdapter(requireContext());
        rvDeliveryHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDeliveryHistory.setAdapter(adapter);
    }

    private void loadHistoryData() {
        DeliveryRepository repo = DeliveryRepository.getInstance();
        List<DeliveryJob> completed = repo.getCompletedDeliveries();

        adapter.setHistoryList(completed);

        if (completed == null || completed.isEmpty()) {
            layoutEmptyHistory.setVisibility(View.VISIBLE);
            rvDeliveryHistory.setVisibility(View.GONE);
            tvHistTotalTrips.setText("0 Deliveries");
            tvHistTotalPayout.setText("₹0.00");
        } else {
            layoutEmptyHistory.setVisibility(View.GONE);
            rvDeliveryHistory.setVisibility(View.VISIBLE);
            tvHistTotalTrips.setText(completed.size() + " Deliveries");
            tvHistTotalPayout.setText("₹" + String.format("%.2f", repo.getTotalEarnings()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistoryData();
    }
}
