package com.wajeba.fooddelivery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wajeba.asyncTask.LoadProfileUpdate;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;
import com.wajeba.utils.SharedPref;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileEditActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Toolbar toolbar;
    Methods methods;
    private SharedPref sharedPref;
    private EditText editText_name, editText_email, editText_phone, editText_pass, editText_cpass, editText_address;
    private AppCompatButton button_update;
    private RoundedImageView imageView_profile;
    private ImageView imageView_editpropic;
    private String imagePath = "";
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        LinearLayout ll_adView = findViewById(R.id.ll_adView_profedit);
        methods.showBannerAd(ll_adView);

        progressDialog = new ProgressDialog(ProfileEditActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.updating));

        toolbar = findViewById(R.id.toolbar_proedit);
        toolbar.setTitle(getResources().getString(R.string.profile_edit));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = new SharedPref(this);

        button_update = findViewById(R.id.button_prof_update);
        imageView_profile = findViewById(R.id.iv_profile_edit);
        imageView_editpropic = findViewById(R.id.iv_edit_pro_ic);
        editText_name = findViewById(R.id.et_prof_edit_fname);
        editText_email = findViewById(R.id.et_prof_edit_email);
        editText_phone = findViewById(R.id.et_prof_edit_mobile);
        editText_address = findViewById(R.id.et_prof_edit_address);
        editText_pass = findViewById(R.id.et_prof_edit_pass);
        editText_cpass = findViewById(R.id.et_prof_edit_cpass);

        setProfileVar();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (methods.isNetworkAvailable()) {
                        loadUpdateProfile();
                    } else {
                        methods.showToast(getString(R.string.error_net_not_conn));
                    }
                }
            }
        });

        imageView_editpropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUpdateProfile() {
        if (methods.isNetworkAvailable()) {
            File file_image;
            if (imagePath.equals("")) {
                file_image = null;
            } else {
                file_image = new File(imagePath);
            }

            LoadProfileUpdate loadProfileEdit = new LoadProfileUpdate(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        switch (registerSuccess) {
                            case "1":
                                updateArray();
                                Constant.isUpdate = true;
                                finish();
                                methods.showToast(message);
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                break;
                            default:
                                if (message.contains("Email address already used")) {
                                    editText_email.setError(message);
                                    editText_email.requestFocus();
                                } else {
                                    methods.showToast(message);
                                }
                                break;
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_PROFILE_EDIT, 0, "", "", "", "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), "", editText_address.getText().toString(), Constant.itemUser.getId(), "", file_image));
            loadProfileEdit.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    private Boolean validate() {
        editText_name.setError(null);
        editText_email.setError(null);
        editText_cpass.setError(null);
        View focusView;
        if (editText_name.getText().toString().trim().isEmpty()) {
            methods.showToast(getString(R.string.name_empty));
            editText_name.setError(getString(R.string.error_invalid_password));
            focusView = editText_name;
            focusView.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            methods.showToast(getString(R.string.email_empty));
            editText_email.setError(getString(R.string.error_field_required));
            focusView = editText_email;
            focusView.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().trim().equals(editText_cpass.getText().toString().trim())) {
            methods.showToast(getString(R.string.pass_nomatch));
            editText_cpass.setError(getString(R.string.pass_nomatch));
            focusView = editText_cpass;
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateArray() {
        Constant.itemUser.setName(editText_name.getText().toString());
        Constant.itemUser.setEmail(editText_email.getText().toString());
        Constant.itemUser.setMobile(editText_phone.getText().toString());
        Constant.itemUser.setAddress(editText_address.getText().toString());

        sharedPref.setLoginDetails(Constant.itemUser);

        if (!editText_pass.getText().toString().equals("")) {
            sharedPref.setRemeber(false);
        }
    }

    public void setProfileVar() {
        editText_name.setText(Constant.itemUser.getName());
        editText_phone.setText(Constant.itemUser.getMobile());
        editText_email.setText(Constant.itemUser.getEmail());
        editText_address.setText(Constant.itemUser.getAddress());

        try {
            Picasso.get()
                    .load(Constant.itemUser.getImage())
                    .placeholder(R.drawable.placeholder_profile)
                    .into(imageView_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            imagePath = methods.getPathImage(uri);

            try {
                Bitmap bitmap_upload = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView_profile.setImageBitmap(bitmap_upload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}