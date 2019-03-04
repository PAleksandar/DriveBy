package com.example.nenad.projekat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.widget.Toolbar;

public class PrikazKorisnikaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUsersReference;
    private DatabaseReference allDatabaseCustomersReference;
    private ArrayList<Customer> lista;
    private ArrayList<String> idLista;
   // private TrenutnoZahtevanaVoznja zahtevanaVoznja;

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
       // lista = (ArrayList<Customer>) getIntent().getSerializableExtra("lista");
        lista=new ArrayList<Customer>();
        idLista=new ArrayList<String>();

        allDatabaseUsersReference= FirebaseDatabase.getInstance().getReference().child("Message").child((FirebaseAuth.getInstance().getCurrentUser().getUid()));
        allDatabaseUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                   //idLista.add(d.getValue().toString());
                    allDatabaseCustomersReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(d.getKey());
                    allDatabaseCustomersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                             lista.add(dataSnapshot.getValue(Customer.class));
                            allUsersList.setAdapter(new CustomAdapterCustomer(PrikazKorisnikaActivity.this,lista));
                            Toast.makeText(PrikazKorisnikaActivity.this, lista.size()+"",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // zahtevanaVoznja= (TrenutnoZahtevanaVoznja) getIntent().getSerializableExtra("zahtevanaVoznja");

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
    public class CustomAdapterCustomer extends RecyclerView.Adapter<AllUsersViewHolder>{

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<Customer> lista;

        public CustomAdapterCustomer(Context context,ArrayList<Customer> lista) {
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
            //String poz=lista.get(.get)
            holder.setUser_status("");
            holder.setUser_image(this.context,lista.get(position).getUser_image());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visit_user_id=lista.get(position).getId();
//
                      Intent profileIntent=new Intent(PrikazKorisnikaActivity.this, ChatActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        //profileIntent.putExtra("zahtevanaVoznja",zahtevanaVoznja);
                        profileIntent.putExtra("user_name","test");
                        profileIntent.putExtra("type","drivers");
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