package com.example.myecom.controllers.holders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myecom.databinding.ItemVbProductBinding;

/**
 * Represents view holder for variant based product
 */
public class VBViewHolder extends RecyclerView.ViewHolder {
    public ItemVbProductBinding binding;

    public VBViewHolder(@NonNull ItemVbProductBinding b) {
        super(b.getRoot());
        this.binding = b;
    }
}