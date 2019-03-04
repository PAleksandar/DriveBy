package com.example.nenad.projekat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;

    private Toolbar chatToolbar;
    private TextView userNameTitle;
    //private TextView userLastSeen;
    private CircleImageView userChatProfileImage;



    private ImageButton sendMessageButton;
    private EditText inputMessageText;

    private DatabaseReference rootRef;
    private DatabaseReference pomRef;

    private FirebaseAuth mAuth;
    private String messageSenderId;

    private RecyclerView userMessagesList;

    private  final List<Messages> messageList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_chat);

        rootRef= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        messageSenderId=mAuth.getCurrentUser().getUid();

        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("user_name").toString();

        //Toast.makeText(ChatActivity.this,messageReceiverId,Toast.LENGTH_LONG).show();
        // Toast.makeText(ChatActivity.this,messageReceiverName,Toast.LENGTH_LONG).show();


//        chatToolbar=(Toolbar)findViewById(R.id.chat_bar_layout);
//        setSupportActionBar(chatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        userNameTitle=(TextView)findViewById(R.id.custom_profile_name);
        userChatProfileImage=(CircleImageView) findViewById(R.id.custom_profile_image);



        sendMessageButton=(ImageButton)findViewById(R.id.send_message);
        inputMessageText=(EditText)findViewById(R.id.input_message);

        type=getIntent().getExtras().get("type").toString();
        messageAdapter=new MessageAdapter(messageList,type);


        userMessagesList=(RecyclerView)findViewById(R.id.mesages_list_of_users);

        userNameTitle.setText(messageReceiverName);

        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(new LinearLayoutManager(this));
        userMessagesList.setAdapter(messageAdapter);

        FetchMessages();
        userNameTitle.setText(messageReceiverName);

        if(type.equals("drivers"))
        {
            pomRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(messageReceiverId);
            Toast.makeText(ChatActivity.this,"type: "+type,Toast.LENGTH_LONG).show();
            Toast.makeText(ChatActivity.this,"receiver: "+messageReceiverId,Toast.LENGTH_LONG).show();
            Toast.makeText(ChatActivity.this,"sender: "+messageSenderId,Toast.LENGTH_LONG).show();
        }
        else
        {
            pomRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(messageReceiverId);
            Toast.makeText(ChatActivity.this,"type: "+type,Toast.LENGTH_LONG).show();
            Toast.makeText(ChatActivity.this,"receiver: "+messageReceiverId,Toast.LENGTH_LONG).show();
            Toast.makeText(ChatActivity.this,"sender: "+messageSenderId,Toast.LENGTH_LONG).show();
        }

        pomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String userImage=dataSnapshot.child("user_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile)
                        .into(userChatProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(ChatActivity.this).load(userImage).placeholder(R.drawable.default_profile).into(userChatProfileImage);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SendMessage();
            }
        });
    }

    private void FetchMessages()
    {
        rootRef.child("Message").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messageList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage()
    {
        String messageText=inputMessageText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {

            Toast.makeText(ChatActivity.this,"Please write message",Toast.LENGTH_LONG).show();
        }
        else
        {
            String message_sender_ref="Message/"+messageSenderId+"/"+messageReceiverId;
            String message_receiver_ref="Message/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference user_message_key=rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();
            String message_push_id=user_message_key.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_ref+"/"+message_push_id, messageTextBody);


            rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null)
                    {

                        Log.d("Chat_log",databaseError.getMessage().toString());
                    }

                    inputMessageText.setText("");
                }
            });
        }
    }
}
