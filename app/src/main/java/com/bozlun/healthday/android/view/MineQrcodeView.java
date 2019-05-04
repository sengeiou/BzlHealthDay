package com.bozlun.healthday.android.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bozlun.healthday.android.Commont;
import com.bozlun.healthday.android.MyApp;
import com.bozlun.healthday.android.R;
import com.bozlun.healthday.android.siswatch.utils.WatchUtils;
import com.google.zxing.WriterException;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yzq.zxinglibrary.encode.CodeCreator;

/**
 * 我的二维码
 * Created by Admin
 * Date 2019/3/22
 */
public class MineQrcodeView extends Dialog {

    ImageView qrImg;
    private Context mContext;

    public MineQrcodeView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_qrcode_layout);


        initViews();

        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.USER_ID_DATA);
        if(WatchUtils.isEmpty(userId))
            return;
        if(userId.equals("9278cc399ab147d0ad3ef164ca156bf0"))  //游客
            return;
        Bitmap bitmap = null;
        try {
            /*
             * contentEtString：字符串内容
             * w：图片的宽
             * h：图片的高
             * logo：不需要logo的话直接传 0
             * */
            Bitmap logo = BitmapFactory.decodeResource((mContext==null?MyApp.getContext():mContext).getResources(), 0);
            bitmap = CodeCreator.createQRCode(userId, 400, 400, logo);
            qrImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }

    private void initViews() {
        qrImg = findViewById(R.id.qrCodeImg);

    }


}
