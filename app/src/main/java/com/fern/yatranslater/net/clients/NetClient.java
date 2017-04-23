package com.fern.yatranslater.net.clients;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fern.yatranslater.R;
import com.fern.yatranslater.YaApplication;
import com.fern.yatranslater.entities.Translate;
import com.fern.yatranslater.entities.TranslateLangs;
import com.fern.yatranslater.net.interfaces.YaNetService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrey Saprykin on 16.04.2017.
 */

public class NetClient {
    private static final String TAG = "NetClient";
    public final int SERVER_CODE_200 = 200;
    public final int SERVER_CODE_401 = 401;
    public final int SERVER_CODE_402 = 402;
    public final int SERVER_CODE_403 = 403;
    public final int SERVER_CODE_404 = 404;
    public final int SERVER_CODE_413 = 413;
    public final int SERVER_CODE_422 = 422;
    public final int SERVER_CODE_501 = 501;

    private Retrofit retrofit;

    private YaNetService yaNetService;

    private Call<Translate> translateCall;
    private Call<TranslateLangs> langsCall;

    private Context context;
    private YaApplication yaApplicationContext;
    private AlertDialog showMessage;

    /**Retrofit клиент для работы с API сервера*/
    public NetClient(Context context, YaApplication yaApplication) {
        this.context = context;
        this.yaApplicationContext = yaApplication;

        this.retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        yaNetService = retrofit.create(YaNetService.class);

        this.showMessage = new AlertDialog.Builder(context).setPositiveButton(R.string.positive_button_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (showMessage != null && showMessage.isShowing()) {
                    showMessage.dismiss();
                }
            }
        }).create();
    }

    /**Запрос к API сервера для получения резульататов перевода текста*/
    public void translateText(String direction, final String text) {
        translateCall = yaNetService.translate(direction, context.getString(R.string.api_key), text);
        translateCall.enqueue(new Callback<Translate>() {
            @Override
            public void onResponse(Call<Translate> call, Response<Translate> response) {
                if (response.code() != SERVER_CODE_200) {
                    int code = response.code();
                    showCodeErrorDialog(code, "Code = " + code + " request = " + call.request().toString() + " headers = " + call.request().headers().toString());
                } else {
                    yaApplicationContext.updateResult(text, response.body().getText());
                }
            }

            @Override
            public void onFailure(Call<Translate> call, Throwable t) {
                showFailureDialog(t.getMessage());
            }
        });
    }

    /**Отображение диалога ошибки сервера*/
    public void showCodeErrorDialog(int code, String logMessage) {
        String message;
        switch (code) {
            case SERVER_CODE_401 | SERVER_CODE_403:
                message = context.getResources().getString(R.string.error_dialog_message_401);
                break;
            case SERVER_CODE_402:
                message = context.getResources().getString(R.string.error_dialog_message_402);
                break;
            case SERVER_CODE_404:
                message = context.getResources().getString(R.string.error_dialog_message_404);
                break;
            case SERVER_CODE_413:
                message = context.getResources().getString(R.string.error_dialog_message_413);
                break;
            case SERVER_CODE_422:
                message = context.getResources().getString(R.string.error_dialog_message_422);
                break;
            case SERVER_CODE_501:
                message = context.getResources().getString(R.string.error_dialog_message_501);
                break;
            default:
                message = context.getResources().getString(R.string.common_error_dialog_message);
                break;
        }
        prepareDialog(context.getResources().getString(R.string.error_dialog_title, code), message);
        Log.d(TAG, "Send Log: " + logMessage);
    }

    /**Отображение диалога ошибки сети*/
    public void showFailureDialog(String logMessage) {
        prepareDialog(context.getResources().getString(R.string.failure_dialog_title), context.getResources().getString(R.string.failure_dialog_message));
        Log.d(TAG, "Send Log: " + logMessage);
    }

    private void prepareDialog(String title, String message) {
        showMessage.setTitle(title);
        showMessage.setMessage(message);
        showMessage.show();
    }

    public YaNetService getYaNetService() {
        return yaNetService;
    }
}
