package antigos.segredos.adamastor;

import android.content.res.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class FileReader{

    private final ArrayList<String> filesText = new ArrayList<>();

    FileReader(Resources resources, int fileFromRes) {

        try {
            InputStream inputStream = resources.openRawResource(fileFromRes);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) filesText.add(nextLine);
        } catch (IOException e) {
            throw new RuntimeException("Could not open the file");
        }
    }

    char[][] newLevel(){
        char[][] map = new char[filesText.size()][];
        for (int y = 0; y < filesText.size(); y++) {
            map[y] = filesText.get(y).toCharArray();
            if (map[y].length <= 0) throw new java.lang.RuntimeException("Empty line in file.lvl");
        }
        return map;
    }
}
