package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class IrregularityReportDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("authorId")
    private Long authorId;
    @SerializedName("comment")
    private String comment;

    public IrregularityReportDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
