package antigos.segredos.adamastor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    public Level stage;
    public Hero hero;
    private FragmentManager fragmentManager;
    public AlertDialog pause;
    public FinishScreen finish;
    ArrayList<Object> object;
    private boolean newGame;
    private Sound sound;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    void set(ArrayList<Object> objects, boolean gameIsNew) {
        fragmentManager = (FragmentManager) objects.get(0);
        object = objects;
        sound = (Sound) object.get(5);
        newGame = gameIsNew;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this, object);
        thread.running = true;
        if (newGame) thread.Reset(new Point(0, 0));
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread.running = false;
                thread.join();
                thread = null;
                retry = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hero.start = true;
            int X = (int) event.getX();
            int Y = (int) event.getY();
            boolean p1 = X < stage.Pause_rect.right;
            boolean p2 = Y < stage.Pause_rect.bottom;
            boolean jumpX = X < stage.Jump_rect.right;
            boolean dropY = stage.Jump_rect.top < Y;
            boolean dropX = stage.Drop_rect.left < X;
            boolean jumpY = stage.Drop_rect.top < Y;
            if (p1 && p2 && pause == null) Pause();
            else if (jumpX && jumpY) {
                if (hero.underwater) hero.dy--;
                else if (0 <= hero.inLand) {
                    hero.Jump();
                }
            } else if (dropX && dropY) {
                if (hero.underwater) hero.dy++;
                else hero.Drop();
            }

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int cursorX = (int) event.getX();
            int cursorY = (int) event.getY();
            for (Foe foe : stage.foes)
                if (!foe.type.equals("acid") && foe.rect.contains(cursorX, cursorY)) {
                    foe.alive = false;
                    foe.death = true;
                }
            for (Solid solid : stage.moving)
                if (solid.rect.contains(cursorX, cursorY)) {
                    solid.start = true;
                    System.out.println("rock cut");
                }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        stage.draw(canvas, getHeight());
        hero.draw(canvas);
    }

    public void update() {
        if (hero.death) {
            thread.Reset(stage.checkpoint);
        }
        if (stage.stop) Finish();
        hero.update(getHeight());
        stage.update(hero);
    }

    public void Pause() {
        if (thread != null) thread.running = false;
        sound.pause();
        if (pause == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final View pause_menu = layoutInflater.inflate(R.layout.pause, null);
            final AlertDialog.Builder pause_builder = new AlertDialog.Builder(getContext());
            pause_builder.setView(pause_menu);
            pause = pause_builder.create();
            pause.setCanceledOnTouchOutside(false);
            pause.setCancelable(false);
            pause.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    //if (keyCode == KeyEvent.KEYCODE_BACK){}
                    return true;
                }
            });
            pause.show();
            pause.getLayoutInflater().inflate(R.layout.pause, null);
            ToggleButton sfxBtn = pause_menu.findViewById(R.id.sfx);
            ToggleButton musicBtn = pause_menu.findViewById(R.id.music);
            sfxBtn.setChecked(sound.sfxOn);
            musicBtn.setChecked(sound.musicOn);
            sfxBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sound.sfxOn = isChecked;
                }
            });
            musicBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sound.musicOn = isChecked;
                }
            });
        }
    }

    private void Finish() {
        if (sound.musicOn) {
            sound.musics.get(1).pause();
            sound.musics.get(2).start();
        }
        object.set(1, 1 + (int) object.get(1)); //save stage progress
        finish = new FinishScreen();
        //object.set(2, stage.coins + (int) object.get(2));     //add caught coins
        //finish.setCoins(stage.coins);
        finish.show(fragmentManager, "Finish");
        thread.running = false;
    }

}


