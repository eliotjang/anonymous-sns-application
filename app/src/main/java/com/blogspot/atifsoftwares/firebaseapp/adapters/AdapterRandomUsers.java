package com.blogspot.atifsoftwares.firebaseapp.adapters;

import android.content.Context;
import android.content.Intent;

import com.blogspot.atifsoftwares.firebaseapp.ChatActivity;
import com.blogspot.atifsoftwares.firebaseapp.models.ModelUser;

import java.util.Random;
import java.util.List;
//김건호: 랜덤채팅 클래스
public class AdapterRandomUsers  {

    Context context;
    List<ModelUser> userList;

    //constructor
    public AdapterRandomUsers(Context context, List<ModelUser> userList) {
        Random rand = new Random();
        String ok="ok";
        this.context = context;
        this.userList = userList;
        int iValue = rand.nextInt(userList.size());
        final String hisUID = userList.get(iValue).getUid();
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("hisUid", hisUID);
        intent.putExtra("sw",ok);
        context.startActivity(intent);
    }
}
