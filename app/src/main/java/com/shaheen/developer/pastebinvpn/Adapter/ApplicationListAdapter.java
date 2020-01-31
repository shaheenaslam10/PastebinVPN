package com.shaheen.developer.pastebinvpn.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.shaheen.developer.pastebinvpn.Models.PInfo;
import com.shaheen.developer.pastebinvpn.R;
import com.shaheen.developer.pastebinvpn.SqlieDataBase.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Shani on 8/16/2019.
 */

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {



    private ArrayList<PInfo> arrayList = new ArrayList<>();
    private Context context;
    private String res;
    private DatabaseHelper databaseHelper;

    public ApplicationListAdapter(ArrayList<PInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.applications_list_item, viewGroup, false);
        return new ApplicationListAdapter.ViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final ApplicationListAdapter.ViewHolder holder, final int position) {


        final PInfo model = arrayList.get(position);

       holder.icon.setImageDrawable(model.getIcon());
       holder.name.setText(model.getAppname());


       holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

               if (b){
                   databaseHelper.InsertApp(model.getAppname(),model.getPname(),model.getVersionName(),model.getVersionCode());
                   if (Build.VERSION.SDK_INT < 21) {
                       CompoundButtonCompat.setButtonTintList(holder.checkBox, ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                   } else {
                       holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));//setButtonTintList is accessible directly on API>19
                   }
               }else {
                   if (Build.VERSION.SDK_INT < 21) {
                       CompoundButtonCompat.setButtonTintList(holder.checkBox, ColorStateList.valueOf(context.getResources().getColor(R.color.gray_mid)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                   } else {
                       holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray_mid)));//setButtonTintList is accessible directly on API>19
                   }
                   ArrayList<PInfo> list = databaseHelper.getAllAPPs();
                   for (int i = 0; i < list.size(); i++) {
                       if (list.get(i).getPname().equals(model.getPname())){
                           databaseHelper.deleteNote(list.get(i));
                       }
                   }
               }
           }
       });
        if (CheckExistence(model.getPname())){
            holder.checkBox.setChecked(true);
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(holder.checkBox, ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));//setButtonTintList is accessible directly on API>19
            }
        }else {
            holder.checkBox.setChecked(false);

        }

    }

    private boolean CheckExistence(String packageName){
        boolean result = false;

        ArrayList<PInfo> list = databaseHelper.getAllAPPs();
        for (int i = 0; i <list.size() ; i++) {
            if (list.get(i).getPname().equals(packageName)){
                result = true;
            }
        }

        return result;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView icon;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.app_name);
            icon = (ImageView) itemView.findViewById(R.id.app_icon);
            checkBox = (CheckBox)itemView.findViewById(R.id.app_check);
        }
    }
}
