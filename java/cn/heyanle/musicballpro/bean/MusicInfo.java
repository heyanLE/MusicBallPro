package cn.heyanle.musicballpro.bean;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * 音乐实体类 保存一个音乐的相关信息
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class MusicInfo {

    private String name = "";
    private String singer = "";
    private boolean isPlaying = false;
    private Drawable albumCover = null;

    private FunctionInfo next = null;
    private FunctionInfo last = null;
    private FunctionInfo click = null;
    private FunctionInfo musicPage = null;

    public String getName() {
        return name;
    }

    public MusicInfo name(String name) {
        this.name = name;
        return this;
    }

    public String getSinger() {
        return singer;
    }

    public MusicInfo singer(String singer) {
        this.singer = singer;
        return this;
    }

    public Drawable getAlbumCover() {
        return albumCover;
    }

    public MusicInfo albumCover(Drawable albumCover) {
        this.albumCover = albumCover;
        return this;
    }

    public MusicInfo setPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
        return this;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public FunctionInfo getNext() {
        return next;
    }

    public void setNext(FunctionInfo next) {
        this.next = next;
    }

    public FunctionInfo getLast() {
        return last;
    }

    public void setLast(FunctionInfo last) {
        this.last = last;
    }

    public FunctionInfo getClick() {
        return click;
    }

    public void setClick(FunctionInfo click) {
        this.click = click;
    }

    public FunctionInfo getMusicPage() {
        return musicPage;
    }

    public void setMusicPage(FunctionInfo longTouch) {
        this.musicPage = longTouch;
    }

    @NonNull
    @Override
    public String toString() {
        String albumCover = "";
        if (this.albumCover!=null){
            albumCover = this.albumCover.toString();
        }
        return "name:"+name+",isPlaying"+isPlaying+",singer:"+singer+"albumCover:"+albumCover;
    }

    /**
     * 是否为空 如果没有获取到音乐封面 则识别为无音乐
     * @return      是否为空
     */
    public boolean isEmpty(){
        return albumCover == null;
    }

    /**
     * 获得一个空的MusicInfo
     * @return      空的MusicInfo
     */
    public static MusicInfo getEmpty(){
        return new MusicInfo();
    }

}
