package com.avg.kasun.reach;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private RecyclerView mReqList;

    private DatabaseReference mReqDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mReqList = mMainView.findViewById(R.id.req_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);

//        mReqDatabase.keepSynced(true);


        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(

                Requests.class,
                R.layout.users_single_layout,
                RequestViewHolder.class,
                mReqDatabase


        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder requestViewHolder, final Requests requests, int i) {

                requestViewHolder.setDate(requests.getDate());
                final String list_user_id = getRef(i).getKey();


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
//                        mReqDatabase.child(mCurrent_user_id).child(list_user_id).
//                        String date=dataSnapshot.child("date").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                        requestViewHolder.setName(userName);
                        requestViewHolder.setUserImage(userThumb);
//                        if (dataSnapshot.hasChild("online")) {
//                            String userOnline = dataSnapshot.child("online").getValue().toString();
//                            requestViewHolder.setUserOnline(userOnline);
//                        }
//                        requestViewHolder.setDate(date);

                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), ProfileActivity.class);
                                intent.putExtra("user_id", list_user_id);
                                startActivity(intent);
//                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                builder.setTitle("Select Options");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        if (which == 0) {
//                                            Intent intent = new Intent(getContext(), ProfileActivity.class);
//                                            intent.putExtra("user_id", list_user_id);
//                                            startActivity(intent);
//                                        } else if (which == 1) {
//                                            Intent chatintent = new Intent(getContext(), ChatActivity.class);
//                                            chatintent.putExtra("user_id", list_user_id);
//                                            chatintent.putExtra("user_name", userName);
//                                            startActivity(chatintent);
//                                        }
//                                    }
//                                });
//                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

//        friendsRecyclerViewAdapter.getRef(4).removeValue();
        mReqList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String userName) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(userName);
        }

        public void setUserImage(String thumb_image) {
            CircleImageView userthumbView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.avatar_default).into(userthumbView);
        }

//        public void setUserOnline(String online_status) {
//            ImageView userOnlineView = mView.findViewById(R.id.user_single_online_icon);
//            if (online_status == "true") {
//                userOnlineView.setVisibility(View.VISIBLE);
//
//            } else {
//                userOnlineView.setVisibility(View.INVISIBLE);
//            }
//        }
    }
}
