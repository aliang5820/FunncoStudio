package com.funnco.funnco.wukong.user;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.wukong.im.User;
import com.funnco.funnco.R;
import com.funnco.funnco.wukong.base.BaseFragmentActivity;

/**
 * Created by shuangxi on 14/12/27.
 */
@TargetApi(11)
public class EditProfileActivity extends BaseFragmentActivity {

    private User mOwnProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar(getString(R.string.setting));
        setContentView(R.layout.activity_user_profile);

        mOwnProfile = (User)getIntent().getSerializableExtra("user_profile");

        Fragment mUserFragment = EditProfileFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_profile", mOwnProfile);
        mUserFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.user_profile_parent, mUserFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
