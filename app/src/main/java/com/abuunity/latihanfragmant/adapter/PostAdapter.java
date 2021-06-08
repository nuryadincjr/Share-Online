package com.abuunity.latihanfragmant.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.latihanfragmant.Interface.ItemClickListener;
import com.abuunity.latihanfragmant.R;
import com.abuunity.latihanfragmant.activity.CommentActivity;
import com.abuunity.latihanfragmant.activity.DetailPostActivity;
import com.abuunity.latihanfragmant.api.CommentsRepository;
import com.abuunity.latihanfragmant.pojo.Comments;
import com.abuunity.latihanfragmant.pojo.Posts;
import com.abuunity.latihanfragmant.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Posts> postsList;
    private FirebaseUser firebaseUser;
    private ItemClickListener itemClickListener;
    private FirebaseFirestore firebaseFirestore;

    public PostAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_littel,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Posts posts = postsList.get(position);
        holder.setListeners();
        Picasso.get().load(posts.getImageurl()).into(holder.imagePost);
        holder.title.setText(posts.getTitle());
        holder.description.setText(posts.getDescription());

        isLiked(posts.getPostid(), holder.imageLike, holder.likeCount);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .whereEqualTo("id", posts.getPublisher()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Users users = document.toObject(Users.class);

                        if(users.getImageUrl().equals("default")) {
                            holder.imageProfile.setImageResource(R.drawable.ic_person);
                        }else {
                            Picasso.get().load(users.getImageUrl()).into(holder.imageProfile);
                        }
                        holder.username.setText(users.getUsername());
                    }
                }
            }});

        new CommentsRepository().getAllComment(posts.getPostid()).observe((LifecycleOwner) context, new Observer<ArrayList<Comments>>() {
            @Override
            public void onChanged(ArrayList<Comments> comments) {
                holder.commentCount.setText(String.valueOf(comments.size()));
            }
        });

        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", posts.getPostid());
                intent.putExtra("publisher", posts.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posts postss = new Posts(posts.getPostid(), posts.getTitle(), posts.getDescription(),
                        posts.getImageurl(), posts.getPublisher(), posts.getToolsList(),posts.getStepsList());
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.putExtra("posts", postss);
                context.startActivity(intent);
            }
        });

        holder.layDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posts postss = new Posts(posts.getPostid(), posts.getTitle(), posts.getDescription(),
                        posts.getImageurl(), posts.getPublisher(), posts.getToolsList(),posts.getStepsList());
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.putExtra("posts", postss);
                context.startActivity(intent);
            }
        });

        holder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likes(posts.getPostid(), FieldValue.arrayUnion(firebaseUser.getUid()));
                holder.imageLike.setImageResource(R.drawable.ic_love_fill);
                holder.imageLike.setTag("liked");
//
//                 if(holder.imageLike.getTag().equals("liked")) {
//                    likes(posts.getPostid(), FieldValue.arrayRemove(firebaseUser.getUid()));
//                    holder.imageLike.setImageResource(R.drawable.ic_love);
//                    holder.imageLike.setTag("like");
//                }else {
//                    likes(posts.getPostid(), FieldValue.arrayUnion(firebaseUser.getUid()));
//                    holder.imageLike.setImageResource(R.drawable.ic_love_fill);
//                    holder.imageLike.setTag("liked");
//                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postsList !=null ? postsList.size():0;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageProfile;
        public ImageView imagePost;
        public ImageView imageLike;
        public ImageView imageComment;
        public ImageView imageSave;
        public ImageView imageMore;


        public TextView likeCount;
        public TextView username;
        public TextView commentCount;
        public MaterialTextView title;
        public SocialTextView description;
        public RelativeLayout layDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            imagePost = itemView.findViewById(R.id.image_post);
            imageLike = itemView.findViewById(R.id.like_post);
            imageComment = itemView.findViewById(R.id.comment_post);
            imageSave = itemView.findViewById(R.id.save_post);
            imageMore = itemView.findViewById(R.id.image_more);

            username = itemView.findViewById(R.id.username);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            title = itemView.findViewById(R.id.title_post);
            description = itemView.findViewById(R.id.description_post);
            layDescription = itemView.findViewById(R.id.lay_description);
        }

        public void setListeners() {
            imageMore.setOnClickListener(ViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if(itemClickListener!=null){
                itemClickListener.onClick(v,getAdapterPosition());
            }
        }
    }

    public void likes(String postid, Object optionuid) {
        DocumentReference references = firebaseFirestore.collection("likes").document(postid);
        references.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    references.update("Likes", optionuid);

                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Likes", optionuid);
                    references.set(map);
                }
            }
        });
    }

    private void isLiked(final String postid, final ImageView imageLike, TextView likeCount) {

        DocumentReference references = firebaseFirestore.collection("likes").document(postid);
        references.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> i = task.getResult().getData();
                    DocumentSnapshot documents = task.getResult();

                    if (i != null) {
                        likeCount.setText(String.valueOf(i.size()));
                        imageLike.setImageResource(R.drawable.ic_love_fill);
                        imageLike.setTag("liked");
                    } else {
//                        likeCount.setText(String.valueOf(0));
//                        imageLike.setImageResource(R.drawable.ic_love);
//                        imageLike.setTag("like");
                    }
                }
            }
        });
    }
}
