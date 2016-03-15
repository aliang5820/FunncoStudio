package com.funnco.funnco.utils.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.view.dialog.BaseDialog;

/**
 * Created by user on 2015/5/23.
 * @author Shawn
 */
public class DialogUtils {

    public static Dialog getDialog(Context context){
        return null;
    }

    public static BaseDialog getDia(final Context context,int titleId,String message){
        return BaseDialog.getDialog(context, titleId, message, "确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static Dialog getSimpleMessageDialog(Context context,String message){
        final Dialog dialog = new Dialog(context, R.style.dialog_themen);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_simplemessage, null);
        ((TextView)view.findViewById(R.id.tv_simplemessagedialog_message)).setText(message);
        dialog.setContentView(view, new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT));

        ((Button)view.findViewById(R.id.bt_simplemessagedialog_but)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }


}
