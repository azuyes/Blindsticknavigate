package com.example.blindsticknavigate.ui.notifications;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blindsticknavigate.MainActivity;
import com.example.blindsticknavigate.R;
import com.example.blindsticknavigate.ui.dashboard.DashboardFragment;

import java.util.List;
public class MyRecyclerconfigviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public List<Config> configlist;
    private AdapterView.OnClickListener mOnClickListener;
    DashboardFragment fragment=null;
    MainActivity mactivity=null;
    Fragment fr=null;

    public MyRecyclerconfigviewAdapter(List<Config> configlist,Fragment fr){
        super();
        this.configlist = configlist;
        this.fr=fr;
    }

    public void setOnClickListener(AdapterView.OnClickListener mOnClickListener,MainActivity mactivity) {
        this.mOnClickListener = mOnClickListener;
        this.mactivity=mactivity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.configtag,parent,false);
        return new MyRecyclerconfigviewAdapter.configViewHolder(view);

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((configViewHolder)holder).title.setText(configlist.get(position).configtitle);

        if(mOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mactivity.gotofragment(fr,configlist.get(position).linkfragment);
//                    mactivity.navi();

                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return configlist.size();
    }

    public void removeItem(int pos){
        configlist.remove(pos);
        notifyItemRemoved(pos);
    }


    public class configViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public configViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.configtitle);
        }
    }

    public static class Config{
        public String configtitle;
        public Class linkfragment;
        public Config(String configtitle,Class linkfragment){
            this.configtitle=configtitle;
            this.linkfragment=linkfragment;
        }
    }
}
