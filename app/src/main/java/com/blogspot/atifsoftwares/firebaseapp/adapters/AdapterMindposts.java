package com.blogspot.atifsoftwares.firebaseapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.firebaseapp.AddPostActivity;
import com.blogspot.atifsoftwares.firebaseapp.PostDetailActivity;
import com.blogspot.atifsoftwares.firebaseapp.R;
import com.blogspot.atifsoftwares.firebaseapp.ThereProfileActivity;
import com.blogspot.atifsoftwares.firebaseapp.models.ModelMindpost;
import com.blogspot.atifsoftwares.firebaseapp.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*생성자 -->이소연*/
public class AdapterMindposts extends RecyclerView.Adapter<AdapterMindposts.MyHolder> {


    Context context;
    List<ModelMindpost> MindpostList;

    String myUid;

    private DatabaseReference likesRef; //for likes database node
    private DatabaseReference postsRef; //reference of posts

    boolean mProcessLike=false;

    public AdapterMindposts(Context context, List<ModelMindpost> MindpostList) {
        this.context = context;
        this.MindpostList = MindpostList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("MindLikes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("MindPosts");
    }

    @NonNull
    @Override
    public AdapterMindposts.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_mindpost, viewGroup, false);

        return new AdapterMindposts.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMindposts.MyHolder myHolder, final int i) {
        //get data
        final String uid = MindpostList.get(i).getuUid();
        String uEmail = MindpostList.get(i).getuEmail();
        String uName = MindpostList.get(i).getuName();
        String uDp = MindpostList.get(i).getuDp();
        final String pId = MindpostList.get(i).getmId();
        final String pDescription = MindpostList.get(i).getmDescr();
        String pLikes = MindpostList.get(i).getmLikes();

        //set data
        //myHolder.uNameTv.setText(uName);
        myHolder.pDescriptionTv.setText(pDescription);
        //myHolder.pLikesTv.setText(pLikes +" Likes"); //e.g. 100 Likes
        //set likes for each post
        setLikes(myHolder, pId);

        //set user dp
        /*try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(myHolder.uPictureIv);
        }
        catch (Exception e){

        }*/

        //handle button clicks,
//        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showMoreOptions(myHolder.moreBtn, uid, myUid, pId);
//            }
//        });

        //마인드 포스트 공감하기 공감수 :이소연
        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get total number of likes for the post, whose like button clicked
                //if currently signed in user has not liked it before
                //increase value by 1, otherwise decrease value by 1
                final int pLikes = Integer.parseInt(MindpostList.get(i).getmLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String postIde = MindpostList.get(i).getmId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postIde).hasChild(myUid)){
                                //already liked, so remove like
                                postsRef.child(postIde).child("pMindLikes").setValue(""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else {
                                // not liked, like it
                                postsRef.child(postIde).child("pMindLikes").setValue(""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("MindLiked"); //set any value
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to ThereProfileActivity with uid, this uid is of clicked user
                 * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });


    }
   /* private void shareTextOnly(String pTitle, String pDescription) {
        //concatenate title and description to share
        String shareBody = pTitle +"\n"+ pDescription;

        //share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        //sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here"); //in case you share via an email app
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody); //text to share
        context.startActivity(Intent.createChooser(sIntent, "Share Via")); //message to show in share dialog

    }*/

     //공감 버튼 리스너 : 이소연-->이미지 바꿔줌
    private void setLikes(final AdapterMindposts.MyHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    //user has liked this post
                    /*To indicate that the post is liked by this(SignedIn) user
                    Change drawable left icon of like button
                    Change text of like button from "Like" to "Liked"*/
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love, 0,0,0);
                    holder.likeBtn.setText("공감");
                }
                else {
                    //user has not liked this post
                    /*To indicate that the post is not liked by this(SignedIn) user
                    Change drawable left icon of like button
                    Change text of like button from "Liked" to "Like"*/
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unlove, 0,0,0);
                    holder.likeBtn.setText("비공감");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId) {
        //creating popup menu currently having option Delete, we will add more options later
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if (uid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
           /// popupMenu.getMenu().add(Menu.NONE, 1, 0, "View Detail");
        }
       // popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");


        //item click listener  //버튼 리스너 : 이소연
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id==0){   //삭제하기 버튼
                    //delete is clicked
                    beginDelete(pId);
                }

                /*else if (id==1){    //올린사람 피드보기
                    //start PostDetailActivity
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId); //will get detail of post using this id, its id of the post clicked
                    context.startActivity(intent);
                }
                else if (id==2){

                }*/
                return false;
            }
        });
        //show menu
        popupMenu.show();

    }

     //마인드 포스트 삭제하기
    private void beginDelete(String pId) {
            deleteMindPost(pId);

    }


    //마인드 포스트 삭제 : 이소연
    private void  deleteMindPost(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("MindPosts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); // remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return MindpostList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{  //구성요소 초기화

        //views from row_post.xml
        TextView uNameTv, pDescriptionTv, pLikesTv, pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views :이소연
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }

}
