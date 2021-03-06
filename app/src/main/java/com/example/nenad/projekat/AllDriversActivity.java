package com.example.nenad.projekat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.widget.Toolbar;

public class AllDriversActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_prikaz_vozaca);

        //mToolbar= (Toolbar) findViewById(R.id.all_users_app_bar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("All Users");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList=(RecyclerView) findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUsersReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }


    @Override
    protected void onStart() {
        super.onStart();




        FirebaseRecyclerAdapter<Driver,AllUsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Driver, AllUsersViewHolder>
                (Driver.class, R.layout.all_users_display_layout,AllUsersViewHolder.class,allDatabaseUsersReference) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, Driver model, final int position) {

                viewHolder.setUser_name(model.getName());
                int poz=model.getPozitivneOcene();
                int neg=model.getNegativneOcene();

                viewHolder.setUser_status("rating: (+) "+poz+"/ (-) "+neg);
                viewHolder.setUser_image(getApplicationContext(),model.getUser_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String visit_user_id=getRef(position).getKey();

                        Intent profileIntent=new Intent(AllDriversActivity.this, DriverProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }


        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView= itemView;
        }

        public void setUser_name(String user_name)
        {
            TextView name=(TextView) mView.findViewById(R.id.all_users_username);
            name.setText(user_name);


        }

        public void setUser_status(String user_status)
        {
            TextView status=(TextView) mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }

        public void setUser_image(Context ctx, String user_image)
        {
            CircleImageView image=(CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(user_image).placeholder(R.drawable.default_profile).into(image);
        }
    }

    
}
