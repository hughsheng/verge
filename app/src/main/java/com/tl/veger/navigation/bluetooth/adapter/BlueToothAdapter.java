package com.tl.veger.navigation.bluetooth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tl.veger.R;
import com.tl.veger.navigation.bluetooth.service.CenterService;

import java.util.ArrayList;
import java.util.List;

/**
 * created by tl on 2019/8/3
 */
public class BlueToothAdapter extends RecyclerView.Adapter<BlueToothAdapter.Holder> {
  private Context context;
  private List<CenterService.BleDev> bleDevList=new ArrayList<>();
  private int layoutId;
  private BlueToothAdapterListener listener;


  public BlueToothAdapter(Context context, int layoutId) {
    this.context = context;
    this.layoutId = layoutId;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(layoutId, null);
    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    final CenterService.BleDev bleDev = bleDevList.get(position);
//    holder.title.setText(bleDev.dev.getName());
//    holder.content.setText(bleDev.dev.getAddress());

    holder.bluetooth_layout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.connect(bleDev);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return bleDevList.size();
  }

  public class Holder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView content;
    private LinearLayout bluetooth_layout;

    private Holder(View itemView) {
      super(itemView);
      bluetooth_layout = itemView.findViewById(R.id.bluetooth_layout);
    }
  }


  public void setListener(BlueToothAdapterListener listener) {
    this.listener = listener;
  }


  public void setData(List<CenterService.BleDev> list) {
    bleDevList.clear();
    bleDevList.addAll(list);
    notifyDataSetChanged();
  }


  public void clearData(){
    bleDevList.clear();
    notifyDataSetChanged();
  }

 public interface BlueToothAdapterListener {
    void connect(CenterService.BleDev bleDev);
  }

}
