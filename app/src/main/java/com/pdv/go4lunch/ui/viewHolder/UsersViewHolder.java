package com.pdv.go4lunch.ui.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_picture_workmate)
    public ImageView mPictureWorkmate;
    @BindView(R.id.item_name_workmate)
    public TextView mNameWorkmate;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateWithUsers(User user) {
        if (user != null) {
            if (user.getUserName() != null) {
                mNameWorkmate.setText(user.getUserName());
            }

            if (user.getUrlPicture() != null) {
                Glide.with(mPictureWorkmate.getContext())
                        .load(user.getUrlPicture())
                        .circleCrop()
                        .into(mPictureWorkmate);
            }
        }
    }
}
