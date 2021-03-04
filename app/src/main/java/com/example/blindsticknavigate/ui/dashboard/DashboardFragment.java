package com.example.blindsticknavigate.ui.dashboard;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blindsticknavigate.MainActivity;
import com.example.blindsticknavigate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    RecyclerView contactlist=null;
    FloatingActionButton add=null;
    AlertDialog.Builder dialog=null;
    public AlertDialog.Builder deletedialog=null;
    View view1=null;
    EditText addname=null;
    EditText addphone=null;
    List<MyRecyclerCardviewAdapter.Person> personlist=new ArrayList<>();
    View root=null;
    MyRecyclerCardviewAdapter adapter=null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        contactlist=(RecyclerView)root.findViewById(R.id.recyclerview);
        add=(FloatingActionButton) root.findViewById(R.id.addbutton);

        initVerticalRecyclerView();
        initaddpersonwindow();
        addname=(EditText) view1.findViewById(R.id.addnamecontent);
        addphone=(EditText) view1.findViewById(R.id.addphonecontet);
//        ConstraintLayout lyout=(ConstraintLayout)root.findViewById(R.id.frag_dashboard_layout);
//        lyout.setBackgroundColor(getResources().getColor(R.color.obstacle));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return root;
    }
    private void initVerticalRecyclerView() {

        //2.创建一个垂直的线性布局(一个布局管理器layoutManager只能绑定一个Recyclerview)
        GridLayoutManager layoutManager1 = new GridLayoutManager(this.getContext(),1, GridLayoutManager.VERTICAL,false);

        //找到RecyclerView，并设置布局管理器
        contactlist.setLayoutManager(layoutManager1);
        contactlist.setHasFixedSize(true);

        //3.取得数据集(此处，应根据不同的主题查询得不同的数据传入到 MyRecyclerCardviewAdapter中构建adapter)
        if(personlist.size()==0) {
            personlist.add(new MyRecyclerCardviewAdapter.Person("张三", "1234567890", R.drawable.portrait));
            personlist.add(new MyRecyclerCardviewAdapter.Person("李四", "0987654321", R.drawable.portrait));
        }
        //4.创建adapter
        adapter = new MyRecyclerCardviewAdapter(personlist);
        //将RecyclerView组件绑定adapter
        contactlist.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom=10;
            }
        };
        contactlist.addItemDecoration(itemDecoration);
        adapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }

        },this);

    }
    private void initaddpersonwindow(){

        dialog= new AlertDialog.Builder(this.getContext());

        view1 = View.inflate(this.getContext(), R.layout.addpersonwindow, null);
        dialog
                .setTitle("添加联系人")
                .setIcon(R.mipmap.ic_launcher)
                .setView(view1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n=addname.getText().toString();
                        String p=addphone.getText().toString();
                        if(n.length()<1||p.length()<1){
                            Toast.makeText(root.getContext(), "姓名和电话不能为空", Toast.LENGTH_SHORT).show();
                            ((ViewGroup)view1.getParent()).removeView(view1);
                            return;
                        }
                        personlist.add(new MyRecyclerCardviewAdapter.Person(n, p, R.drawable.portrait));
                        initVerticalRecyclerView();
                        addname.setText("");
                        addphone.setText("");
                        ((ViewGroup)view1.getParent()).removeView(view1);
                        contactlist.scrollToPosition(adapter.getItemCount()-1);
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addname.setText("");
                        addphone.setText("");
                        ((ViewGroup)view1.getParent()).removeView(view1);
//                        dialog.show().dismiss();
                    }
                })
                .create();

    }

    public void initdeletepersonwindow(int pos){

        deletedialog= new AlertDialog.Builder(this.getContext());

        deletedialog
                .setTitle("是否删除联系人？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.removeItem(pos);
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

    }
}