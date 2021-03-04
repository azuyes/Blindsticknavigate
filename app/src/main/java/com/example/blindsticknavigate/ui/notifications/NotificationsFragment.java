package com.example.blindsticknavigate.ui.notifications;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blindsticknavigate.MainActivity;
import com.example.blindsticknavigate.R;
import com.example.blindsticknavigate.ui.configmenu.AboutFragment;
import com.example.blindsticknavigate.ui.configmenu.PersonalprofileFragment;
import com.example.blindsticknavigate.ui.dashboard.DashboardFragment;
import com.example.blindsticknavigate.ui.dashboard.MyRecyclerCardviewAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    RecyclerView configmenu=null;
    public List<MyRecyclerconfigviewAdapter.Config> configlist=new ArrayList<>();
    View root=null;
    MyRecyclerconfigviewAdapter adapter=null;
    FragmentTransaction fragmentTransaction=null;
    private FragmentManager fmanager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        configmenu=(RecyclerView)root.findViewById(R.id.configmenu);
        initVerticalRecyclerView();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                NavController navController=((MainActivity)getActivity()).navController;
                navController.navigate(R.id.navigation_notifications);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this.getViewLifecycleOwner(), callback);
        return root;
    }


    private void initVerticalRecyclerView() {

        //2.创建一个垂直的线性布局(一个布局管理器layoutManager只能绑定一个Recyclerview)
        GridLayoutManager layoutManager1 = new GridLayoutManager(this.getContext(),1, GridLayoutManager.VERTICAL,false);

        //找到RecyclerView，并设置布局管理器
        configmenu.setLayoutManager(layoutManager1);
        configmenu.setHasFixedSize(true);

        //3.取得数据集(此处，应根据不同的主题查询得不同的数据传入到 MyRecyclerCardviewAdapter中构建adapter)
        if(configlist.size()==0) {
            configlist.add(new MyRecyclerconfigviewAdapter.Config("个人信息设置", PersonalprofileFragment.class));
            configlist.add(new MyRecyclerconfigviewAdapter.Config("关于我们", AboutFragment.class));
        }
        //4.创建adapter
        adapter = new MyRecyclerconfigviewAdapter(configlist,this);
        //将RecyclerView组件绑定adapter
        configmenu.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom=10;
            }
        };
        configmenu.addItemDecoration(itemDecoration);
        adapter.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, (MainActivity)getActivity());

    }
}