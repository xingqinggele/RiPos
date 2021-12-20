package com.example.ripos.homefragment.homeintegral.bean;

/**
 * 作者: qgl
 * 创建日期：2021/5/18
 * 描述:积分设备列表Bean
 */
public class IntegralMostBean {
    private String id;
    private String typeName;
    private String returnMoney;
    private String taxation;
    private String returnIntegral;
    private String imgPath;
    private String detailImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney;
    }

    public String getTaxation() {
        return taxation;
    }

    public void setTaxation(String taxation) {
        this.taxation = taxation;
    }

    public String getReturnIntegral() {
        return returnIntegral;
    }

    public void setReturnIntegral(String returnIntegral) {
        this.returnIntegral = returnIntegral;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getDetailImg() {
        return detailImg;
    }

    public void setDetailImg(String detailImg) {
        this.detailImg = detailImg;
    }
}
