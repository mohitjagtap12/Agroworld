package com.example.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.adapter.FarmerProductOrderAdapter;
import com.example.viewmodel.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

/**
 * Farmer Product Orders Fragment (Java + XML).
 * Displays order tracking history, delivery status timelines, and supplier receipts.
 */
public class FarmerProductOrdersFragment extends Fragment {

    private CartViewModel cartViewModel;
    private FarmerProductOrderAdapter adapter;

    private MaterialToolbar toolbarFarmerOrders;
    private ChipGroup chipGroupFarmerOrders;
    private LinearLayout layoutEmptyFarmerOrders;
    private RecyclerView rvFarmerOrders;

    public static FarmerProductOrdersFragment newInstance() {
        return new FarmerProductOrdersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farmer_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        cartViewModel.refreshOrders();
    }

    private void initViews(View view) {
        toolbarFarmerOrders = view.findViewById(R.id.toolbarFarmerOrders);
        chipGroupFarmerOrders = view.findViewById(R.id.chipGroupFarmerOrders);
        layoutEmptyFarmerOrders = view.findViewById(R.id.layoutEmptyFarmerOrders);
        rvFarmerOrders = view.findViewById(R.id.rvFarmerOrders);
    }

    private void setupRecyclerView() {
        adapter = new FarmerProductOrderAdapter();
        rvFarmerOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFarmerOrders.setAdapter(adapter);
    }

    private void setupListeners() {
        toolbarFarmerOrders.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        chipGroupFarmerOrders.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                cartViewModel.filterOrdersByStatus("All");
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chipOrderAll) {
                cartViewModel.filterOrdersByStatus("All");
            } else if (id == R.id.chipOrderActive) {
                cartViewModel.filterOrdersByStatus("Active");
            } else if (id == R.id.chipOrderPending) {
                cartViewModel.filterOrdersByStatus("Pending");
            } else if (id == R.id.chipOrderCompleted) {
                cartViewModel.filterOrdersByStatus("Completed");
            }
        });
    }

    private void observeViewModel() {
        cartViewModel.getFarmerOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
            if (orders == null || orders.isEmpty()) {
                layoutEmptyFarmerOrders.setVisibility(View.VISIBLE);
                rvFarmerOrders.setVisibility(View.GONE);
            } else {
                layoutEmptyFarmerOrders.setVisibility(View.GONE);
                rvFarmerOrders.setVisibility(View.VISIBLE);
            }
        });
    }
}
