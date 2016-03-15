package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 我的作品
 * Created by user on 2015/5/29.
 */
@Table(name = "WorkItem")
public class WorkItem implements Parcelable{
    @Column(column = "id")
    @Id
    private String id;
    @Column(column = "title")
    private String title;
    @Column(column = "description")
    private String description;
    @Column(column = "pic_sm")
    private String pic_sm;
    @Column(column = "pic_bg")
    private String pic_bg;
    @Column(column = "createtime")
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic_sm() {
        return pic_sm;
    }

    public void setPic_sm(String pic_sm) {
        this.pic_sm = pic_sm;
    }

    public String getPic_bg() {
        return pic_bg;
    }

    public void setPic_bg(String pic_bg) {
        this.pic_bg = pic_bg;
    }

    public String getCreatetime() {
        return createtime;
    }
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
    public WorkItem(String id, String title, String description, String pic_sm, String pic_bg, String createtime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pic_sm = pic_sm;
        this.pic_bg = pic_bg;
        this.createtime = createtime;
    }
    public WorkItem() {
    }
    @Override
    public String toString() {
        return "WorkItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pic_sm='" + pic_sm + '\'' +
                ", pic_bg='" + pic_bg + '\'' +
                ", createtime='" + createtime + '\'' +
                '}';
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(pic_sm);
        dest.writeString(pic_bg);
        dest.writeString(createtime);
    }
    public static Creator<WorkItem> CREATOR = new Creator<WorkItem>(){
        @Override
        public WorkItem createFromParcel(Parcel source) {
            String id = source.readString();
            String title = source.readString();
            String description = source.readString();
            String pic_sm = source.readString();
            String pic_bg = source.readString();
            String createtime = source.readString();
            return new WorkItem(id,title,description,pic_sm,pic_bg,createtime);
        }
        @Override
        public WorkItem[] newArray(int size) {
            return new WorkItem[0];
        }
    };
}
