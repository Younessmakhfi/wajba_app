package com.wajeba.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wajeba.asyncTask.LoadProfile;
import com.wajeba.fooddelivery.ProfileEditActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FragmentProfile extends Fragment {

    private Methods methods;
    private RoundedImageView imageView_profile;
    private TextView textView_name, textView_email, textView_mobile, textView_address;
    private LinearLayout ll_mobile, ll_address;
    private View view_phone, view_address;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        methods = new Methods(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.loading));

        imageView_profile = rootView.findViewById(R.id.iv_profile);
        textView_name = rootView.findViewById(R.id.tv_prof_fname);
        textView_email = rootView.findViewById(R.id.tv_prof_email);
        textView_mobile = rootView.findViewById(R.id.tv_prof_mobile);
        textView_address = rootView.findViewById(R.id.tv_prof_address);

        ll_mobile = rootView.findViewById(R.id.ll_prof_phone);
        ll_address = rootView.findViewById(R.id.ll_prof_address);

        view_phone = rootView.findViewById(R.id.view_prof_phone);
        view_address = rootView.findViewById(R.id.view_prof_address);

        if (Constant.isLogged) {
            loadUserProfile();
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profile_edit:
                if (Constant.isLogged) {
                    Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                    startActivity(intent);
                } else {
                    methods.showToast(getString(R.string.not_log));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        if (methods.isNetworkAvailable()) {
            LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    if(getActivity() != null) {
                        progressDialog.dismiss();
                        if (success.equals("1")) {
                            if (registerSuccess.equals("1")) {
                                setVariables();
                            }
                        } else {
                            methods.showToast(getString(R.string.error_server));
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadProfile.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    private void setVariables() {
        textView_name.setText(Constant.itemUser.getName());
        textView_mobile.setText(Constant.itemUser.getMobile());

        textView_email.setText(Constant.itemUser.getEmail());
        textView_address.setText(Constant.itemUser.getAddress());

        if (!Constant.itemUser.getMobile().trim().isEmpty()) {
            ll_mobile.setVisibility(View.VISIBLE);
            view_phone.setVisibility(View.VISIBLE);
        }

        if (!Constant.itemUser.getAddress().trim().isEmpty()) {
            ll_address.setVisibility(View.VISIBLE);
            view_address.setVisibility(View.VISIBLE);
        }

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
    public void onResume() {
        if (Constant.isUpdate) {
            Constant.isUpdate = false;
            setVariables();
        }
        super.onResume();
    }
}
