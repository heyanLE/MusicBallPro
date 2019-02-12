package cn.heyanle.musicballpro;

import cn.heyanle.musicballpro.bean.FunctionInfo;
import cn.heyanle.musicballpro.models.MainModel;
import cn.heyanle.musicballpro.models.MusicModel;
import cn.heyanle.musicballpro.presenters.notification.BallPresenter;
import cn.heyanle.musicballpro.presenters.notification.BallViewPresenter;

/**
 * 主要监察者 MainProcurator 单例模式
 * 每当Rx（项目内所有Model的监听都为Rx组件）传输数据的时候
 * 注 ：如果为Followable传输 则为背压延迟后
 * 检查当前状态是否正确
 * 如果MusicModel有音乐 但小球没显示，就显示
 * 如果MusicModel空音乐 但小球显示，则隐藏
 * Created by HeYanLe
 * 2019/2/3 0003
 * https://github.com/heyanLE
 */
public class MainProcurator {

    private BallPresenter ballPresenter = null;
    //private BallViewPresenter ballViewPresenter = null;

    public interface OnRestart{

        void restart();

    }

    private OnRestart restart;


    public void setBallPresenter(BallPresenter ballPresenter){
        this.ballPresenter = ballPresenter;
    }

    public void work(){

        if (MainModel.getInstance().isOpen()){//如果开关开启
            if (ballPresenter == null){//但这没初始化
                restart.restart();
            }
        }

        if (MusicModel.getInstance().getNowMusic().isEmpty()){//如果歌曲为空
            if (ballPresenter.isShow()){//但小球显示了
                ballPresenter.refresh();
            }
        }else{//如果歌曲不为空
            if (!ballPresenter.isShow()){//但小球没显示
                ballPresenter.refresh();
            }

        }

    }

    public void setRestart(OnRestart restart){
        this.restart = restart;
    }


    //======================单例模式==============================

    private static MainProcurator INSTANCE = null;
    private MainProcurator(){
        INSTANCE = this;
    }

    public static MainProcurator getInstance(){
        return INSTANCE;
    }

}
