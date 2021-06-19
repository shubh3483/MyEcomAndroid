package com.example.myecom.controllers.holders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myecom.databinding.ItemWbProductBinding;

/**
 * Represents view holder for weight based product
 */
public class WBViewHolder extends RecyclerView.ViewHolder {
    public ItemWbProductBinding binding;

    public WBViewHolder(@NonNull ItemWbProductBinding b) {
        super(b.getRoot());
        this.binding = b;
    }
}
