package com.example.ripos.mefragment.setup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.cos.CosServiceFactory;
import com.example.ripos.fragment.MeFragment;
import com.example.ripos.homefragment.homeInvitepartners.HomeInvitePartnersActivity;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * ??????: qgl
 * ???????????????2020/12/23
 * ??????:????????????
 */
public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout iv_back;
    private RelativeLayout personal_mcode_line;  // ????????????
    private TextView user_name; // ????????????
    private TextView user_phone; // ???????????????
    private TextView user_code; //  ????????????
    private SimpleDraweeView me_head; //  ????????????
    //??????????????????
    private List<LocalMedia> selectList = new ArrayList<>();
    //?????????????????????
    private int maxSelectNum = 1;
    private final int PER_NUM = 10; // ??????????????????inCode
    private COSXMLUploadTask cosxmlTask;
    private String folderName = "portrait";
    private CosXmlService cosXmlService;
    private TransferManager transferManager;

    @Override
    protected int getLayoutId() {
        //?????????????????????
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.personal_activity;
    }

    @Override
    protected void initView() {
        //????????????????????????
        cosXmlService = CosServiceFactory.getCosXmlService(this, "ap-beijing", getSecretId(), getSecretKey(), true);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
        iv_back = findViewById(R.id.iv_back);
        personal_mcode_line = findViewById(R.id.personal_mcode_line);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        user_code = findViewById(R.id.user_code);
        me_head = findViewById(R.id.me_head);

    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        personal_mcode_line.setOnClickListener(this);
        me_head.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        posDate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.personal_mcode_line:
                startActivity(new Intent(PersonalActivity.this, HomeInvitePartnersActivity.class));
                break;
            case R.id.me_head:
                initSelectImage(PER_NUM);
                break;
        }
    }

    // ????????????????????????
    public void posDate() {
        RequestParams params = new RequestParams();
        HttpRequest.getCurrent(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = result.getJSONObject("data");
                    JSONObject merchantDetail = data.getJSONObject("merchantDetail");
                    JSONObject merchantBrief = data.getJSONObject("merchantBrief");
                    user_name.setText(merchantDetail.getString("merchIdcardName").substring(0,1) + "**");
                    user_phone.setText(merchantDetail.getString("merchBankMobile").substring(0, 3) + "****" + merchantDetail.getString("merchBankMobile").substring(merchantDetail.getString("merchBankMobile").length() - 4));
                    user_code.setText(merchantBrief.getString("merchCode"));
                    Uri imgurl=Uri.parse(data.getString("portrait"));
                    // ??????Fresco???????????????????????????
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    imagePipeline.evictFromMemoryCache(imgurl);
                    imagePipeline.evictFromDiskCache(imgurl);
                    // combines above two lines
                    imagePipeline.evictFromCache(imgurl);
                    me_head.setImageURI(imgurl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * ?????????????????????
     */
    private void initSelectImage(int resultCode) {
        selectList = new ArrayList<>();
        PictureSelector.create(PersonalActivity.this)
                .openGallery(PictureMimeType.ofImage())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                .maxSelectNum(maxSelectNum)// ????????????????????????
                .minSelectNum(0)// ??????????????????
                .imageSpanCount(4)// ??????????????????
                .selectionMode(PictureConfig.SINGLE)// ?????? or ??????
                .previewImage(true)// ?????????????????????
                .isCamera(true)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
//                .imageFormat(PictureMimeType.PNG)// ??????????????????????????????,??????jpeg
                //.setOutputCameraPath("/CustomPath")// ???????????????????????????
                .compress(true)// ????????????
                .synOrAsy(true)//??????true?????????false ?????? ????????????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .withAspectRatio(1, 1)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                .selectionMedia(selectList)// ????????????????????????
                .minimumCompressSize(100)// ??????100kb??????????????????
                .forResult(resultCode);//????????????onActivityResult code
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectList = new ArrayList<>();
            switch (requestCode) {
                case PER_NUM:
                    // ????????????????????????
                    selectList = PictureSelector.obtainMultipleResult(data);
                    Uri uri = new Uri.Builder()
                            .scheme(UriUtil.LOCAL_FILE_SCHEME)
                            .path(selectList.get(0).getCompressPath())
                            .build();
                    me_head.setImageURI(uri);
                    upload(selectList.get(0).getCompressPath());
                    break;
            }
        }
    }


    //????????????
    private void upload(String file_url) {
        if (TextUtils.isEmpty(file_url)) {
            return;
        }
        if (cosxmlTask == null) {
            File file = new File(file_url);
            String cosPath;
            if (TextUtils.isEmpty(folderName)) {
                cosPath = file.getName();
            } else {
                cosPath = folderName + File.separator + file.getName();
            }
            cosxmlTask = transferManager.upload(getBucketName(), cosPath, file_url, null);
            Log.e("??????-------???", getBucketName() + "----" + cosPath + "---" + file_url);
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {
                    // refreshUploadState(state);
                }
            });
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(final long complete, final long target) {
                    // refreshUploadProgress(complete, target);
                }
            });

            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                    cosxmlTask = null;
                    setResult(RESULT_OK);
                    // ???????????????????????????????????????
                    // ???????????????url
                    posHead(cOSXMLUploadTaskResult.accessUrl);
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                        cosxmlTask = null;
                        Log.e("1111", "????????????");
                    }
                    exception.printStackTrace();
                    serviceException.printStackTrace();
                }
            });

        }
    }
    //????????????
    public void posHead(String url){
        RequestParams params = new RequestParams();
        params.put("portraitUrl",url);
        HttpRequest.getUpdatePortrait(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                // ??????,?????????????????????????????????
                EventBus.getDefault().post(new MeFragment());
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(),failuer.getEmsg());
            }
        });

    }
}
