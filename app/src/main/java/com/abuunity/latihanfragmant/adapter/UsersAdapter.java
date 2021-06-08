package com.abuunity.latihanfragmant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.latihanfragmant.Interface.ItemClickListener;
import com.abuunity.latihanfragmant.R;
import com.abuunity.latihanfragmant.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private List<Users> usersList;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private ItemClickListener itemClickListener;

    public UsersAdapter(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users,parent,false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Users users = usersList.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(users.getUsername());
        holder.fullName.setText(users.getName());
        firebaseFirestore = FirebaseFirestore.getInstance();

        Picasso.get().load(usersList.get(position).getImageUrl()).placeholder(R.drawable.ic_person).into(holder.imageProfil);

        isFollowed(users.getId(), holder.btnFollow);

        if(users.getId().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.btnFollow.getText().toString().toLowerCase().equals("follow")) {
                    follow(firebaseUser.getUid(), users.getId(), FieldValue.arrayUnion(users.getId()),
                            FieldValue.arrayUnion(firebaseUser.getUid()));
                    holder.btnFollow.setText("following");
                } else {
                    follow(firebaseUser.getUid(), users.getId(), FieldValue.arrayRemove(users.getId()),
                            FieldValue.arrayRemove(firebaseUser.getUid()));
                    holder.btnFollow.setText("follow");
                }
            }
        });
    }

    private void isFollowed(final String id, final Button btnFollow) {

        DocumentReference references = firebaseFirestore.collection("follows").document(firebaseUser.getUid());
        references.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<String> group = (List<String>) document.get("followings");

                for (String list : group) {
                    if(list.equals(id)) {
                        btnFollow.setText("following");
                    } else {
                        btnFollow.setText("follow");
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfil;
        public TextView username;
        public TextView fullName;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfil = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            btnFollow = itemView.findViewById(R.id.btn_follow);

        }
    }


    public void follow(String uid, String ids, Object optionuid, Object optionsids) {
        DocumentReference references = firebaseFirestore.collection("follows").document(uid);
        references.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    references.update("followings", optionuid);

                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("followings", optionuid);
                    references.set(map);
                }
            }
        });

        DocumentReference reference = firebaseFirestore.collection("follows").document(ids);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    reference.update("followers", optionsids);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("followers", optionsids);
                    reference.set(map);
                }
            }
        });
    }
}
