package com.gank.gankly.ui.main;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gank.gankly.App;
import com.gank.gankly.R;
import com.gank.gankly.ui.base.BaseFragment;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

/**
 * Create by LingYan on 2016-05-10
 */
public class AboutFragment extends BaseFragment {
    @Bind(R.id.about_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.about_collapsing)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindString(R.string.about_gank_url)
    String mGankUrl;
    @BindString(R.string.about_mzitu_url)
    String mMzituUrl;
    @BindString(R.string.about_my_github_url)
    String mGithubUrl;

    public static AboutFragment sAboutFragment;
    public MainActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    public static AboutFragment getInstance() {
        if (sAboutFragment == null) {
            sAboutFragment = new AboutFragment();
        }
        return sAboutFragment;
    }

    @Override
    protected void initValues() {

    }

    @Override
    protected void initViews() {
        mCollapsingToolbarLayout.setTitle(App.getAppString(R.string.navigation_about));
        mActivity.setSupportActionBar(mToolbar);
        ActionBar bar = mActivity.getSupportActionBar();
        if (bar != null) {
            bar.setHomeAsUpIndicator(R.drawable.ic_home_navigation);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.openDrawer();
            }
        });
    }

    @Override
    protected void bindLister() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @OnClick(R.id.about_item_text_gank)
    void clickGank() {
        gotoUrl(mGankUrl);
    }

    @OnClick(R.id.about_item_text_mzitu)
    void clickMzitu() {
        gotoUrl(mMzituUrl);
    }

    @OnClick(R.id.about_item_text_github)
    void clickGithub() {
        gotoUrl(mGithubUrl);
    }

    private void gotoUrl(String url) {
        if (url == null || !url.startsWith("http")) {
            throw new RuntimeException("not complete web site");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
