package com.test.firemomo.firemomo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.firemomo.firemomo.MomoGen.MomoCam;
import com.test.firemomo.firemomo.adapter.MomoFeedAdapter;
import com.test.firemomo.firemomo.models.Momo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LIMIT = 50;

    private FloatingActionButton goFast;
    private Intent goCam;
    private RecyclerView lstMomo;
    ArrayList<Momo> items = new ArrayList<>(LIMIT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        lstMomo = findViewById(R.id.lstMomo);
        goFast = findViewById(R.id.go_fast);

        goFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCam = new Intent(MainActivity.this, MomoCam.class);
                startActivity(goCam);
            }
        });

        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(LIMIT)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Demo", "Listen failed.", e);
                            return;
                        }

                        List<DocumentChange> changes = value.getDocumentChanges();

                        for (DocumentChange change : changes) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot snapshot = change.getDocument();
                                Momo item = snapshot.toObject(Momo.class);

                                items.add(change.getNewIndex(), item);
                            }

                            // TODO: Handle REMOVED and MODIFIED and MOVED events
                        }

                        lstMomo.setAdapter(new MomoFeedAdapter(items));
                     }
                });


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        lstMomo.setLayoutManager(llm);


    }
}
