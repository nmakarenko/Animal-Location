package com.evostest.animallocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evostest.animallocation.model.Animal;
import com.evostest.animallocation.model.AnimalType;
import com.evostest.animallocation.model.Bird;
import com.evostest.animallocation.model.Mammal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimalsRVAdapter extends RecyclerView.Adapter<AnimalsRVAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position, boolean location);
    }

    public ArrayList<Animal> data;
    private final OnItemClickListener listener;
    Context context;
    FloatingActionButton fab;

    HashMap<Integer, RelativeLayout> mapRlItems = new HashMap<>();
    public static boolean longClick = false;

    public AnimalsRVAdapter(Context context, FloatingActionButton fab, ArrayList<Animal> animals, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.data = animals;
        this.fab = fab;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.animals_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        if (!longClick) {
            viewHolder.llItem.setTranslationX(-MainActivity.smallestWidth / 8);
        }

        mapRlItems.put(i, viewHolder.rlDelItem);

        ((View) mapRlItems.get(i).getParent()).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!longClick) {
                    fab.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_done_white_24dp));
                    animate();
                }
                return true;
            }
        });
        LinearLayout.LayoutParams layoutParamsll = new LinearLayout.LayoutParams(
                MainActivity.smallestWidth / 8, MainActivity.smallestWidth / 6);
        viewHolder.rlDelItem.setLayoutParams(layoutParamsll);
        viewHolder.rlDelItem.setGravity(Gravity.CENTER);

        Bitmap iconDel = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.delete);
        if (iconDel.getWidth() > MainActivity.smallestWidth / 8) {
            viewHolder.ivToLocation.setImageBitmap(resizeImage(iconDel, MainActivity.smallestWidth / 8));
        }

        viewHolder.tvName.setText(data.get(i).getName());
        Bitmap icon;

        switch (data.get(i).getType()) {
            case MAMMAL:
                icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.mammal);

                if (((Mammal) data.get(i)).hasMilk) {
                    viewHolder.tvCharacteristic.setText(context.getResources().getString(R.string.has_milk));
                } else {
                    viewHolder.tvCharacteristic.setText(context.getResources().getString(R.string.has_not_milk));
                }
                break;
            case BIRD:
                icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.bird);

                if (((Bird) data.get(i)).canFly) {
                    viewHolder.tvCharacteristic.setText(context.getResources().getString(R.string.can_fly));
                } else {
                    viewHolder.tvCharacteristic.setText(context.getResources().getString(R.string.can_not_fly));
                }
                break;

            default:
                icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.bird);
                viewHolder.tvCharacteristic.setText(context.getResources().getString(R.string.no_data));
                break;
        }

        viewHolder.ivType.setImageBitmap(resizeImage(icon, MainActivity.smallestWidth / 6));

        Bitmap button = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.location_pointer);
        viewHolder.ivToLocation.setImageBitmap(resizeImage(button, MainActivity.smallestWidth / 10));

        final int position = i;
        viewHolder.rlDelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, false);
            }
        });

        viewHolder.ivToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llItem;

        ImageView ivType;
        ImageView ivToLocation;
        ImageView ivDelItem;

        RelativeLayout rlDelItem;

        TextView tvName;
        TextView tvCharacteristic;

        ViewHolder(View itemView) {
            super(itemView);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);

            ivType = (ImageView) itemView.findViewById(R.id.ivType);
            ivToLocation = (ImageView) itemView.findViewById(R.id.ivToLocation);
            ivDelItem = (ImageView) itemView.findViewById(R.id.ivDelItem);

            rlDelItem = (RelativeLayout) itemView.findViewById(R.id.rlDelItem);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCharacteristic = (TextView) itemView.findViewById(R.id.tvCharacteristic);
        }
    }

    public void animate() {
            if (!longClick) {
                longClick = true;
                for (Map.Entry<Integer, RelativeLayout> entry : mapRlItems.entrySet()) {

                    if (entry.getValue() != null) {
                        entry.getValue().getChildAt(0).setVisibility(View.VISIBLE);
                        ((View) entry.getValue().getParent()).animate().translationX(0);//.setDuration(500);
                    }
                }
            }
    }

    public void animateBack() {
            for (Map.Entry<Integer, RelativeLayout> entry : mapRlItems.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().getChildAt(0).setVisibility(View.INVISIBLE);
                    ((View) entry.getValue().getParent()).animate().translationX(-MainActivity.smallestWidth / 8);//.setDuration(500);
                }
            }
            longClick = false;
    }


    private Bitmap resizeImage(Bitmap image, int scaleSize) {
        int widthB = image.getWidth();
        int heightB = image.getHeight();

        float excessSizeRatio = widthB > heightB ? (float) widthB / scaleSize : (float) heightB / scaleSize;

        Bitmap resizedPhoto = Bitmap.createScaledBitmap(image,
                (int) (widthB / excessSizeRatio), (int) (heightB / excessSizeRatio), true);
        return resizedPhoto;
    }

}

