package com.blogspot.atifsoftwares.firebaseapp.models;
/*생성자 -->이소연*/
public class ModelMindpost {


    String mId, mDescr, mLikes, uUid, uEmail,uDp,uName ;


    public ModelMindpost() {

    }

    public ModelMindpost(String mId, String mDescr, String mLikes, String uUid, String uEmail, String uDp, String uName) {
        this.mId = mId;
        this.mDescr = mDescr;
        this.mLikes = mLikes;
        this.uUid = uUid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmDescr() {
        return mDescr;
    }

    public void setmDescr(String mDescr) {
        this.mDescr = mDescr;
    }

    public String getmLikes() {
        return mLikes;
    }

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public String getuUid() {
        return uUid;
    }

    public void setuUid(String uUid) {
        this.uUid = uUid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}