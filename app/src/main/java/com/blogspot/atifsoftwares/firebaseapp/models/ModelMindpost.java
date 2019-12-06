package com.blogspot.atifsoftwares.firebaseapp.models;
/*생성자 -->이소연*/
public class ModelMindpost {

    String mId, mDescr,mLikes, mUid, mEmail, mDp, mName;

    public ModelMindpost(String mId, String mDescr, String mLikes, String muid, String mEmail, String mDp, String mName) {
        this.mId = mId;
        this.mDescr = mDescr;
        this.mLikes = mLikes;
        this.mUid = muid;
        this.mEmail = mEmail;
        this.mDp = mDp;
        this.mName = mName;
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

    public String getMUid() {
        return mUid;
    }

    public void setMUid(String muid) {
        this.mUid = muid;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmDp() {
        return mDp;
    }

    public void setmDp(String mDp) {
        this.mDp = mDp;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }


}

