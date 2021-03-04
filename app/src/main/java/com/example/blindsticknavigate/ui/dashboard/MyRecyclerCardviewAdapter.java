package com.example.blindsticknavigate.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blindsticknavigate.R;

import java.util.List;

public class MyRecyclerCardviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //数据集
    public List<Person> personlist;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
    DashboardFragment fragment=null;

    public MyRecyclerCardviewAdapter(List<Person> personlist){
        super();
        this.personlist = personlist;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener mOnItemLongClickListener,DashboardFragment fragment) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
        this.fragment=fragment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactitem,parent,false);
        return new PersonViewHolder(view);

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((PersonViewHolder)holder).pic.setImageResource(R.drawable.portrait);
        ((PersonViewHolder)holder).name.setText(personlist.get(position).name);
        ((PersonViewHolder)holder).phone.setText(personlist.get(position).phone);

        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //mOnItemLongClickListener.onItemLongClick(holder.,holder.itemView,position,position);
                    fragment.initdeletepersonwindow(position);
                    fragment.deletedialog.show();
                    return true;
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return personlist.size();
    }

    public void removeItem(int pos){
        personlist.remove(pos);
        notifyItemRemoved(pos);
    }


    public class PersonViewHolder extends RecyclerView.ViewHolder {
        public ImageView pic;
        public TextView nametag;
        public EditText name;
        public TextView phonetag;
        public EditText phone;

        public PersonViewHolder(View itemView) {
            super(itemView);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            nametag = (TextView) itemView.findViewById(R.id.nametag);
            name = (EditText) itemView.findViewById(R.id.name);
            phonetag = (TextView) itemView.findViewById(R.id.phonetag);
            phone = (EditText) itemView.findViewById(R.id.phone);
        }
    }

    public static class Person{
        String name;
        String phone;
        int picresid;
        public Person(String name,String phone,int picresid){
            this.name=name;
            this.phone=phone;
            this.picresid=picresid;
        }
    }
}
