package com.abuunity.latihanfragmant.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abuunity.latihanfragmant.R;
import com.abuunity.latihanfragmant.ViewModel.MainViewModel;
import com.abuunity.latihanfragmant.adapter.HashtagAdapter;
import com.abuunity.latihanfragmant.adapter.UsersAdapter;
import com.abuunity.latihanfragmant.pojo.Hashtags;
import com.abuunity.latihanfragmant.pojo.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SocialAutoCompleteTextView searchBar;

    private SwipeRefreshLayout refreshLayout;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerViewUsers;
    private ArrayList<Users> usersArrayList;
    private  UsersAdapter usersAdapter;

    private HashtagAdapter hashtagAdapter;
    private RecyclerView recyclerViewTags;
    private ArrayList<Hashtags> hashtagsArrayList;
    private MainViewModel mainviewmodel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        searchBar = view.findViewById(R.id.search_bar);
        usersArrayList = new ArrayList<>();
        recyclerViewUsers = view.findViewById(R.id.rv_users);
        recyclerViewTags = view.findViewById(R.id.rv_tags);
        hashtagsArrayList = new ArrayList<>();

        getUsers();
        getHastags();

        refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.colorPrimarySoft,
                R.color.colorAccent
        );

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getUsers();
                getHastags();
                refreshLayout.setRefreshing(false);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getUsers();
//        getHastags();
//    }

    private void getHastags() {
        mainviewmodel = new ViewModelProvider(this).get(MainViewModel.class);

        mainviewmodel.getHashTagsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Hashtags>>() {
            @Override
            public void onChanged(ArrayList<Hashtags> hashtags) {
                hashtagsArrayList.clear();
                hashtagsArrayList.addAll(hashtags);
                loadHastags();
            }
        });
    }

    private void loadHastags() {
        hashtagAdapter= new HashtagAdapter(hashtagsArrayList);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTags.setAdapter(hashtagAdapter);
        recyclerViewTags.setItemAnimator(new DefaultItemAnimator());
    }


    private void getUsers() {
        mainviewmodel = new ViewModelProvider(this).get(MainViewModel.class);
        mainviewmodel.getUsersMutableLive().observe(getViewLifecycleOwner(), new Observer<ArrayList<Users>>() {
            @Override
            public void onChanged(ArrayList<Users> users) {
                usersArrayList.clear();
                usersArrayList.addAll(users);
                loadsUsers();
            }
        });
    }

    private void loadsUsers() {
        usersAdapter = new UsersAdapter(usersArrayList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setAdapter(usersAdapter);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
    }

    private void searchUsers(String s) {

        CollectionReference cities = firebaseFirestore.collection("users");
        Query query = cities.orderBy("username").startAt(s).endAt(s + "\uf8ff");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                usersArrayList.clear();
                for(DocumentSnapshot dataSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Users users = dataSnapshot.toObject(Users.class);
                    usersArrayList.add(users);
                }
                usersAdapter.notifyDataSetChanged();
            }
        });
    }


}