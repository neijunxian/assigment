package com.example.assigment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.assigment.Modal.chat;
import com.example.assigment.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Fragment_message123 extends Fragment {

    ImageButton btn_send,btn_bck;
    ImageView profile_image;
    EditText type_msg;
    TextView username;


    DatabaseReference reference;
    FirebaseUser firebaseUser;
    Intent intent;

    MessageAdapter messageAdapter;
    List<chat> mchat;

    RecyclerView recyclerView;

    private long contentView;
    private String userid = intent.getStringExtra("id");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_message, container, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        username = view.findViewById(R.id.username);
        type_msg = view.findViewById(R.id.type_msg);
        btn_send = view.findViewById(R.id.btn_send);

        recyclerView = view.findViewById(R.id.recycler_watch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(userid);

        /*
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userid= (user.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg= type_msg.getText().toString();
                if (!msg.equals("")){
                    sendMsg(firebaseUser.getUid(), userid ,msg);
                }else {
                    Toast.makeText(getActivity(),"You cant send empty message",Toast.LENGTH_SHORT).show();
                }
                type_msg.setText("");
            }
        });

        return view;
    }

    private void sendMsg (String sender,String receiver,String message){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    public void setContentView(int contentView) {
        this.contentView = contentView;
    }

    public int getContentView() {
        int contentView = 0;
        return contentView;
    }

    private void readMessagges(final String myid, final String userid, final String imageurl){
        mchat =new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chat chat = snapshot.getValue(chat.class);
                    if ((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(getActivity(), mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}