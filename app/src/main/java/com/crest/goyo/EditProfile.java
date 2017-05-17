package com.crest.goyo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.crest.goyo.other.CircleTransform;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import okhttp3.HttpUrl;

/**
 * Created by brittany on 3/23/17.
 */

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_edit, img_profile;
    private EditText et_first_name, et_email, et_mo_number;
    private TextView actionbar_title;
    private Button bt_save;
    private static int RESULT_PROFILE_IMG = 0;
    Uri selectedImage;
    Cursor cursor;
    ProgressDialog dialog;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private String filePathProfile = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);

        initUI();

        if (Constant.isOnline(getApplicationContext())) {
            getUserProfileAPI();
        }
    }

    private void getUserProfileAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_USER_PROFILE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(EditProfile.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
//                        Toast.makeText(EditProfile.this, message, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = response.getJSONObject("data");
                        et_first_name.setText(jsonObject.getString("v_name"));
                        et_email.setText(jsonObject.getString("v_email"));
                        et_mo_number.setText(jsonObject.getString("v_phone"));

                        if (jsonObject.getString("v_image").equals("")) {
                            img_profile.setImageResource(R.drawable.no_user);
                        } else {
                            Glide.with(EditProfile.this).load(jsonObject.getString("v_image"))
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransform(EditProfile.this))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(img_profile);
                        }


                    } else {
//                        Toast.makeText(EditProfile.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void initUI() {
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mo_number = (EditText) findViewById(R.id.et_mo_number);
        bt_save = (Button) findViewById(R.id.bt_save);

        iv_edit.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        img_profile.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_edit_profile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                Intent eIntent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(eIntent);
                break;

            case R.id.bt_save:
                editProfileValidation();
                break;

            case R.id.img_profile:
                pickImageFromGallery();
                break;


        }
    }

    private void pickImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_PROFILE_IMG);

        }

    }

    private void editProfileValidation() {

        if (et_first_name.getText().toString().equals("")) {
            et_first_name.setError("Please enter name.");
        } else {
            if (et_email.getText().toString().matches(Constant.emailPattern)) {
                if (et_mo_number.getText().toString().length() == 10) {
                    new upload_image_asyn().execute();
                } else {
                    et_mo_number.setError("Please enter 10 digit mobile no.");
                }
            } else {
                et_email.setError("Please enter valid email.");
            }
        }

    }

    private class upload_image_asyn extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(EditProfile.this, "", "", true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... url) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        executeMultipartPost();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();
        }
    }

    private void executeMultipartPost() throws Exception {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(Constant.URL_UPDATE_USER_PROFILE);
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("device", new StringBody("ANDROID"));
            reqEntity.addPart("login_id", new StringBody(Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID)));
            reqEntity.addPart("v_token", new StringBody(Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN)));
            reqEntity.addPart("v_name", new StringBody(et_first_name.getText().toString()));
            reqEntity.addPart("v_email", new StringBody(et_email.getText().toString()));
            reqEntity.addPart("v_phone", new StringBody(et_mo_number.getText().toString()));
            if (filePathProfile.equals("")) {
            } else {
                File file = new File(filePathProfile);
                reqEntity.addPart("v_image", new FileBody(file));
            }
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response Register: " + s.toString());
            final JSONObject jsonObject = new JSONObject(s.toString());
            final String success = jsonObject.optString("success").toString();
            String value = String.valueOf(success);
            final String messaage = jsonObject.optString("message").toString();
            if (value == "1") {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), messaage, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(EditProfile.this,MainActivity.class);
                        startActivity(intent);
//                        getUserProfileAPI();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), messaage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            // handle exception here
            Log.d("exception", "     " + e);
            e.printStackTrace();
        }
    }


    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_title_rationale));
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(EditProfile.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_PROFILE_IMG && resultCode == RESULT_OK
                    && null != data) {
                selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePathProfile = cursor.getString(columnIndex);
                cursor.close();
                img_profile.setImageBitmap(BitmapFactory
                        .decodeFile(filePathProfile));
            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
