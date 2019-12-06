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
//김건호: 랜덤채팅 클래스
public class AdapterRandomUsers  {

    Context context;
    List<ModelUser> userList;

    //constructor
    public AdapterRandomUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        final String hisUID = userList.get(2).getUid();
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("hisUid", hisUID);
        context.startActivity(intent);
    }
}
