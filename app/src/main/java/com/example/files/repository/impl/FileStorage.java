//package com.example.files.repository.impl;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.PendingIntent;
//import android.app.RecoverableSecurityException;
//import android.app.RemoteAction;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Parcelable;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//
//import androidx.fragment.app.FragmentActivity;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.Closeable;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import kotlin.Metadata;
//import kotlin.Unit;
//import kotlin.collections.ArraysKt;
//import kotlin.collections.CollectionsKt;
//import kotlin.io.CloseableKt;
//import kotlin.jvm.functions.Function1;
//import kotlin.jvm.internal.Intrinsics;
//
//public class FileStorage {
//}
//
//
//public final class HiStorage {
//    @NotNull
//    public static final String EXTERNAL_FILE_DIRECTORY = "imooc";
//    private static final Uri DOCUMENT_EXTERNAL_URI;
//    @NotNull
//    public static final HiStorage INSTANCE;
//
//    @SuppressLint({"InlinedApi"})
//    @Nullable
//    public final Uri saveMedia(@NotNull Context context, @NotNull byte[] byteArray, @NotNull String fileName, @Nullable String mimeType, int width, int height) {
//        Intrinsics.checkParameterIsNotNull(context, "context");
//        Intrinsics.checkParameterIsNotNull(byteArray, "byteArray");
//        Intrinsics.checkParameterIsNotNull(fileName, "fileName");
//        String var10000 = mimeType;
//        if (mimeType == null) {
//            var10000 = Util.INSTANCE.guessFileMimeType(fileName);
//        }
//
//        String mediaMimeType = "var10000";
//        Uri externalMediaUri = Util.INSTANCE.guessExternalMediaUri(mimeType);
//        if (this.isExternalStorageLegacy()) {
//            File externalMediaDir = Util.INSTANCE.guessExternalFileDirectory(mediaMimeType);
//            File externalMediaAppDir = new File(externalMediaDir, "imooc");
//            if (!externalMediaAppDir.exists()) {
//                externalMediaAppDir.mkdirs();
//            }
//
//            File mediaFile = new File(externalMediaAppDir, fileName);
//
//            try {
//                FileOutputStream outputStream = new FileOutputStream(mediaFile);
//                outputStream.write(byteArray);
//                outputStream.flush();
//                outputStream.close();
//            } catch (Exception var13) {
//                var13.printStackTrace();
//                mediaFile.delete();
//                return null;
//            }
//
//            ContentValues values = new ContentValues();
//            values.put("_data", mediaFile.getAbsolutePath());
//            values.put("_display_name", fileName);
//            values.put("mime_type", mediaMimeType);
//            values.put("width", width);
//            values.put("height", height);
//            return context.getContentResolver().insert(externalMediaUri, values);
//        } else {
//            ContentValues values = new ContentValues();
//            values.put("_display_name", fileName);
//            values.put("mime_type", mediaMimeType);
//            values.put("width", width);
//            values.put("height", height);
//            values.put("is_pending", 1);
//            Uri var18 = context.getContentResolver().insert(externalMediaUri, values);
//            if (var18 != null) {
//                Intrinsics.checkExpressionValueIsNotNull(var18, "context.contentResolver.…i, values) ?: return null");
//                Uri uri = var18;
//                OutputStream var19 = context.getContentResolver().openOutputStream(uri);
//                if (var19 != null) {
//                    Intrinsics.checkExpressionValueIsNotNull(var19, "context.contentResolver.…tream(uri) ?: return null");
//                    OutputStream openOutputStream = var19;
//                    openOutputStream.write(byteArray);
//                    openOutputStream.flush();
//                    openOutputStream.close();
//                    values.clear();
//                    values.put("is_pending", 0);
//                    context.getContentResolver().update(uri, values, (String)null, (String[])null);
//                    this.grantUriPermission(context, uri);
//                    return uri;
//                } else {
//                    return null;
//                }
//            } else {
//                return null;
//            }
//        }
//    }
//
//    @NotNull
//    public final List queryMedias(@NotNull Activity activity, @Nullable String displayName, @Nullable String mimeType) {
//        Intrinsics.checkParameterIsNotNull(activity, "activity");
//        boolean var5 = false;
//        List list = (List)(new ArrayList());
//        String selection = "";
//        String[] sectionArgs = new String[0];
//        String[] projection = new String[]{"_id", "_data", "_display_name", "width", "height", "mime_type"};
//        if (!TextUtils.isEmpty((CharSequence)displayName)) {
//            selection = selection + "_display_name = ? ";
//            sectionArgs = (String[]) ArraysKt.plus(sectionArgs, displayName);
//        }
//
//        if (!TextUtils.isEmpty((CharSequence)mimeType)) {
//            selection = selection + "mime_type LIKE ?";
//            sectionArgs = (String[])ArraysKt.plus(sectionArgs, mimeType);
//        }
//
//        if (TextUtils.isEmpty((CharSequence)mimeType)) {
//            selection = selection + "mime_type LIKE ? or mime_type LIKE ? or mime_type LIKE ?";
//            sectionArgs = (String[])ArraysKt.plus(sectionArgs, new String[]{"image%", "video%", "audio%"});
//        }
//
//        String sortOrder = "_id DESC";
//        Uri mediaExternalUri = Util.INSTANCE.guessExternalMediaUri(mimeType);
//        Cursor var10000 = activity.getContentResolver().query(mediaExternalUri, projection, selection, sectionArgs, sortOrder);
//        if (var10000 != null) {
//            Closeable var10 = (Closeable)var10000;
//            boolean var11 = false;
//            Throwable var12 = (Throwable)null;
//
//            try {
//                Cursor it = (Cursor)var10;
//                boolean var14 = false;
//
//                while(it.moveToNext()) {
//                    int idIndex = it.getColumnIndex("_id");
//                    int pathIndex = it.getColumnIndex("_data");
//                    int displayNameIndex = it.getColumnIndex("_display_name");
//                    int widthIndex = it.getColumnIndex("width");
//                    int heightIndex = it.getColumnIndex("height");
//                    int mimeTypeIndex = it.getColumnIndex("mime_type");
//                    Uri externalMediaUri = Util.INSTANCE.guessExternalMediaUri(it.getString(mimeTypeIndex));
//                    Uri var10002 = ContentUris.withAppendedId(externalMediaUri, it.getLong(idIndex));
//                    Intrinsics.checkExpressionValueIsNotNull(var10002, "ContentUris.withAppended…Uri, it.getLong(idIndex))");
//                    String var10003 = it.getString(displayNameIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10003, "it.getString(displayNameIndex)");
//                    String var10004 = it.getString(mimeTypeIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10004, "it.getString(mimeTypeIndex)");
//                    int var10005 = it.getInt(widthIndex);
//                    int var10006 = it.getInt(heightIndex);
//                    String var10007 = it.getString(pathIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10007, "it.getString(pathIndex)");
//                    HiStorage.MediaInfo media = new HiStorage.MediaInfo(var10002, var10003, var10004, var10005, var10006, var10007);
//                    list.add(media);
//                }
//
//                it.close();
//                Unit var28 = Unit.INSTANCE;
//            } catch (Throwable var25) {
//                var12 = var25;
//                throw var25;
//            } finally {
//                CloseableKt.closeFinally(var10, var12);
//            }
//        }
//
//        return list;
//    }
//
//    // $FF: synthetic method
//    public static List queryMedias$default(HiStorage var0, Activity var1, String var2, String var3, int var4, Object var5) {
//        if ((var4 & 2) != 0) {
//            var2 = (String)null;
//        }
//
//        if ((var4 & 4) != 0) {
//            var3 = (String)null;
//        }
//
//        return var0.queryMedias(var1, var2, var3);
//    }
//
//    public final void deleteFile(@NotNull FragmentActivity activity, @NotNull Uri uri, @NotNull final Function1 callback) {
//        Intrinsics.checkParameterIsNotNull(activity, "activity");
//        Intrinsics.checkParameterIsNotNull(uri, "uri");
//        Intrinsics.checkParameterIsNotNull(callback, "callback");
//        if (DocumentsContract.isDocumentUri((Context)activity, uri)) {
//            boolean ret = DocumentsContract.deleteDocument(activity.getContentResolver(), uri);
//            callback.invoke(ret);
//        } else {
//            String mimeType = Util.INSTANCE.queryMimeTypeFromUri((Context)activity, uri);
//            boolean isMediaFile = Util.INSTANCE.isMediaMimeType(mimeType);
//            boolean hasPermission = activity.checkUriPermission(uri, Binder.getCallingPid(), Binder.getCallingUid(), 2) == 0;
//            IntentSender var13;
//            Companion var10000;
//            if (isMediaFile && !hasPermission && Build.VERSION.SDK_INT >= 30 && !this.isExternalStorageLegacy()) {
//                PendingIntent var11 = MediaStore.createDeleteRequest(activity.getContentResolver(), (Collection) CollectionsKt.arrayListOf(new Uri[]{uri}));
//                Intrinsics.checkExpressionValueIsNotNull(var11, "MediaStore.createDeleteR…Of(uri)\n                )");
//                PendingIntent pendingIntent = var11;
//                var10000 = HiPermission.Companion;
//                var13 = pendingIntent.getIntentSender();
//                Intrinsics.checkExpressionValueIsNotNull(var13, "pendingIntent.intentSender");
//                var10000.startIntentSenderForResult(activity, var13, (SimpleCallback)(new SimpleCallback() {
//                    public void onResult(boolean result) {
//                        callback.invoke(result);
//                    }
//                }));
//            } else {
//                int delete;
//                if (isMediaFile && Build.VERSION.SDK_INT >= 29 && !this.isExternalStorageLegacy()) {
//                    try {
//                        delete = activity.getContentResolver().delete(uri, (String)null, (String[])null);
//                        callback.invoke(delete > 0);
//                    } catch (Exception var8) {
//                        if (Build.VERSION.SDK_INT >= 29 && var8 instanceof RecoverableSecurityException) {
//                            var10000 = HiPermission.Companion;
//                            RemoteAction var10002 = ((RecoverableSecurityException)var8).getUserAction();
//                            Intrinsics.checkExpressionValueIsNotNull(var10002, "ex.userAction");
//                            PendingIntent var12 = var10002.getActionIntent();
//                            Intrinsics.checkExpressionValueIsNotNull(var12, "ex.userAction.actionIntent");
//                            var13 = var12.getIntentSender();
//                            Intrinsics.checkExpressionValueIsNotNull(var13, "ex.userAction.actionIntent.intentSender");
//                            var10000.startIntentSenderForResult(activity, var13, (SimpleCallback)(new SimpleCallback() {
//                                public void onResult(boolean result) {
//                                    callback.invoke(result);
//                                }
//                            }));
//                            return;
//                        }
//
//                        var8.printStackTrace();
//                        callback.invoke(false);
//                    }
//                } else {
//                    delete = activity.getContentResolver().delete(uri, (String)null, (String[])null);
//                    callback.invoke(delete > 0);
//                }
//            }
//
//        }
//    }
//
//    public final void createFile(@NotNull final FragmentActivity activity, @NotNull final byte[] byteArray, @NotNull String fileName, @Nullable String mimeType, @NotNull final Function1 callback) {
//        Intrinsics.checkParameterIsNotNull(activity, "activity");
//        Intrinsics.checkParameterIsNotNull(byteArray, "byteArray");
//        Intrinsics.checkParameterIsNotNull(fileName, "fileName");
//        Intrinsics.checkParameterIsNotNull(callback, "callback");
//        String var10000 = mimeType;
//        if (mimeType == null) {
//            var10000 = Util.INSTANCE.guessFileMimeType(fileName);
//        }
//
//        String fileMimeType = var10000;
//        Intent var8 = new Intent("android.intent.action.CREATE_DOCUMENT");
//        boolean var9 = false;
//        boolean var10 = false;
//        int var12 = false;
//        var8.setType(fileMimeType);
//        var8.putExtra("android.intent.extra.TITLE", fileName);
//        if (Build.VERSION.SDK_INT >= 26) {
//            var8.putExtra("android.provider.extra.INITIAL_URI", (Parcelable)Util.INSTANCE.getExtraInitUri());
//        }
//
//        HiPermission.Companion.startActivityForResult(activity, var8, (ActivityResultCallback)(new ActivityResultCallback() {
//            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//                if (resultCode == -1 && data != null && data.getData() != null) {
//                    try {
//                        HiStorage var10000 = HiStorage.INSTANCE;
//                        Context var10001 = (Context)activity;
//                        Uri var10002 = data.getData();
//                        if (var10002 == null) {
//                            Intrinsics.throwNpe();
//                        }
//
//                        Intrinsics.checkExpressionValueIsNotNull(var10002, "data.data!!");
//                        var10000.grantUriPermission(var10001, var10002);
//                        ContentResolver var15 = activity.getContentResolver();
//                        Uri var17 = data.getData();
//                        if (var17 == null) {
//                            Intrinsics.throwNpe();
//                        }
//
//                        OutputStream var16 = var15.openOutputStream(var17);
//                        if (var16 != null) {
//                            Closeable var4 = (Closeable)var16;
//                            boolean var5 = false;
//                            Throwable var6 = (Throwable)null;
//
//                            try {
//                                OutputStream it = (OutputStream)var4;
//                                int var8 = false;
//                                it.write(byteArray);
//                                it.flush();
//                                it.close();
//                                callback.invoke(data.getData());
//                                Unit var18 = Unit.INSTANCE;
//                            } catch (Throwable var12) {
//                                var6 = var12;
//                                throw var12;
//                            } finally {
//                                CloseableKt.closeFinally(var4, var6);
//                            }
//                        }
//                    } catch (Exception var14) {
//                        var14.printStackTrace();
//                        callback.invoke((Object)null);
//                    }
//                } else {
//                    callback.invoke((Object)null);
//                }
//
//            }
//        }));
//    }
//
//    public final void pickFile(@NotNull FragmentActivity activity, @Nullable String mimeType, @NotNull final Function1 callback) {
//        Intrinsics.checkParameterIsNotNull(activity, "activity");
//        Intrinsics.checkParameterIsNotNull(callback, "callback");
//        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
//        intent.addCategory("android.intent.category.OPENABLE");
//        intent.addFlags(64);
//        intent.addFlags(1);
//        intent.addFlags(2);
//        String var10001 = mimeType;
//        if (mimeType == null) {
//            var10001 = "*/*";
//        }
//
//        intent.setType(var10001);
//        if (Build.VERSION.SDK_INT >= 26) {
//            intent.putExtra("android.provider.extra.INITIAL_URI", (Parcelable)Util.INSTANCE.getExtraInitUri());
//        }
//
//        HiPermission.Companion.startActivityForResult(activity, intent, (ActivityResultCallback)(new ActivityResultCallback() {
//            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//                if (resultCode == -1 && data != null) {
//                    callback.invoke(data.getData());
//                } else {
//                    callback.invoke((Object)null);
//                }
//
//            }
//        }));
//    }
//
//    @SuppressLint({"InlinedApi"})
//    @Nullable
//    public final Uri saveDocument(Context context, @NotNull byte[] byteArray, @NotNull String fileName, @Nullable String mimeType) {
//        Intrinsics.checkParameterIsNotNull(context, "context");
//        Intrinsics.checkParameterIsNotNull(byteArray, "byteArray");
//        Intrinsics.checkParameterIsNotNull(fileName, "fileName");
//        String var10000 = mimeType;
//        if (mimeType == null) {
//            var10000 = Util.INSTANCE.guessFileMimeType(fileName);
//        }
//
//        String docMimeType = var10000;
//        Uri docExternalUri = Util.INSTANCE.guessExternalMediaUri(docMimeType);
//        if (Util.INSTANCE.isMediaMimeType(docMimeType)) {
//            throw (Throwable)(new IllegalArgumentException("Media files should be stored in a shared media directory or application private directory"));
//        } else if (this.isExternalStorageLegacy()) {
//            File externalDocDir = Util.INSTANCE.guessExternalFileDirectory(docMimeType);
//            File externalDocAppDir = new File(externalDocDir, "imooc");
//            if (!externalDocAppDir.exists()) {
//                externalDocAppDir.mkdirs();
//            }
//
//            File docFile = new File(externalDocAppDir, fileName);
//
//            try {
//                FileOutputStream outputStream = new FileOutputStream(docFile);
//                outputStream.write(byteArray);
//                outputStream.flush();
//                outputStream.close();
//            } catch (Exception var11) {
//                var11.printStackTrace();
//                docFile.delete();
//                return null;
//            }
//
//            ContentValues values = new ContentValues();
//            values.put("_data", docFile.getAbsolutePath());
//            values.put("_display_name", fileName);
//            values.put("mime_type", docMimeType);
//            return context.getContentResolver().insert(docExternalUri, values);
//        } else {
//            ContentValues values = new ContentValues();
//            values.put("_display_name", fileName);
//            values.put("mime_type", docMimeType);
//            values.put("is_pending", 1);
//            Uri var13 = context.getContentResolver().insert(DOCUMENT_EXTERNAL_URI, values);
//            if (var13 != null) {
//                Intrinsics.checkExpressionValueIsNotNull(var13, "context.contentResolver.…I, values) ?: return null");
//                Uri uri = var13;
//                OutputStream var14 = context.getContentResolver().openOutputStream(uri);
//                if (var14 != null) {
//                    Intrinsics.checkExpressionValueIsNotNull(var14, "context.contentResolver.…tream(uri) ?: return null");
//                    OutputStream openOutputStream = var14;
//                    openOutputStream.write(byteArray);
//                    openOutputStream.flush();
//                    openOutputStream.close();
//                    values.clear();
//                    values.put("is_pending", 0);
//                    context.getContentResolver().update(uri, values, (String)null, (String[])null);
//                    this.grantUriPermission(context, uri);
//                    return uri;
//                } else {
//                    return null;
//                }
//            } else {
//                return null;
//            }
//        }
//    }
//
//    // $FF: synthetic method
//    public static Uri saveDocument$default(HiStorage var0, Context var1, byte[] var2, String var3, String var4, int var5, Object var6) {
//        if ((var5 & 8) != 0) {
//            var4 = (String)null;
//        }
//
//        return var0.saveDocument(var1, var2, var3, var4);
//    }
//
//    @NotNull
//    public final List queryDocument(@NotNull Activity activity, @Nullable String displayName, @Nullable String mimeType) {
//        Intrinsics.checkParameterIsNotNull(activity, "activity");
//        boolean var5 = false;
//        List list = (List)(new ArrayList());
//        String selection = "";
//        String[] sectionArgs = new String[0];
//        String[] projection = new String[]{"_id", "_data", "_display_name", "width", "height", "mime_type"};
//        if (!TextUtils.isEmpty((CharSequence)displayName)) {
//            selection = selection + "_display_name = ? ";
//            sectionArgs = (String[])ArraysKt.plus(sectionArgs, displayName);
//        }
//
//        if (!TextUtils.isEmpty((CharSequence)mimeType)) {
//            selection = selection + "mime_type LIKE ?";
//            sectionArgs = (String[])ArraysKt.plus(sectionArgs, mimeType);
//        }
//
//        if (TextUtils.isEmpty((CharSequence)mimeType)) {
//            selection = selection + "mime_type NOT LIKE ? AND mime_type NOT LIKE ? AND mime_type NOT LIKE ?";
//            sectionArgs = (String[])ArraysKt.plus(sectionArgs, new String[]{"image%", "video%", "audio%"});
//        }
//
//        String sortOrder = "_id DESC";
//        Cursor var10000 = activity.getContentResolver().query(DOCUMENT_EXTERNAL_URI, projection, selection, sectionArgs, sortOrder);
//        if (var10000 != null) {
//            Closeable var9 = (Closeable)var10000;
//            boolean var10 = false;
//            Throwable var11 = (Throwable)null;
//
//            try {
//                Cursor it = (Cursor)var9;
//                boolean var13 = false;
//
//                while(it.moveToNext()) {
//                    int idIndex = it.getColumnIndex("_id");
//                    int pathIndex = it.getColumnIndex("_data");
//                    int displayNameIndex = it.getColumnIndex("_display_name");
//                    int widthIndex = it.getColumnIndex("width");
//                    int heightIndex = it.getColumnIndex("height");
//                    int mimeTypeIndex = it.getColumnIndex("mime_type");
//                    Uri var10002 = ContentUris.withAppendedId(DOCUMENT_EXTERNAL_URI, it.getLong(idIndex));
//                    Intrinsics.checkExpressionValueIsNotNull(var10002, "ContentUris.withAppended…URI, it.getLong(idIndex))");
//                    String var10003 = it.getString(displayNameIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10003, "it.getString(displayNameIndex)");
//                    String var10004 = it.getString(mimeTypeIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10004, "it.getString(mimeTypeIndex)");
//                    int var10005 = it.getInt(widthIndex);
//                    int var10006 = it.getInt(heightIndex);
//                    String var10007 = it.getString(pathIndex);
//                    Intrinsics.checkExpressionValueIsNotNull(var10007, "it.getString(pathIndex)");
//                    HiStorage.MediaInfo media = new HiStorage.MediaInfo(var10002, var10003, var10004, var10005, var10006, var10007);
//                    list.add(media);
//                }
//
//                it.close();
//                Unit var26 = Unit.INSTANCE;
//            } catch (Throwable var23) {
//                var11 = var23;
//                throw var23;
//            } finally {
//                CloseableKt.closeFinally(var9, var11);
//            }
//        }
//
//        return list;
//    }
//
//    // $FF: synthetic method
//    public static List queryDocument$default(HiStorage var0, Activity var1, String var2, String var3, int var4, Object var5) {
//        if ((var4 & 2) != 0) {
//            var2 = (String)null;
//        }
//
//        return var0.queryDocument(var1, var2, var3);
//    }
//
//    private final void grantUriPermission(Context activity, Uri uri) {
//        if (Build.VERSION.SDK_INT >= 30) {
//            activity.grantUriPermission(Util.INSTANCE.getPackageName(), uri, 2);
//        }
//
//    }
//
//    private final boolean isExternalStorageLegacy() {
//        return Build.VERSION.SDK_INT >= 29 ? Environment.isExternalStorageLegacy() : true;
//    }
//
//    private HiStorage() {
//    }
//
//    static {
//        HiStorage var0 = new HiStorage();
//        INSTANCE = var0;
//        Uri var10000 = MediaStore.Files.getContentUri("external");
//        Intrinsics.checkExpressionValueIsNotNull(var10000, "MediaStore.Files.getContentUri(\"external\")");
//        DOCUMENT_EXTERNAL_URI = var10000;
//    }
//
//    @Metadata(
//            mv = {1, 1, 18},
//            bv = {1, 0, 3},
//            k = 1,
//            d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u0005¢\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0018\u001a\u00020\bHÆ\u0003J\t\u0010\u0019\u001a\u00020\bHÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003JE\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001f\u001a\u00020\bHÖ\u0001J\t\u0010 \u001a\u00020\u0005HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\n\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000f¨\u0006!"},
//            d2 = {"Lcom/imooc/android/scopestorage/storage/HiStorage$MediaInfo;", "", "uri", "Landroid/net/Uri;", "displayName", "", "mineType", "width", "", "height", "path", "(Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;)V", "getDisplayName", "()Ljava/lang/String;", "getHeight", "()I", "getMineType", "getPath", "getUri", "()Landroid/net/Uri;", "getWidth", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "ScopeStorage.app"}
//    )
//    public static final class MediaInfo {
//        @NotNull
//        private final Uri uri;
//        @NotNull
//        private final String displayName;
//        @NotNull
//        private final String mineType;
//        private final int width;
//        private final int height;
//        @NotNull
//        private final String path;
//
//        @NotNull
//        public final Uri getUri() {
//            return this.uri;
//        }
//
//        @NotNull
//        public final String getDisplayName() {
//            return this.displayName;
//        }
//
//        @NotNull
//        public final String getMineType() {
//            return this.mineType;
//        }
//
//        public final int getWidth() {
//            return this.width;
//        }
//
//        public final int getHeight() {
//            return this.height;
//        }
//
//        @NotNull
//        public final String getPath() {
//            return this.path;
//        }
//
//        public MediaInfo(@NotNull Uri uri, @NotNull String displayName, @NotNull String mineType, int width, int height, @NotNull String path) {
//            Intrinsics.checkParameterIsNotNull(uri, "uri");
//            Intrinsics.checkParameterIsNotNull(displayName, "displayName");
//            Intrinsics.checkParameterIsNotNull(mineType, "mineType");
//            Intrinsics.checkParameterIsNotNull(path, "path");
//            super();
//            this.uri = uri;
//            this.displayName = displayName;
//            this.mineType = mineType;
//            this.width = width;
//            this.height = height;
//            this.path = path;
//        }
//
//        @NotNull
//        public final Uri component1() {
//            return this.uri;
//        }
//
//        @NotNull
//        public final String component2() {
//            return this.displayName;
//        }
//
//        @NotNull
//        public final String component3() {
//            return this.mineType;
//        }
//
//        public final int component4() {
//            return this.width;
//        }
//
//        public final int component5() {
//            return this.height;
//        }
//
//        @NotNull
//        public final String component6() {
//            return this.path;
//        }
//
//        @NotNull
//        public final HiStorage.MediaInfo copy(@NotNull Uri uri, @NotNull String displayName, @NotNull String mineType, int width, int height, @NotNull String path) {
//            Intrinsics.checkParameterIsNotNull(uri, "uri");
//            Intrinsics.checkParameterIsNotNull(displayName, "displayName");
//            Intrinsics.checkParameterIsNotNull(mineType, "mineType");
//            Intrinsics.checkParameterIsNotNull(path, "path");
//            return new HiStorage.MediaInfo(uri, displayName, mineType, width, height, path);
//        }
//
//        // $FF: synthetic method
//        public static HiStorage.MediaInfo copy$default(HiStorage.MediaInfo var0, Uri var1, String var2, String var3, int var4, int var5, String var6, int var7, Object var8) {
//            if ((var7 & 1) != 0) {
//                var1 = var0.uri;
//            }
//
//            if ((var7 & 2) != 0) {
//                var2 = var0.displayName;
//            }
//
//            if ((var7 & 4) != 0) {
//                var3 = var0.mineType;
//            }
//
//            if ((var7 & 8) != 0) {
//                var4 = var0.width;
//            }
//
//            if ((var7 & 16) != 0) {
//                var5 = var0.height;
//            }
//
//            if ((var7 & 32) != 0) {
//                var6 = var0.path;
//            }
//
//            return var0.copy(var1, var2, var3, var4, var5, var6);
//        }
//
//        @NotNull
//        public String toString() {
//            return "MediaInfo(uri=" + this.uri + ", displayName=" + this.displayName + ", mineType=" + this.mineType + ", width=" + this.width + ", height=" + this.height + ", path=" + this.path + ")";
//        }
//
//        public int hashCode() {
//            Uri var10000 = this.uri;
//            int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
//            String var10001 = this.displayName;
//            var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
//            var10001 = this.mineType;
//            var1 = (((var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31 + Integer.hashCode(this.width)) * 31 + Integer.hashCode(this.height)) * 31;
//            var10001 = this.path;
//            return var1 + (var10001 != null ? var10001.hashCode() : 0);
//        }
//
//        public boolean equals(@Nullable Object var1) {
//            if (this != var1) {
//                if (var1 instanceof HiStorage.MediaInfo) {
//                    HiStorage.MediaInfo var2 = (HiStorage.MediaInfo)var1;
//                    if (Intrinsics.areEqual(this.uri, var2.uri) && Intrinsics.areEqual(this.displayName, var2.displayName) && Intrinsics.areEqual(this.mineType, var2.mineType) && this.width == var2.width && this.height == var2.height && Intrinsics.areEqual(this.path, var2.path)) {
//                        return true;
//                    }
//                }
//
//                return false;
//            } else {
//                return true;
//            }
//        }
//    }
//}
