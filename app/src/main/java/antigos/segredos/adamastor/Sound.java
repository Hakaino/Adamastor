package antigos.segredos.adamastor;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import java.util.ArrayList;

class Sound implements AudioManager.OnAudioFocusChangeListener {

    ArrayList<MediaPlayer> musics;
    SoundPool soundPool;
    private final Context context;
    boolean musicOn, sfxOn;
    int[] pool;

    Sound(Context context, boolean musicOn, boolean sfxOn){
        this.context = context;
        this.musicOn = musicOn;
        this.sfxOn = sfxOn;
        setMusic(context);
        setPool(context);
    }

    private void setMusic(Context context){
        musics = new ArrayList<>(2);
        musics.add(0, MediaPlayer.create(context, R.raw.spiritsofthenight));
        musics.add(1, MediaPlayer.create(context, R.raw.running));
        musics.add(2, MediaPlayer.create(context, R.raw.theend));
        for (MediaPlayer music: musics) {
            music.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (!music.equals(musics.get(2)))music.setLooping(true);
        }
    }

    private void setPool(Context context){
        int maxStrm = 10;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(maxStrm)
                .setAudioAttributes(audioAttributes)
                .build();
        }
        else soundPool = new SoundPool(maxStrm, AudioManager.STREAM_MUSIC, 0);
        pool = new int[]{
                0,    //0
                soundPool.load(context, R.raw.herod, 1),    //1
                soundPool.load(context, R.raw.heroj, 1),    //2
                soundPool.load(context, R.raw.heros, 1),    //3
                soundPool.load(context, R.raw.walkerd, 1),  //4
                soundPool.load(context, R.raw.arrowd, 1),   //5
                soundPool.load(context, R.raw.jumperd, 1),  //6
                soundPool.load(context, R.raw.jumperj, 1),  //7
                soundPool.load(context, R.raw.shooterd, 1), //8
                soundPool.load(context, R.raw.shooters, 1), //9
                soundPool.load(context, R.raw.missild, 1),  //10
                soundPool.load(context, R.raw.checkpoint, 1),  //11
                soundPool.load(context, R.raw.rock, 1),  //12
        };
    }

    void pause(){
        for (MediaPlayer clip: musics) if (clip.isPlaying()) clip.pause();
        soundPool.autoPause();
    }

    void stopMusic(){
        for (MediaPlayer clip: musics) {
            if (clip.isPlaying()) clip.stop();
            clip.release();
        }
        musics.clear();
    }

    void stopSFX(){
        soundPool.release();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                setMusic(context);
                setPool(context);
                System.out.println("AUDIO FOCUS GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                System.out.println("AUDIO FOCUS LOSS TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopMusic();
                stopSFX();
                System.out.println("AUDIO FOCUS LOSS");
                break;
        }
    }
}
