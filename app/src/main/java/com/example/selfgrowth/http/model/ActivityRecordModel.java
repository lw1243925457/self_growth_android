package com.example.selfgrowth.http.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActivityRecordModel {

    private Date date;
    private String activity;
}
