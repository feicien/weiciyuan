package org.qii.weiciyuan.ui.browser;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.ShortUrlBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.lib.CheatSheet;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.interfaces.AbstractAppActivity;
import org.qii.weiciyuan.ui.main.MainTimeLineActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-2-19
 */
public class BrowserWebActivity extends AbstractAppActivity {

    private Button shareCountBtn;
    private int shareCountInt;
    private String url;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("shareCountInt", shareCountInt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(action)) {
            url = getIntent().getData().toString();
        } else {
            url = getIntent().getStringExtra("url");
        }

        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        View title = getLayoutInflater().inflate(R.layout.browserwebactivity_title_layout, null);
        shareCountBtn = (Button) title.findViewById(R.id.share_count);
        CheatSheet.setup(BrowserWebActivity.this, shareCountBtn, R.string.share_sum);
        shareCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BrowserShareTimeLineActivity.newIntent(url);
                startActivity(intent);
            }
        });
        getActionBar().setCustomView(title, new ActionBar.LayoutParams(Gravity.RIGHT));
        getActionBar().setDisplayShowCustomEnabled(true);

        getActionBar().setTitle(url);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, BrowserWebFragment.newInstance(url))
                    .commit();
            shareCount();
        } else {
            shareCountInt = savedInstanceState.getInt("shareCountInt");
            shareCountBtn.setText(String.valueOf(shareCountInt));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = MainTimeLineActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void shareCount(){
        String token = GlobalContext.getInstance().getSpecialToken();
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<ShortUrlBean> call = service.shortUrlShareCount(token, url);
        call.enqueue(new Callback<ShortUrlBean>() {
            @Override
            public void onResponse(Call<ShortUrlBean> call, Response<ShortUrlBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (shareCountBtn == null) {
                        return;
                    }
                    shareCountInt = response.body().share_counts;
                    shareCountBtn.setText(String.valueOf(shareCountInt));
                }
            }

            @Override
            public void onFailure(Call<ShortUrlBean> call, Throwable t) {

            }
        });
    }
}

