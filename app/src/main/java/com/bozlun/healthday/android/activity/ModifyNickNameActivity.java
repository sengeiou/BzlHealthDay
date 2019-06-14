package com.bozlun.healthday.android.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.base.BaseActivity;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.bozlun.healthday.android.util.ToastUtil;

import butterknife.BindView;

/**
 * Created by thinkpad on 2017/3/8.
 * 修改昵称
 */

public class ModifyNickNameActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.code_et_nickname)
    EditText codeEt;
//    private DialogSubscriber dialogSubscriber;
//    private SubscriberOnNextListener<String> subscriberOnNextListener;
//    private  boolean isregister=false;
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        isregister=false;
//        EventBus.getDefault().unregister(this);
//    }
    @Override
    protected void initViews() {

        tvTitle.setText(R.string.modify_nickname);


        Intent intent = getIntent();
        if (intent!=null) {
            String name = intent.getStringExtra("name");
            if (!WatchUtils.isEmpty(name)){
                codeEt.setHint(name+"");
            }else {
                codeEt.setHint(R.string.modify_nickname);
            }
        }

//        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
//            @Override
//            public void onNext(String result) {
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    String resultCode = jsonObject.getString("resultCode");
//                    if ("001".equals(resultCode.trim())) {
//                        String nickName = codeEt.getText().toString();
//                        userInfo.setNickName(nickName);
//
//                        SharedPreferences shares = getSharedPreferences("nickName", 0);
//                        SharedPreferences.Editor editors = shares.edit();
//                        editors.putString("name",nickName);
//                        editors.commit();
//
////                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
//                        ToastUtil.showShort(ModifyNickNameActivity.this, getString(R.string.modify_success));
//                        finish();
//                        Glide.get(ModifyNickNameActivity.this).clearMemory();//子线程清除缓存
//                    } else {
//                        ToastUtil.showShort(ModifyNickNameActivity.this, getString(R.string.submit_fail));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_modify_nickname;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String nickName = codeEt.getText().toString();
        if (!TextUtils.isEmpty(nickName)) {
//            modifyPersonData(nickName);
            Intent intent = new Intent();
            intent.putExtra("name", nickName);
            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            ToastUtil.showShort(this, getString(R.string.write_nickname));
        }
        return super.onOptionsItemSelected(item);
    }

//    private void modifyPersonData(String val) {
//        Gson gson = new Gson();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("userId", Common.customer_id);
//        map.put("nickName", val);
//        String mapjson = gson.toJson(map);
//        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ModifyNickNameActivity.this);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
//    }

}
