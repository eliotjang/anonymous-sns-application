package com.blogspot.atifsoftwares.firebaseapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AddMindpostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;
    ActionBar actionBar;


    //views
    EditText descriptionEt;
    Button uploadBtn;
    Button anonymouseBtn; // gy :익명 버튼
    Button username;

    //user info
    String name, email, uid, dp;

    //info of post to be edited
    String editDescription;
    static int counter=0;

    //progress bar
    ProgressDialog pd;

    //int flag = 1; // gy : flag가 1이면 실명으로 바꾸고 flag 0이면 익명으로
    String Aname; // gy : 임시로 이름 저장       데이터에 들어가고 출력되는 값이 Aname에 저장. 데이터에서 받아오는 이름은 name변수에

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mindpost);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New MindPost");// gy :작성 화면에 상단 제목
        //enable back button in actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        //init views
        descriptionEt = findViewById(R.id.postDscEt);
        uploadBtn = findViewById(R.id.mUploadBtn);
        anonymouseBtn = findViewById(R.id.mAnonymouseBtn); // gy : 버튼에 대한 아이디 가져옴
        username = findViewById(R.id.mUsernameBtn); // gy : 버튼에 대한 아이디


        //get data through intent from previous activitie's adapter
        Intent intent = getIntent();
        final String isUpdateKey = ""+intent.getStringExtra("key");
        final String editPostId = ""+intent.getStringExtra("editPostId");

        //validate if we came here to update post i.e. came from AdapterPost
        if (isUpdateKey.equals("editPost")){ // gy :수정시
            //update
            actionBar.setTitle("Update Post");
            uploadBtn.setText("Update");
            loadPostData(editPostId);
        }
        else { // 작성시
            //add
            actionBar.setTitle("Add New Post");
            uploadBtn.setText("Upload");
            Aname = name;
        }

        actionBar.setSubtitle(email); // gy : add now post아래에 자신의 이메일

        //get some info of current user to include in post
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ ds.child("name").getValue();
                    email = ""+ ds.child("email").getValue();
                    dp = ""+ ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //upload button click listener
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data(title, description) from EditTexts
                String description = descriptionEt.getText().toString().trim();

               /* if (isUpdateKey.equals("editPost")){
                    beginUpdate(description, editPostId);
                }*/
               // else {
                    uploadData( description);
                //}

                // gy : update버튼 클릭 시 게시판 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);

            }
        });

        // gy : 익명 버튼에 대한 클릭 이벤트 발생
        anonymouseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  if(flag == 1) {// gy : 실명에서 익명으로
                //flag = 0;
                Aname = "anonymouse"; //gy : 이름을 익명으로 만들어줌
                //anonymouseBtn.setText("user name"); // gy : 버튼 내용을 바꿔줌
                Toast.makeText(AddMindpostActivity.this, "name is anonymouse...", Toast.LENGTH_SHORT).show();
                // } 버튼 하나로 하려 했으나 실패....
//                else if(flag == 0) {
//                    flag = 1;
//                    Aname = name; // gy : 원래 이름은 바꿔줌
//                    anonymouseBtn.setText("Anonymouse"); // gy : 버튼을 내용을 anonymouse로 바꿔줌
//
//                }
            }
        });

        //gy : 자신의 이름으로 포스트 하는 버튼 클릭 이벤트 발생
        username.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Aname = name;
                Toast.makeText(AddMindpostActivity.this, "name is yourname...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void beginUpdate(String description, String editPostId) {
        pd.setMessage("Updating Post...");
        pd.show();

            updateWithoutImage(description, editPostId);

    }

    private void updateWithoutImage(String description, String editPostId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        //put post info
        hashMap.put("uid", uid);
        hashMap.put("uName", Aname);
        hashMap.put("uEmail", email);
        hashMap.put("uDp", dp);
        hashMap.put("mDescr", description);
        hashMap.put("mImage", "noImage");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MindPosts");
        ref.child(editPostId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(AddMindpostActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddMindpostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MindPosts");
        //get detail of post using id of post
        Query fquery = reference.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data

                    editDescription = ""+ds.child("pDescr").getValue();

                    //set data to views
                    descriptionEt.setText(editDescription);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadData( final String description) {
        pd.setMessage("Publishing post...");
        pd.show();

        //for post-image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "MindPosts/" + "post_" + timeStamp;

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName", Aname);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("mId", timeStamp);
            hashMap.put("mDescr", description);
            hashMap.put("mLikes", "0");

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MindPosts");
            //put data in this ref
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            pd.dismiss();
                            Toast.makeText(AddMindpostActivity.this, "Post published", Toast.LENGTH_SHORT).show();
                            descriptionEt.setText("");



                            //send notification
                            prepareNotification(
                                    ""+timeStamp,//since we are using timestamp for post id
                                    ""+name+" added new post",
                                    ""+"\n"+description,
                                    "PostNotification",
                                    "POST"
                            );
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            pd.dismiss();
                            Toast.makeText(AddMindpostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }



    private void prepareNotification(String pId, String title, String description, String notificationType, String notificationTopic){
        //prepare data for notification


        String NOTIFICATION_TOPIC = "/topics/" + notificationTopic; //topic must match with what the receiver subscribed to
        String NOTIFICATION_TITLE = title; //e.g. Atif Pervaiz added new post
        String NOTIFICATION_MESSAGE = description; //content of post
        String NOTIFICATION_TYPE = notificationType; //now there are two notification types chat & post, so to differentiate in FirebaseMessaging.java class

        //prepare json what to send, and where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("sender", uid);//uid of current use/sender
            notificationBodyJo.put("pId", pId);//post id
            notificationBodyJo.put("pTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);

            notificationJo.put("data", notificationBodyJo);//combine data to be sent
        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        sendPostNotification(notificationJo);

    }

    private void sendPostNotification(JSONObject notificationJo) {
        //send volley object requrest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FCM_RESPONSE", "onResponse: "+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error occurred
                        Toast.makeText(AddMindpostActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAA7AuJGz8:APA91bGwtgymO7JGkhboQEjJPR7wdIzOYA4ZeCU0th6udSCABz8VnPcfcwlh8R7hSrYBzX1QQcP8To55cwcRSjIj0YttTGVaaXP2e8u18QGbluxclRlIFBwlExiwqk9AkHPt6cLegJkt");//paste your fcm key here after "key="

                return headers;
            }
        };
        //enqueue the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        } else {
            //user not signed in, go to main acitivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //goto previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }


}
