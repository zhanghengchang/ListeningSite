package com.kuaige.player.listener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Date: 2018-10-08
 * Time: 17:56
 * Description:
 */
public interface VideoListener extends IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener{
}
