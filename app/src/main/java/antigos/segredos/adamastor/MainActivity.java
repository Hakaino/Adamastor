package antigos.segredos.adamastor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.ArrayList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences settings;
    private ArrayList<Object> objects;
    private AudioManager audioManager;
    private Sound sound;
    private Game game;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.initiate);
        context = this;
        new Beginning().execute();
        System.out.println("__________onCreate");
    }

    @Override
    protected void onPause(){
        super.onPause();
        //the game will pause and a snapshot will
        // be taken if the application pauses while
        // a game is in progress
        if (sound != null)sound.pause();
        if (game != null) {
            game.Pause();
            ImageView imageView = new ImageView(this);
            Bitmap snapshot = Bitmap.createBitmap(
                    game.getWidth(), game.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(snapshot);
            game.draw(canvas);
            imageView.setImageBitmap(snapshot);
            setContentView(imageView);
        }
        if (sound != null) audioManager.abandonAudioFocus(sound);
        try{//commit the preferences for next session
            int stage = (int) objects.get(1);
            SharedPreferences.Editor editPreferences = settings.edit();
            editPreferences.putInt("Level Unlocked", stage);
            if (objects.size() >=2 && objects.get(2) != null)
                editPreferences.putInt("coins saved", (int) objects.get(2));
            editPreferences.apply();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("__________onPause");
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (sound != null)sound.soundPool.autoResume();
        System.out.println("__________onResume");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        sound.stopMusic();
        sound.stopSFX();
        System.out.println("__________onStop");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                if (game != null) game.Pause();
                //else Home(getCurrentFocus());
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("StaticFieldLeak")
    private class Beginning extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //Screen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Point screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);
            // preferences
            settings = getPreferences(MODE_PRIVATE);
            int stage = settings.getInt("Level Unlocked", 1);
            int savedCoins = settings.getInt("coins saved", 0);
            boolean music = settings.getBoolean("music", true);
            boolean sfx = settings.getBoolean("sfx", true);
            //Audio Focus
            sound = new Sound(context, music, sfx);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            audioManager.requestAudioFocus(sound, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            // Initialize objects
            objects = new ArrayList<>();
            objects.add(0, getSupportFragmentManager());                    //support fragment manager
            objects.add(1, stage);                                          //current stage
            objects.add(2, savedCoins);                                     //saved coins
            objects.add(3, new Image(getResources(), screenSize.y));        //Image
            objects.add(4, screenSize);                                     //screen size
            objects.add(5, sound);                                          //Sound
            objects.add(6, null);                                   //Level
            objects.add(7, null);                                   //Hero
            return "You have " + String.valueOf((int) objects.get(2)) + " coins";
        }
        @Override
        protected void onPostExecute(String message) {
            if (sound.musicOn) sound.musics.get(0).start();
            Home(getCurrentFocus());
            //Play(getCurrentFocus());
            System.out.println("********Building Finished********");
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Loading extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object...o) {
            ArrayList O = (ArrayList) o[0];
            Sound msound = (Sound) O.get(5);
            if (msound != null && msound.musics.get(0).isPlaying()) msound.musics.get(0).pause();
            int stage = (int) O.get(1);
            Image image = (Image) O.get(3);
            if (stage == 4 || stage == 8) image.makeBoss();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            closeFragments();
            game = new Game(context);
            game.set(objects, true);
            setContentView(game);
        }
    }

    public void Home(View view){
        closeFragments();
        game = null;
        setContentView(R.layout.home);
        Banner();
        if (sound.musicOn && !sound.musics.get(0).isPlaying()) {
            sound.musics.get(0).seekTo(0);
            sound.musics.get(0).start();
        }
        System.out.println("__________Home");
    }

    public void Play(View view){
        if (((int) objects.get(1)) < 9) {
            setContentView(R.layout.loading);
            new Loading().execute(objects);
        }
        else{
            closeFragments();
            setContentView(R.layout.end);
            objects.set(1, 8);
        }
        System.out.println("__________Play");
    }

    public void Resume(View view){
        closeFragments();
        game = new Game(this);
        game.set(objects, false);
        setContentView(game);
    }

    public void Banner(){
        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .build();
        adView.loadAd(adRequest);
        // TODO: Add adView to your view hierarchy.
    }

    public void Info(View view) {
        setContentView(R.layout.info);
        TextView message= findViewById(R.id.story);
        message.setMovementMethod(new ScrollingMovementMethod());
        Banner();
    }

    public void Options(View view){
        setContentView(R.layout.options);

        ToggleButton sfxBtn = findViewById(R.id.sfx);
        sfxBtn.setChecked(sound.sfxOn);
        ToggleButton musicBtn = findViewById(R.id.music);
        musicBtn.setChecked(sound.musicOn);
        sfxBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                sound.sfxOn = isChecked;
                SharedPreferences.Editor editPreferences = settings.edit();
                editPreferences.putBoolean("sfx", isChecked);
                editPreferences.apply();
            }
        });
        musicBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                sound.musicOn = isChecked;
                SharedPreferences.Editor editPreferences = settings.edit();
                editPreferences.putBoolean("music", isChecked);
                editPreferences.apply();
                if (isChecked) sound.musics.get(0).start();
                else{
                    sound.musics.get(0).pause();
                }
            }
        });
        Banner();
        System.out.println("__________Options");
    }

    public void AskAgain(View view){
        setContentView(R.layout.reset);
        Banner();
        System.out.println("__________AskAgain");
    }

    public void Reset(View view){                 //reset stage
        objects.set(1, 1);
        objects.set(2, 0);                  //reset coins
        Home(getCurrentFocus());
        Banner();
        System.out.println("__________Reset");
    }

    public void Restart (View view){
        objects.set(1, (int) objects.get(1) - 1);
        Play(getCurrentFocus());
    }

    public void Chose(View view){
        setContentView(R.layout.levels);
        //buttons
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) //get the id which is an int
                {
                    case R.id.level1:
                        objects.set(1, 1);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level2:
                        objects.set(1, 2);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level3:
                        objects.set(1, 3);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level4:
                        objects.set(1, 4);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level5:
                        objects.set(1, 5);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level6:
                        objects.set(1, 6);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level7:
                        objects.set(1, 7);
                        Play(getCurrentFocus());
                        break;
                    case R.id.level8:
                        objects.set(1, 8);
                        Play(getCurrentFocus());
                        break;
                    case R.id.back:
                        Home(getCurrentFocus());
                        break;
                }
            }
        };
        Button[] Buttons = {
                findViewById(R.id.back),
                findViewById(R.id.level1),
                findViewById(R.id.level2),
                findViewById(R.id.level3),
                findViewById(R.id.level4),
                findViewById(R.id.level5),
                findViewById(R.id.level6),
                findViewById(R.id.level7),
                findViewById(R.id.level8)
        };
        for (int stage = 0; stage <= 8; stage++){
            boolean cheat = true;
            if (stage <= (int) objects.get(1) || cheat) Buttons[stage].setOnClickListener(listener);
            else Buttons[stage].setEnabled(false);
        }
        Banner();
    }

    private void closeFragments(){
        if (game != null) {
            if (game.pause != null) game.pause.dismiss();
            else if (game.finish != null) {
                sound.pause();
                game.finish.dismiss();
            }
        }
    }
}