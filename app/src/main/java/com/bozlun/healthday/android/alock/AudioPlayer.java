/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.bozlun.healthday.android.alock;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Vibrator;


import com.bozlun.healthday.android.R;

import java.io.IOException;

/**
 * 音频播放器
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class AudioPlayer {

    /**
     * 音频播放
     */
    private MediaPlayer mPlayer;

    /**
     * 共通音频播放器实例
     */
    private static AudioPlayer sAudioPlayer;

    private final Context mContext;

    /**
     * 振动
     */
    private Vibrator mVibrator;

    /**
     * 是否正在播放停止录音音乐
     */
    public static boolean sIsRecordStopMusic = false;

    private AudioPlayer(Context appContext) {
        mContext = appContext;
    }

    /**
     * 取得音频播放器实例
     *
     * @param context context
     * @return 音频播放器实例
     */
    public static AudioPlayer getInstance(Context context) {
        if (sAudioPlayer == null) {
            sAudioPlayer = new AudioPlayer(context.getApplicationContext());
        }
        return sAudioPlayer;
    }

    /**
     * 停止播放，振动
     */
    public void stop() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

    }

    /**
     * 开始播放
     *
     * @param url     音频文件地址
     * @param looping 是否循环播放
     * @param vibrate 是否振动
     */
    public void play(String url, final boolean looping, final boolean vibrate) {
        stop();
        // 当设为振动时
        if (vibrate) {
            vibrate();
        }

        mPlayer = new MediaPlayer();
        try {
            // 设置数据源
            mPlayer.setDataSource(url);
            // 异步准备，不会阻碍主线程
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException | SecurityException
                | IllegalStateException | IOException e) {
            ToastUtil.showShortToast(mContext,
                    mContext.getString(R.string.play_fail));
        }

        // 当准备好时
        mPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                if (looping) {
                    mPlayer.setLooping(true);
                    mPlayer.start();
                } else {
                    mPlayer.start();
                }
            }
        });
        // 当播放完成时
        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mPlayer.isLooping()) {
                    stopPlay();
                }

            }
        });
        // 当播放出现错误时
        mPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.showShortToast(mContext,
                        mContext.getString(R.string.play_fail));
                return false;
            }

        });
    }

    /**
     * 开始播放
     *
     * @param resId   音频资源文件ID
     * @param looping 是否循环播放
     * @param vibrate 是否振动
     */
    public void playRaw(final int resId, boolean looping, boolean vibrate) {
        stop();
        // 当设为振动时
        if (vibrate) {
            vibrate();
        }

        // 设置音频资源文件
        mPlayer = MediaPlayer.create(mContext, resId);
        if (looping) {
            mPlayer.setLooping(true);
            mPlayer.start();
        } else {
            mPlayer.start();
        }

        // 当播放完成时
        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mPlayer.isLooping()) {
                    stopPlay();
                }



            }
        });
        // 当播放出现错误时
        mPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.showShortToast(mContext,
                        mContext.getString(R.string.play_fail));
                return false;
            }

        });
    }

    /**
     * 振动
     */
    public void vibrate() {
        mVibrator = (Vibrator) mContext
                .getSystemService(Context.VIBRATOR_SERVICE);
        // 前一个代表等待多少毫秒启动vibrator，后一个代表vibrator持续多少毫秒停止。
        // 从repeat索引开始的振动进行循环。-1表示只振动一次，非-1表示从pattern的指定下标开始重复振动。
        mVibrator.vibrate(new long[]{1000, 1000}, 0);
    }

}
