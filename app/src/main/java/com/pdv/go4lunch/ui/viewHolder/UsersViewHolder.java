package com.pdv.go4lunch.ui.viewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.fragment.MapFragment.PLACE_ID;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    //FOR UI
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
            if (user.getRestaurantName() != null) {
                if (itemView.getContext() instanceof DetailsActivity) {
                    mNameWorkmate.setText(user.getUserName() + " " + itemView.getResources().getString(R.string.joining));
                } else {
                    mNameWorkmate.setText(user.getUserName() + " " + itemView.getResources().getString(R.string.is_eating_at) + " " + user.getRestaurantName());
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                            intent.putExtra(PLACE_ID, user.getRestaurantId());
                            v.getContext().startActivity(intent);
                        }
                    });
                }
                mNameWorkmate.setTextColor(itemView.getResources().getColor(R.color.colorBlack));
            } else {
                mNameWorkmate.setText(user.getUserName() + " " + itemView.getResources().getString(R.string.hasnt_decided));
            }

            if (user.getUrlPicture() != null) {
                Glide.with(mPictureWorkmate.getContext())
                        .load(user.getUrlPicture())
                        .circleCrop()
                        .into(mPictureWorkmate);
            }else{
                Glide.with(mPictureWorkmate.getContext())
                        .load(R.drawable.no_picture_user)
                        .circleCrop()
                        .into(mPictureWorkmate);
            }
        }
    }
}
