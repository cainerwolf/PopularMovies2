package net.rayray.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rhawley on 7/23/15.
 */
public class Review implements Parcelable {
    private String strReviewer;
    private String strReview;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strReviewer);
        dest.writeString(strReview);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Review(String strReviewer, String strReview) {
        this.strReviewer = strReviewer;
        this.strReview = strReview;
    }

    private Review(Parcel in) {
        this.strReviewer = in.readString();
        this.strReview = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {

        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

    public String getReviewer() { return strReviewer; }

    public String getReview() { return strReview; }

}
