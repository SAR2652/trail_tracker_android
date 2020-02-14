package com.example.poachernotify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.MyViewHolder>
{
    private Context mCtx;
    public List<Camera> Cameras;
    public ProgressDialog progressDialog;

    public CameraListAdapter(Context mCtx, List<Camera> Cameras)
    {
        this.mCtx = mCtx;
        this.Cameras = Cameras;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout camera_item_layout;
        TextView camera_id, latitude_val, longitude_val, zone_val;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            this.camera_id = (TextView) itemView.findViewById(R.id.camera_id);
            this.latitude_val = (TextView) itemView.findViewById(R.id.latitude_val);
            this.longitude_val = (TextView) itemView.findViewById(R.id.longitude_val);
            this.zone_val=(TextView)itemView.findViewById(R.id.zone_val);
            this.camera_item_layout = (LinearLayout) itemView.findViewById(R.id.camera_item_layout);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        LayoutInflater inflater=LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.camera_list_item, parent,false);
        return new MyViewHolder(view);
    }
    
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final Camera camera = Cameras.get(position);
        holder.camera_id.setText(Integer.toString(camera.ret_camera_id()));
        holder.latitude_val.setText(Integer.toString(camera.retLat()));
        holder.longitude_val.setText(Integer.toString(camera.retLong()));
        holder.zone_val.setText(camera.retZone());

        holder.camera_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mCtx, VideoActivity.class);
                Bundle intentBundle=new Bundle();
                progressDialog = new ProgressDialog(mCtx);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Getting the list of available cameras...");
                progressDialog.show();
                intentBundle.putString("camera_id", Integer.toString(camera.ret_camera_id()));
                intentBundle.putString("latitude", Integer.toString(camera.retLat()));
                intentBundle.putString("longitude", Integer.toString(camera.retLong()));
                intentBundle.putString("zone", camera.retZone());
                intent.putExtras(intentBundle);
                mCtx.startActivity(intent);
                progressDialog.dismiss();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Cameras.size();
    }
}
