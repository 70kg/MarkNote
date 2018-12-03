package me.shouheng.notepal.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Collections;

import me.shouheng.commons.activity.PermissionActivity;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.LogUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.data.ModelFactory;
import me.shouheng.data.entity.Attachment;
import me.shouheng.data.entity.QuickNote;
import me.shouheng.data.entity.Model;
import me.shouheng.data.entity.Note;
import me.shouheng.notepal.Constants;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.dialog.AttachmentPicker;
import me.shouheng.notepal.dialog.QuickNoteDialog;
import me.shouheng.notepal.util.AppWidgetUtils;
import me.shouheng.notepal.util.AttachmentHelper;
import me.shouheng.notepal.util.preferences.LockPreferences;
import me.shouheng.notepal.viewmodel.NoteViewModel;

import static me.shouheng.notepal.Constants.SHORTCUT_ACTION_QUICK_NOTE;

public class QuickActivity extends PermissionActivity implements AttachmentHelper.OnAttachingFileListener {

    private final static int REQUEST_PASSWORD = 0x0016;

    private QuickNoteDialog quickNoteDialog;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPassword();
    }

    private void checkPassword() {
        if (LockPreferences.getInstance().isPasswordRequired() && !PalmApp.isPasswordChecked()) {
            LockActivity.requireLaunch(this, REQUEST_PASSWORD);
        } else {
            init();
        }
    }

    private void init() {
        handleIntent(getIntent());

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        LogUtils.d("action:" + action);

        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }

        switch (action) {
            case SHORTCUT_ACTION_QUICK_NOTE:
                break;
            case Constants.APP_WIDGET_ACTION_LIST_ITEM_CLICLED:
                Model model;
                if (intent.hasExtra(Constants.APP_WIDGET_EXTRA_NOTE)
                        && (model = (Model) intent.getSerializableExtra(Constants.APP_WIDGET_EXTRA_NOTE)) != null) {
                    if (model instanceof QuickNote) {
                        LogUtils.d(model);
                        editMindSnagging((QuickNote) model);
                    }
                }
                break;
            case Constants.APP_WIDGET_ACTION_QUICK_NOTE:
                editMindSnagging(ModelFactory.getQuickNote());
                break;
            default:
                finish();
        }
    }

    // TODO
    private void editMindSnagging(@NonNull QuickNote param) {
//        quickNoteDialog = new QuickNoteDialog.Builder()
//                .setMindSnagging(param)
//                .setOnAddAttachmentListener(mindSnagging -> showAttachmentPicker())
//                .setOnLifeMethodCalledListener(new OnLifeMethodCalledListener() {
//                    @Override
//                    public void onCancel() {
//                        finish();
//                    }
//
//                    @Override
//                    public void onDismiss() {
//                        finish();
//                    }
//                })
//                .setOnAttachmentClickListener(this::resolveAttachmentClick)
//                .setOnConfirmListener(this::saveMindSnagging)
//                .build();
//        quickNoteDialog.show(getSupportFragmentManager(), "MIND SNAGGING");
    }

    private void resolveAttachmentClick(Attachment attachment) {
        AttachmentHelper.resolveClickEvent(
                this,
                attachment,
                Collections.singletonList(attachment),
                attachment.getName());
    }

    private void saveMindSnagging(QuickNote quickNote, Attachment attachment) {
        noteViewModel.saveSnagging(ModelFactory.getNote(), quickNote, attachment).observe(this, new Observer<Resource<Note>>() {
            @Override
            public void onChanged(@Nullable Resource<Note> noteResource) {
                assert noteResource != null;
                switch (noteResource.status) {
                    case SUCCESS:
                        ToastUtils.makeToast(R.string.text_save_successfully);
                        AppWidgetUtils.notifyAppWidgets(QuickActivity.this);
                        break;
                    case FAILED:
                        ToastUtils.makeToast(R.string.text_failed_to_modify_data);
                        break;
                    case LOADING:break;
                }
            }
        });
    }

    private void showAttachmentPicker() {
        new AttachmentPicker.Builder()
                .setRecordVisible(false)
                .setVideoVisible(false)
                .build().show(getSupportFragmentManager(), "ATTACHMENT PICKER");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            AttachmentHelper.resolveResult(this, requestCode, data);
        }
        switch (requestCode) {
            case REQUEST_PASSWORD:
                if (resultCode == RESULT_OK) {
                    init();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment attachment) {
        ToastUtils.makeToast(R.string.text_failed_to_save_attachment);
    }

    @Override
    public void onAttachingFileFinished(Attachment attachment) {
        if (AttachmentHelper.checkAttachment(attachment)) {
//            quickNoteDialog.setAttachment(attachment);
        }
    }
}
