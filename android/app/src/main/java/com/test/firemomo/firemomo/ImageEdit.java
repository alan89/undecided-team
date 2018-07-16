package com.test.firemomo.firemomo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.test.firemomo.firemomo.util.Async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

public class ImageEdit extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "ImageEdit";

  private FrameLayout mContainer;

  private EditText mTopText;
  private EditText mBottomText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_edit);

    mContainer = findViewById(R.id.image_container);
    mTopText = findViewById(R.id.text_top);
    mBottomText = findViewById(R.id.text_bottom);

    findViewById(R.id.button_add_text_top).setOnClickListener(this);
    findViewById(R.id.button_add_text_bottom).setOnClickListener(this);
    findViewById(R.id.button_done).setOnClickListener(this);
  }

  private void addTopText() {
    findViewById(R.id.text_top).setVisibility(View.VISIBLE);
  }

  private void addBottomText() {
    findViewById(R.id.text_bottom).setVisibility(View.VISIBLE);
  }

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
    View view = this.getCurrentFocus();
    if (view == null) {
      view = new View(this);
    }

    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  private void onDoneClicked() {
    // Move focus out of edit texts
    mContainer.requestFocus();
    hideKeyboard();

    // Save the image
    saveView(mContainer)
        .addOnSuccessListener(
            this,
            new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {
                Toast.makeText(ImageEdit.this, "Saved!", Toast.LENGTH_SHORT).show();
              }
            })
        .addOnFailureListener(
            this,
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageEdit.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveView:onFailure", e);
              }
            });
  }

  private Bitmap getViewBitmap(View v) {
    Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    v.draw(c);

    return b;
  }

  private Task<Uri> saveView(final View v) {
    return Tasks.call(
        Async.IO,
        new Callable<Uri>() {
          @Override
          public Uri call() throws Exception {
            Bitmap b = getViewBitmap(v);
            File file = saveImage(b);
            Uri uri = Uri.fromFile(file);

            Log.d(TAG, "Created file at: " + uri);
            return uri;
          }
        });
  }

  @WorkerThread
  private File saveImage(Bitmap bitmap) throws IOException {
    String uuid = UUID.randomUUID().toString();

    File file = File.createTempFile(uuid, ".png");
    FileOutputStream fos = new FileOutputStream(file);

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    fos.close();

    return file;
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_add_text_top:
        addTopText();
        break;
      case R.id.button_add_text_bottom:
        addBottomText();
        break;
      case R.id.button_done:
        onDoneClicked();
        break;
    }
  }
}
