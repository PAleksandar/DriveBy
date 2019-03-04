package com.example.nenad.projekat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.widget.Toolbar;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrikazVozacaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUsersReference;
    private ArrayList<Driver> lista;
    private TrenutnoZahtevanaVoznja zahtevanaVoznja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_prikaz_vozaca);

//        mToolbar= (Toolbar) findViewById(R.id.all_users_app_bar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("All Users");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList=(RecyclerView) findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        lista = (ArrayList<Driver>) getIntent().getSerializableExtra("lista");
        Set<Driver> set = new HashSet<Driver>();
        set.addAll(lista);
        lista.clear();
        lista.addAll(set);

        zahtevanaVoznja= (TrenutnoZahtevanaVoznja) getIntent().getSerializableExtra("zahtevanaVoznja");


        //allDatabaseUsersReference= FirebaseDatabase.getInstance().getReference().child("Users").chi;d("Drivers").child(Idvozaca);
    }


    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerAdapter<Driver,AllUsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Driver, AllUsersViewHolder>
//                (Driver.class,R.layout.all_users_display_layout,AllUsersViewHolder.class,allDatabaseUsersReference) {
//            @Override
//            protected void populateViewHolder(AllUsersViewHolder viewHolder, Driver model, final int position) {
//
//                viewHolder.setUser_name(model.getEmail());
//                viewHolder.setUser_status(model.getId());
//                //viewHolder.setUser_image(getApplicationContext(),model.getUser_image());
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        String visit_user_id=getRef(position).getKey();
//
//                        Intent profileIntent=new Intent(PrikazVozacaActivity.this, ProfileActivity.class);
//                        profileIntent.putExtra("visit_user_id",visit_user_id);
//                        startActivity(profileIntent);
//                    }
//                });
//            }
//
//
//        };


        allUsersList.setAdapter(new CustomAdapter(PrikazVozacaActivity.this,lista));
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
    public class CustomAdapter extends RecyclerView.Adapter<AllUsersViewHolder>{

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<Driver> lista;

        public CustomAdapter(Context context,ArrayList<Driver> lista) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.lista =lista;
        }

        @Override
        public AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.all_users_display_layout, parent, false);
            AllUsersViewHolder holder = new AllUsersViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(AllUsersViewHolder holder, final int position) {
            holder.setUser_name(lista.get(position).getName());
            holder.setUser_image(getApplicationContext(),lista.get(position).getUser_image());
            int poz=lista.get(position).getPozitivneOcene();
            int neg=lista.get(position).getNegativneOcene();

            holder.setUser_status("rating: (+) "+poz+"/ (-) "+neg);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visit_user_id=lista.get(position).getId();
//
                      Intent profileIntent=new Intent(PrikazVozacaActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        profileIntent.putExtra("zahtevanaVoznja",zahtevanaVoznja);
                        startActivity(profileIntent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }


    }
}