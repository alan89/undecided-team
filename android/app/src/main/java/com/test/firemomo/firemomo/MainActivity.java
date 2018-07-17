package com.test.firemomo.firemomo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.firemomo.firemomo.MomoGen.MomoCam;
import com.test.firemomo.firemomo.adapter.MomoFeedAdapter;
import com.test.firemomo.firemomo.models.Momo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button goFast;
    private Intent goCam;
    private RecyclerView lstMomo;
    ArrayList<Momo> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        lstMomo = (RecyclerView) findViewById(R.id.lstMomo);
        goFast=(Button)findViewById(R.id.go_fast);
        goFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCam=new Intent(MainActivity.this, MomoCam.class);
                startActivity(goCam);
            }
        });



        ///
        db.collection("posts")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Demo", "Listen failed.", e);
                            return;
                        }


                        for (QueryDocumentSnapshot doc : value) {
                            Momo item = new Momo();
                            if (doc.get("title") != null) {
                                item.setImageURL(doc.getString("imageUrl"));
                                item.setTimeStamp(doc.get("createdAt").toString());
                                item.setLikes(doc.get("likesCount").toString());
                                item.setTitle(doc.getString("title"));
                                item.setMomoId(doc.getString("id"));
                                item.setCommentCount(doc.get("commentCount").toString());
                                item.setUsrName(doc.getString("userName"));


                            }
                            items.add(item);
                        }
                        lstMomo.setAdapter(new MomoFeedAdapter(items));
                     }
                });


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lstMomo.setLayoutManager(llm);


    }
}
