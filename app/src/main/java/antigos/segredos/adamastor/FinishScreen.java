package antigos.segredos.adamastor;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class FinishScreen extends AppCompatDialogFragment{

    //int number = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View finish_screen = layoutInflater.inflate(R.layout.finish, null);

        //TextView textView = (TextView) finish_screen.findViewById(R.id.finishCoins);
        //String plural = "";
        //if (1 != number) plural = "s";
        //String message = "You have caught " + String.valueOf(number) + " coin" + plural;
        //textView.setText(message);
        //textView.setTextSize(40);
        //textView.setTextColor(Color.RED);

        final AlertDialog.Builder finish_builder = new AlertDialog.Builder(getActivity());
        finish_builder.setView(finish_screen);
        final AlertDialog Finish = finish_builder.create();
        Finish.setCanceledOnTouchOutside(false);
        return Finish;
    }

    //public void setCoins(int coins) {
    //    number = coins;
    //}
}