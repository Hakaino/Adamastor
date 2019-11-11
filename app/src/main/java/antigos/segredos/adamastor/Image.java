package antigos.segredos.adamastor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class Image{

    final Bitmap[][] Hero = new Bitmap[5][10];
    final Bitmap[][] Walker = new Bitmap[3][10];
    final Bitmap[][] Acid = new Bitmap[2][10];
    final Bitmap[][] Arrow = new Bitmap[2][10];
    final Bitmap[][] Jumper = new Bitmap[3][10];
    final Bitmap[][] Shooter = new Bitmap[3][10];
    final Bitmap[][] Missile = new Bitmap[3][10];
    final Bitmap[][] Lifter = new Bitmap[1][10];
    final Bitmap[][] Water = new Bitmap[1][10];
    final Bitmap[][] Block = new Bitmap[1][10];
    final Bitmap[] Buttons = new Bitmap[3];
    Bitmap[] Boss;
    final int unit;
    private final int height;
    final Resources resources;

    Image (Resources resources, int Height){
        //All the sprites in the same cheat
        this.resources = resources;
        unit = Height / 9;
        height = Height;
        int nRows = 10;
        int nLines = 23;
        Bitmap sprites = Decode(R.mipmap.sprites, nRows, nLines, Height);
        Bitmap[][] Sprites = Make(sprites, nRows, nLines);
        Distribute(Sprites);
        //buttons
        Buttons[0] = Decode(R.mipmap.button_up, 2, 2, Height);
        Buttons[1] = Decode(R.mipmap.button_down, 2, 2, Height);
        Buttons[2] = Decode(R.mipmap.button_pause, 2, 2, Height);
    }

    void makeBoss(){
        int frames = 10;
        Boss = new Bitmap[frames];
        int nHeight = 9;
        Bitmap monster = Decode(R.mipmap.monster, frames * 2, nHeight, height);
        int U = monster.getWidth() / frames;
        for(int row = 0; row < frames ; row++) {
            Bitmap rawImage = Bitmap.createBitmap(monster, row * U, 0, U, monster.getHeight());
            Bitmap image = Bitmap.createScaledBitmap(rawImage, unit * 2, unit * nHeight, true);
            Boss[row] = image;
        }
    }

    private Bitmap[][] Make(Bitmap bitmap, int rows, int lines){
        Bitmap[][] film = new Bitmap[lines][rows];
        int U = bitmap.getWidth() / rows;
        for(int row = 0; row < rows ; row++) {
            for (int line = 0; line < lines; line++) {
                Bitmap rawImage = Bitmap.createBitmap(bitmap, row * U, line * U, U, U);
                Bitmap image = Bitmap.createScaledBitmap(rawImage, U, U, true);
                film[line][row] = image;
            }
        }
        return film;
    }

    private Bitmap Decode(int ref, int nRows, int nLines, int height){
        // TODO: returns a scaled bitmap of a resource
        int SBH = 9; //height of the screen in blocks
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, ref, options);
        int sample = 1;
        if (10 <= height) while (options.outWidth * SBH > height * nRows * sample) sample++;
        options.inSampleSize = sample;
        options.inJustDecodeBounds = false;
        Bitmap decode_bitmap = BitmapFactory.decodeResource(resources, ref, options);
        return Bitmap.createScaledBitmap(decode_bitmap,nRows * unit, nLines * unit, true);
    }

    private void Distribute(Bitmap[][] bitmaps){
        for (int i = 0; i < bitmaps.length; i++) {
            switch (i) {
                case 0: //stand
                case 1: //run
                case 2: //die
                case 3: //jump
                case 4: //swim
                    Hero[i] = bitmaps[i];
                    break;
                case 5: //run
                case 6: //swim
                    Acid[i - 5] = bitmaps[i];
                    break;
                case 7: //run
                case 8: //die
                    Walker[i - 7] = bitmaps[i];
                    break;
                case 9: //run
                case 10: //die
                    Arrow[i - 9] = bitmaps[i];
                    break;
                case 11: //run
                case 12: //die
                case 13: //jump
                    Shooter[i - 11] = bitmaps[i];
                    break;
                case 14: //run
                case 15: //die
                case 16:
                    Jumper[i - 14] = bitmaps[i];
                    break;
                case 17: //run
                case 18: //die
                case 19: //swim
                    Missile[i - 17] = bitmaps[i];
                    break;
                case 20:
                    Lifter[i - 20] = bitmaps[i];
                    break;
                case 21:
                    Water[i - 21] = bitmaps[i];
                    break;
                case 22:
                    Block[i - 22] = bitmaps[i];
                    break;
            }
        }
    }
}