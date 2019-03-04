package com.example.nenad.projekat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aca on 4/15/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseReference;
    private String type;
    private String pomFromUserId;
    private String imageCustomer;
    private String imageDriver;

    public MessageAdapter(List<Messages> userMessagesList, String type) {
        this.userMessagesList = userMessagesList;
        this.type=type;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View V= LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_user,parent,false);

        mAuth= FirebaseAuth.getInstance();

        return  new MessageViewHolder(V);


    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        String message_sender_id=mAuth.getCurrentUser().getUid();

        Messages messages=userMessagesList.get(position);

        final String fromUserId=messages.getFrom();


        if(message_sender_id.equals(fromUserId))
        {
            if(this.type.equals("customers"))
            {
                UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(fromUserId);
            }
            else
            {
                UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(fromUserId);
            }
        }
        else
        {
            if(this.type.equals("customers"))
            {
                UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(fromUserId);
            }
            else
            {
                UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(fromUserId);
            }
        }

        /*if(this.type.equals("drivers"))
        {
            UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(fromUserId);
            //Toast.makeText(th,"type: "+type,Toast.LENGTH_LONG).show();
            //Toast.makeText(ChatActivity.this,"id: "+fromUserId,Toast.LENGTH_LONG).show();
            Log.i("type", type);
            Log.i("id",fromUserId);
            //type="izmena";
            //Toast.makeText(ChatActivity.this,"sender: "+messageSenderId,Toast.LENGTH_LONG).show();
        }
        else
        {
            UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(fromUserId);
            Log.i("obavestenje","izvrsava se else");
            Log.i("type", type);
            Log.i("id",fromUserId);
            //UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(fromUserId);
        }*/

        // UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(fromUserId);

        UsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("user_image").getValue()==null)
                {
                    /*if(type.equals("drivers"))
                    {
                        if(imageCustomer!=null)
                        {
                            Picasso.with(holder.userProfileImage.getContext()).load(imageCustomer).placeholder(R.drawable.default_profile).into(holder.userProfileImage);
                        }
                        else

                            type="customers";


                    }
                    else
                    {
                        if(imageDriver!=null)
                        {
                            Picasso.with(holder.userProfileImage.getContext()).load(imageDriver).placeholder(R.drawable.default_profile).into(holder.userProfileImage);

                        }
                        else  type="drivers";
                    }*/




                }
                else
                {
                    String userImage = dataSnapshot.child("user_image").getValue().toString();
                    /*if(type.equals("drivers"))
                    {
                        if(imageCustomer==null)
                            imageCustomer=userImage;
                    }
                    else
                    {
                        if(imageDriver==null)
                            imageDriver=userImage;
                    }*/

                    Log.i("Dodata slika za", fromUserId);
                    Picasso.with(holder.userProfileImage.getContext()).load(userImage).placeholder(R.drawable.default_profile).into(holder.userProfileImage);

                }

                //onBindViewHolder();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(fromUserId.equals(message_sender_id))
        {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.RIGHT);


            //CircleImageView image=(CircleImageView) holder.itemView.findViewById(R.id.all_users_profile_image);
            //Picasso.with(holder.userProfileImage.getContext()).load(user_image).placeholder(R.drawable.default_profile).into(image);


        }
        else
        {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setGravity(Gravity.LEFT);
        }

        holder.messageText.setText(messages.getMessage());


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public CircleImageView userProfileImage;
        // public ImageView messagePicture;

        public  MessageViewHolder(View view)
        {
            super(view);
            messageText=(TextView) view.findViewById(R.id.message_text);
            userProfileImage=(CircleImageView) view.findViewById(R.id.message_profile_image);
            // messagePicture=(ImageView) view.findViewById(R.id.messa);
        }
    }
}
