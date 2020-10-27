package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SiteEmployeeAdapter extends RecyclerView.Adapter<SiteEmployeeAdapter.SiteEmployeeViewHolder> implements Filterable {

    private Context mCtx;
    private List<SiteEmployee> siteEmployeeList;
    private List<SiteEmployee> siteEmployeeListFull;

    private static OnItemClickListener mListener;

    public boolean showShimmer = true;
    private int SHIMMER_ITEM_NUMBER = 5;

    @Override
    public Filter getFilter() {
        return SiteEmployeeFilter;
    }

    private Filter SiteEmployeeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SiteEmployee> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(siteEmployeeListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SiteEmployee employee : siteEmployeeListFull) {
                    if (employee.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(employee);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            siteEmployeeList.clear();
            siteEmployeeList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onMoreClick(int position);
    }

    public static void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SiteEmployeeAdapter(Context mCtx, List<SiteEmployee> siteEmployeeList) {
        this.mCtx = mCtx;
        this.siteEmployeeList = siteEmployeeList;
        siteEmployeeListFull = new ArrayList<>(siteEmployeeList);
    }

    @NonNull
    @Override
    public SiteEmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_recyclerview_list_layout_employee, null);
        return new SiteEmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteEmployeeViewHolder holder, int position) {

        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                holder.relativeLayout.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));

                holder.textViewFullname.setBackground(null);
                holder.textViewFullname.setText(siteEmployeeList.get(position).getName());

                holder.textViewId.setBackground(null);
                holder.textViewId.setText(siteEmployeeList.get(position).getEmpid() + "");

                String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());


                if (!isNetworkAvailable()) { // hide home, in, out textview when network is down
                    holder.textViewStatus.setVisibility(View.INVISIBLE);
                } else {
                    if (date.equals(siteEmployeeList.get(position).getDatecheck())) {
                        switch (siteEmployeeList.get(position).getTimestatus()) {
                            case "1": // Has time in
                                holder.textViewStatus.setText("IN");
                                holder.textViewStatus.setBackgroundResource(R.color.colorIn);
                                break;

                            case "2": // Has time in and time out
                                holder.textViewStatus.setText("OUT");
                                holder.textViewStatus.setBackgroundResource(R.color.colorOut);
                                break;
                            case "3": // Marked absent
                                holder.textViewStatus.setText("ABSENT");
                                holder.textViewStatus.setBackgroundResource(R.color.colorAbsent);
                                break;
                            default:
                        }
                    } else { // New day
                        holder.textViewStatus.setText("HOME");
                        holder.textViewStatus.setBackgroundResource(R.color.colorHome);
                    }
                }

                byte[] decodedString = Base64.decode(siteEmployeeList.get(position).getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.civ_photo.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
                holder.civ_photo.setBackground(null);
                holder.civ_photo.setImageBitmap(decodedByte);
            }
        }

        /* WORKING
        SiteEmployee siteEmployee = siteEmployeeList.get(position);

        holder.textViewFullname.setText(siteEmployee.getName());
        holder.textViewId.setText(siteEmployee.getEmpid()+"");

        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        if (date.equals(siteEmployee.getDatecheck())) {
            switch (siteEmployee.getTimestatus()) {
                case "1": // Has time in
                    holder.textViewStatus.setText("IN");
                    holder.textViewStatus.setBackgroundResource(R.color.colorIn);
                    break;

                case "2": // Has time in and time out
                    holder.textViewStatus.setText("OUT");
                    holder.textViewStatus.setBackgroundResource(R.color.colorOut);
                    break;

                default:
            }
        } else { // New day
            holder.textViewStatus.setText("HOME");
            holder.textViewStatus.setBackgroundResource(R.color.colorHome);
        }

        byte[] decodedString = Base64.decode(siteEmployee.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.civ_photo.setImageBitmap(decodedByte);

         */

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return showShimmer ? SHIMMER_ITEM_NUMBER : siteEmployeeList.size();
    }

    class SiteEmployeeViewHolder extends RecyclerView.ViewHolder {

        ShimmerFrameLayout shimmerFrameLayout;
        CircleImageView civ_photo;
        TextView textViewFullname, textViewId, textViewStatus;
        ImageView imageViewMore;

        RelativeLayout relativeLayout;

        public SiteEmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rl_list_layout_employee);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            textViewFullname = itemView.findViewById(R.id.tv_listlayout_fullname);
            textViewId = itemView.findViewById(R.id.tv_listlayout_id);
            textViewStatus = itemView.findViewById(R.id.tv_listlayout_status);
            civ_photo = itemView.findViewById(R.id.iv_listlayout_civ);
            imageViewMore = itemView.findViewById(R.id.iv_listlayout_more);
            imageViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onMoreClick(position);
                        }
                    }
                }

            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

}
