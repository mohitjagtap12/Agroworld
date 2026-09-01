package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.CartItem;
import com.example.model.SellerProduct;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying and modifying items in the Farmer shopping cart.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemInteractionListener {
        void onQuantityChanged(String productId, int newQuantity);
        void onRemoveItem(String productId);
    }

    private List<CartItem> cartItems = new ArrayList<>();
    private final OnCartItemInteractionListener listener;

    public CartAdapter(OnCartItemInteractionListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCartItemEmoji;
        private final TextView tvCartItemName;
        private final TextView tvCartItemSeller;
        private final TextView tvCartUnitPrice;
        private final ImageView btnRemoveCartItem;
        private final MaterialButton btnCartMinus;
        private final TextView tvCartItemQuantity;
        private final MaterialButton btnCartPlus;
        private final TextView tvCartItemSubtotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartItemEmoji = itemView.findViewById(R.id.tvCartItemEmoji);
            tvCartItemName = itemView.findViewById(R.id.tvCartItemName);
            tvCartItemSeller = itemView.findViewById(R.id.tvCartItemSeller);
            tvCartUnitPrice = itemView.findViewById(R.id.tvCartUnitPrice);
            btnRemoveCartItem = itemView.findViewById(R.id.btnRemoveCartItem);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
            tvCartItemQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
            tvCartItemSubtotal = itemView.findViewById(R.id.tvCartItemSubtotal);
        }

        public void bind(CartItem item, OnCartItemInteractionListener listener) {
            SellerProduct p = item.getProduct();
            if (p != null) {
                tvCartItemEmoji.setText(p.getImageEmoji() != null ? p.getImageEmoji() : "🌱");
                tvCartItemName.setText(p.getName());
                tvCartItemSeller.setText("Seller: " + p.getSellerName());
                tvCartUnitPrice.setText(String.format("₹%.0f / %s", p.getPrice(), p.getUnit()));
                tvCartItemQuantity.setText(item.getQuantity() + " " + p.getUnit());
            } else {
                tvCartItemQuantity.setText(String.valueOf(item.getQuantity()));
            }

            tvCartItemSubtotal.setText(String.format("₹%.0f", item.getSubtotal()));

            btnCartMinus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(item.getProductId(), item.getQuantity() - 1);
                }
            });

            btnCartPlus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(item.getProductId(), item.getQuantity() + 1);
                }
            });

            btnRemoveCartItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(item.getProductId());
                }
            });
        }
    }
}
