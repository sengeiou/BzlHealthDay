package com.bozlun.healthday.android.friend;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.bzlmaps.CommomDialog;
import com.bozlun.healthday.android.friend.bean.NewApplyFrendBean;
import com.bozlun.healthday.android.friend.bean.NewFrendApplyBean;
import com.bozlun.healthday.android.friend.bean.TextItem;
import com.bozlun.healthday.android.friend.mutilbind.NewApplyDataBind;
import com.bozlun.healthday.android.friend.mutilbind.NewFrendApplyDataBind;
import com.bozlun.healthday.android.friend.mutilbind.TextItemViewBinder;
import com.bozlun.healthday.android.siswatch.WatchBaseActivity;
import com.bozlun.healthday.android.util.ToastUtil;
import com.bozlun.healthday.android.util.URLs;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestPressent;
import com.bozlun.healthday.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/19 11:30
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class FriendApplyActivity
        extends WatchBaseActivity
        implements View.OnClickListener, RequestView, NewFrendApplyDataBind.ButtonOnClickLister {
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.w30s_listView)
    RecyclerView w30sListView;
    @BindView(R.id.toolbar_normal)
    Toolbar mNormalToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fredensapply_history);
        ButterKnife.bind(this);

        inEdit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inEdit() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        //设置标题
        barTitles.setText(getResources().getString(R.string.string_frend_apply));//好友申请

        mNormalToolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        mNormalToolbar.setNavigationOnClickListener(this);//右边返回按钮点击事件


        w30sListView.setLayoutManager(new GridLayoutManager(this, 1));
        //添加Android自带的分割线
        w30sListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
        if (!TextUtils.isEmpty(userId)) {
            if (multiTypeAdapter != null) multiTypeAdapter = null;
            if (items != null) items = null;
            //查找朋友的新申请
            findNewApplyFrend(userId);
        }
    }

    /**
     * 右边返回按钮点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        finish();
    }


    private RequestPressent requestPressent;

    /**
     * 查找朋友的新申请
     *
     * @param userId
     */
    public void findNewApplyFrend(String userId) {
        String sleepUrl = URLs.HTTPs + Commont.FindNewFrend;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            Log.d("-----------朋友--", " 查找朋友的新申请参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 删除我对好友的申请记录
     *
     * @param userId
     * @param applicant
     */
    public void deleteApplyFrend(String userId, String applicant) {
        String sleepUrl = URLs.HTTPs + Commont.DeleteApplyFrend;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("applicant", applicant);
            Log.d("-----------朋友--", " 查找朋友的新申请参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x06, sleepUrl, FriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 同意或者驳回申请
     *
     * @param userId
     */
    public void findReturnApply(String userId, String applicant, int status, int see) {
        String sleepUrl = URLs.HTTPs + Commont.FindReturnApply;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("applicant", applicant);
            sleepJson.put("status", status);
//            sleepJson.put("see", see);
            Log.d("-----------朋友--", " 同意或者驳回申请参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x03, sleepUrl, FriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }


    /**
     * 查找我的申请历史
     *
     * @param userId
     */
    public void findMineApplyHistory(String userId) {
        String sleepUrl = URLs.HTTPs + Commont.FindApplyHistory;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            Log.d("-----------朋友--", " 查找我的申请历史参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, sleepUrl, FriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAddhead = false;
    }

    boolean isAddhead = false;
    MultiTypeAdapter multiTypeAdapter = null;
    Items items = null;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                if (multiTypeAdapter == null) multiTypeAdapter = new MultiTypeAdapter();
                if (items == null) items = new Items();
                switch (message.what) {
                    case 0x01:
                        Log.d("----------新的朋友申请列表返回--", message.obj.toString());
                        items.clear();
                        //朋友申请
                        if (handler != null) handler.sendEmptyMessage(0x05);
                        NewFrendApplyDataBind newFrendApplyDataBind = new NewFrendApplyDataBind();
                        //设置布局按钮监听
                        newFrendApplyDataBind.setmButtonOnClickLister(FriendApplyActivity.this);
                        multiTypeAdapter.register(NewFrendApplyBean.ApplyListBean.class, newFrendApplyDataBind);
                        NewFrendApplyBean newFrendApplyBean = new Gson().fromJson(message.obj.toString(), NewFrendApplyBean.class);
                        if (newFrendApplyBean != null && newFrendApplyBean.getResultCode().equals("001")) {
                            List<NewFrendApplyBean.ApplyListBean> applyList = newFrendApplyBean.getApplyList();
                            if (applyList != null && !applyList.isEmpty()) {
                                for (NewFrendApplyBean.ApplyListBean ls : applyList) {
                                    items.add(new NewFrendApplyBean.ApplyListBean(ls.getBirthday(), ls.getImage(), ls.getNickName(), ls.getSex(), ls.getWeight(), ls.getUserId(), ls.getPhone(), ls.getHeight()));
                                }
                            }

                        }
                        break;
                    case 0x02:

                        Log.d("----------我的申请历史返回--", message.obj.toString());
                        NewApplyDataBind newApplyDataBind = new NewApplyDataBind();
                        newApplyDataBind.setmOnLongDeletelister(new NewApplyDataBind.OnLongDeletelister() {
                            @Override
                            public void OnLongDeletelister(final String applicant, String userName) {

                                new CommomDialog(FriendApplyActivity.this, R.style.dialog, MyApp.getInstance().getResources().getString(R.string.string_add_frend)
                                        + userName + MyApp.getInstance().getResources().getString(R.string.string_add_frend_delete), new CommomDialog.OnCloseListener() {
                                    @Override
                                    public void onClick(Dialog dialog, boolean confirm) {
                                        if (confirm) {

                                            String userId = (String) SharedPreferencesUtils.readObject(FriendApplyActivity.this, "userId");
                                            if (!TextUtils.isEmpty(userId)) {
//                                                if (multiTypeAdapter != null) multiTypeAdapter = null;
//                                                if (items != null) items = null;

                                                deleteApplyFrend(userId, applicant);
                                                if (multiTypeAdapter != null)
                                                    multiTypeAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        dialog.dismiss();
                                    }
                                }).setTitle(MyApp.getInstance().getResources().getString(R.string.string_delete_frend)).show();


                            }
                        });
                        multiTypeAdapter.register(NewApplyFrendBean.MyApplyBean.class, newApplyDataBind);
                        NewApplyFrendBean newMineApply = new Gson().fromJson(message.obj.toString(), NewApplyFrendBean.class);
                        if (newMineApply != null && newMineApply.getResultCode().equals("001")) {
                            List<NewApplyFrendBean.MyApplyBean> applyList = newMineApply.getMyApply();
                            if (applyList != null && !applyList.isEmpty()) {
                                //我的申请记录
                                if (!isAddhead) {
                                    isAddhead = true;
                                    multiTypeAdapter.register(TextItem.class, new TextItemViewBinder());
                                    TextItem textItem = new TextItem(getResources().getString(R.string.sreing_apply_history));//我的申请记录
                                    items.add(textItem);

                                    for (NewApplyFrendBean.MyApplyBean ls : applyList) {
                                        items.add(new NewApplyFrendBean.MyApplyBean(ls.getBirthday(), ls.getImage(), ls.getNickName(),
                                                ls.getSex(), ls.getWeight(), ls.getUserId(), ls.getFriendStatus(), ls.getPhone(), ls.getHeight()));
                                    }

                                    multiTypeAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                        break;
                    case 0x03:
                        try {
                            Log.d("----------同意或拒绝返回--", message.obj.toString());
                            JSONObject jsonObject = new JSONObject(message.obj.toString());
                            if (jsonObject.has("resultCode")) {
                                String resultCode1 = jsonObject.getString("resultCode");
                                boolean b = Commont.ReturnCode(resultCode1);
                                if (b) {
                                    Log.d("-----------朋友--", "添加成功--重新获取列表");
                                    String userId = (String) SharedPreferencesUtils.readObject(FriendApplyActivity.this, "userId");
                                    if (!TextUtils.isEmpty(userId)) {
                                        if (multiTypeAdapter != null) multiTypeAdapter = null;
                                        if (items != null) items = null;
                                        isAddhead = false;
                                        //查找朋友的新申请
                                        findNewApplyFrend(userId);
                                        if (handler != null)
                                            handler.sendEmptyMessageAtTime(0x05, 1000);

                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x05:
                        handler.removeMessages(0x05);
                        String userId = (String) SharedPreferencesUtils.readObject(FriendApplyActivity.this, "userId");
                        if (!TextUtils.isEmpty(userId)) {
                            //查找我的申请历史
                            findMineApplyHistory(userId);
                        }
                        break;
                    case 0x06:
                        handler.removeMessages(0x06);
                        try {
                            JSONObject jsonObject = new JSONObject(message.obj.toString());
                            if (jsonObject.has("resultCode")) {
                                String resultCode1 = jsonObject.getString("resultCode");
                                boolean b = Commont.ReturnCode(resultCode1);
                                if (b) {
                                    handler.sendEmptyMessage(0x05);

                                    String userIds = (String) SharedPreferencesUtils.readObject(FriendApplyActivity.this, "userId");
                                    if (!TextUtils.isEmpty(userIds)) {
                                        if (multiTypeAdapter != null) multiTypeAdapter = null;
                                        if (items != null) items = null;
                                        isAddhead = false;
                                        //查找朋友的新申请
                                        findNewApplyFrend(userIds);
                                    }
                                } else {
                                    ToastUtil.showShort(FriendApplyActivity.this, getResources().getString(R.string.string_network_error));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                if (items != null) multiTypeAdapter.setItems(items);
                if (multiTypeAdapter != null) {
                    w30sListView.setAdapter(multiTypeAdapter);
                    multiTypeAdapter.notifyDataSetChanged();
                }
            } catch (Error error) {
            }

            return false;
        }
    });


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object == null || TextUtils.isEmpty(object.toString().trim()) || object.toString().contains("<html>"))
            return;
        Log.d("-----------朋友--", object.toString());
        Message message = new Message();
        message.what = what;
        message.obj = object;
        if (handler != null) handler.sendMessage(message);
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    /**
     * * 是否同意添加好友
     *
     * @param frendUserId
     * @param stute       stute 2 - 通过  stute 3 - 拒绝
     * @param checked     1 详细资料对好友不可见，1 是可见
     */
    @Override
    public void OnButtonOnClickLister(String frendUserId, int stute, boolean checked) {
        String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
        if (!TextUtils.isEmpty(userId)) {
            //同意或者拒绝
            findReturnApply(userId, frendUserId, stute, checked ? 1 : 0);
        }
    }


}
