package com.blogspot.atifsoftwares.firebaseapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.firebaseapp.ChatActivity;
import com.blogspot.atifsoftwares.firebaseapp.R;
import com.blogspot.atifsoftwares.firebaseapp.ThereProfileActivity;
import com.blogspot.atifsoftwares.firebaseapp.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUser> userList;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String hisUID = userList.get(i).getUid();
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        final String userEmail = userList.get(i).getEmail();

        //set data
        myHolder.mNameTv.setText("익명");//userName
        myHolder.mEmailTv.setText("익명");//userEmail
//        try {
//            Picasso.get().load(userImage)
//                    .placeholder(R.drawable.ic_default_img)
//                    .into(myHolder.mAvatarIv);
//        }
//        catch (Exception e){
//
//        }

        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        if (which==0){
//                            //profile clicked
//                            /*click to go to ThereProfileActivity with uid, this uid is of clicked user
//                             * which will be used to show user specific data/posts*/
//                            Intent intent = new Intent(context, ThereProfileActivity.class);
//                            intent.putExtra("uid",hisUID);
//                            context.startActivity(intent);
//                        }
                        if (which==0){
                            //chat clicked
                            /*Click user from user list to start chatting/messaging
                             * Start activity by putting UID of receiver
                             * we will use that UID to identify the user we are gonna chat*/
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid", hisUID);
                            intent.putExtra("sw","ok");
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
        }
    }
}
