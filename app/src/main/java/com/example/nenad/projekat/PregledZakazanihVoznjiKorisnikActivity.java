package com.example.nenad.projekat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PregledZakazanihVoznjiKorisnikActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUsersReference;
    private ArrayList<ZakazanaVoznja> lista = new ArrayList<ZakazanaVoznja>();
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

        allDatabaseUsersReference= FirebaseDatabase.getInstance().getReference().child("ZakazaneVoznje");

        allDatabaseUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lista.clear();
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    for (DataSnapshot s : d.getChildren())
                    {
                        lista.add(s.getValue(ZakazanaVoznja.class));
                        Toast.makeText(PregledZakazanihVoznjiKorisnikActivity.this,"Nova voznja",Toast.LENGTH_LONG).show();
                        allUsersList.setAdapter(new CustomAdapter(PregledZakazanihVoznjiKorisnikActivity.this,lista));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerAdapter<ZakazanaVoznja,AllUsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ZakazanaVoznja, AllUsersViewHolder>
//                (ZakazanaVoznja.class,R.layout.all_users_display_layout,AllUsersViewHolder.class,allDatabaseUsersReference) {
//            @Override
//            protected void populateViewHolder(AllUsersViewHolder viewHolder, ZakazanaVoznja model, final int position) {
//
//                viewHolder.setUser_name(model.getPocetak() + " - "+ model.getKraj());
//                viewHolder.setUser_status(model.getDay()+ ". " + model.getMonth()+ ". "+ model.getYear()+".");
//                //viewHolder.setUser_image(getApplicationContext(),model.getUser_image());
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        final String visit_user_id=getRef(position).getKey();
//                        // Toast.makeText(PregledZakazanihVoznjiVozacActivity.this,visit_user_id,Toast.LENGTH_LONG).show();
//
//
////                        Intent profileIntent=new Intent(AllUsersActivity.this, ProfileActivity.class);
////                        profileIntent.putExtra("visit_user_id",visit_user_id);
////                        startActivity(profileIntent);
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
    public class CustomAdapter extends RecyclerView.Adapter<PrikazVozacaActivity.AllUsersViewHolder>{

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<ZakazanaVoznja> lista;

        public CustomAdapter(Context context,ArrayList<ZakazanaVoznja> lista) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.lista =lista;
        }

        @Override
        public PrikazVozacaActivity.AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.all_users_display_layout, parent, false);
            PrikazVozacaActivity.AllUsersViewHolder holder = new PrikazVozacaActivity.AllUsersViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(PrikazVozacaActivity.AllUsersViewHolder holder, final int position) {
            int poz=lista.get(position).getDriver().getPozitivneOcene();
            int neg=lista.get(position).getDriver().getNegativneOcene();

           // holder.setUser_status("rating: (+) "+poz+"/ (-) "+neg);
            holder.setUser_name(lista.get(position).getDriver().getName()+"  rating: (+) "+poz+"/ (-) "+neg);
            holder.setUser_image(getApplicationContext(),lista.get(position).getDriver().getUser_image() );
            ZakazanaVoznja voznja = lista.get(position);
            holder.setUser_status(voznja.getPocetak()+ " - "+ voznja.getKraj()+"  "+ voznja.getDay() + "."+voznja.getMonth()+"."+voznja.getYear());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String visit_user_id=lista.get(position).getId();
////
//                    Intent profileIntent=new Intent(PrikazVozacaActivity.this, ProfileActivity.class);
//                    profileIntent.putExtra("visit_user_id",visit_user_id);
//                    profileIntent.putExtra("zahtevanaVoznja",zahtevanaVoznja);
//                    startActivity(profileIntent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }


    }
}
