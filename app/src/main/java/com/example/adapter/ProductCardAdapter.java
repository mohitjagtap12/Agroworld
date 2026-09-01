package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.SellerProduct;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying Agricultural Products in Farmer Marketplace and Seller Inventory.
 */
public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ProductViewHolder> {

    public interface OnProductActionListener {
        void onViewDetails(SellerProduct product);
        void onAddToCart(SellerProduct product);
        void onSellerEdit(SellerProduct product);
        void onSellerUpdateStock(SellerProduct product);
    }

    private List<SellerProduct> productList = new ArrayList<>();
    private final boolean isSellerMode;
    private final OnProductActionListener listener;

    public ProductCardAdapter(boolean isSellerMode, OnProductActionListener listener) {
        this.isSellerMode = isSellerMode;
        this.listener = listener;
    }

    public void setProducts(List<SellerProduct> products) {
        this.productList = (products != null) ? new ArrayList<>(products) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SellerProduct product = productList.get(position);
        holder.bind(product, isSellerMode, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductEmoji;
        private final TextView tvProductCategory;
        private final TextView tvProductBrand;
        private final TextView tvProductName;
        private final TextView tvStockStatusBadge;
        private final TextView tvProductDescription;
        private final TextView tvProductPrice;
        private final TextView tvSellerInfo;

        private final LinearLayout layoutFarmerActions;
        private final MaterialButton btnViewDetails;
        private final MaterialButton btnAddToCart;

        private final LinearLayout layoutSellerActions;
        private final MaterialButton btnSellerEdit;
        private final MaterialButton btnSellerStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductEmoji = itemView.findViewById(R.id.tvProductEmoji);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductBrand = itemView.findViewById(R.id.tvProductBrand);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvStockStatusBadge = itemView.findViewById(R.id.tvStockStatusBadge);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSellerInfo = itemView.findViewById(R.id.tvSellerInfo);

            layoutFarmerActions = itemView.findViewById(R.id.layoutFarmerActions);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);

            layoutSellerActions = itemView.findViewById(R.id.layoutSellerActions);
            btnSellerEdit = itemView.findViewById(R.id.btnSellerEdit);
            btnSellerStock = itemView.findViewById(R.id.btnSellerStock);
        }

        public void bind(SellerProduct product, boolean isSellerMode, OnProductActionListener listener) {
            tvProductEmoji.setText(product.getImageEmoji() != null ? product.getImageEmoji() : "🌱");
            tvProductCategory.setText(product.getCategory());
            tvProductBrand.setText("• " + (product.getBrand() != null ? product.getBrand() : "Agro Certified"));
            tvProductName.setText(product.getName());
            tvProductDescription.setText(product.getDescription());

            // Price per unit
            String priceStr = String.format("₹%.0f / %s", product.getPrice(), product.getUnit());
            tvProductPrice.setText(priceStr);

            // Seller Info
            tvSellerInfo.setText(String.format("🏪 %s • ⭐ %.1f", product.getSellerName(), product.getRating()));

            // Stock Badge & Out of Stock styling
            if (product.isOutOfStock()) {
                tvStockStatusBadge.setText("OUT OF STOCK");
                tvStockStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_error));
                btnAddToCart.setEnabled(false);
                btnAddToCart.setText("Out of Stock");
            } else {
                tvStockStatusBadge.setText(product.getStock() + " " + product.getUnit() + " left");
                tvStockStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.farmer_primary));
                btnAddToCart.setEnabled(true);
                btnAddToCart.setText("+ Cart");
            }

            if (isSellerMode) {
                layoutFarmerActions.setVisibility(View.GONE);
                layoutSellerActions.setVisibility(View.VISIBLE);

                btnSellerEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onSellerEdit(product);
                });

                btnSellerStock.setOnClickListener(v -> {
                    if (listener != null) listener.onSellerUpdateStock(product);
                });
            } else {
                layoutFarmerActions.setVisibility(View.VISIBLE);
                layoutSellerActions.setVisibility(View.GONE);

                btnViewDetails.setOnClickListener(v -> {
                    if (listener != null) listener.onViewDetails(product);
                });

                btnAddToCart.setOnClickListener(v -> {
                    if (listener != null) listener.onAddToCart(product);
                });

                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onViewDetails(product);
                });
            }
        }
    }
}
