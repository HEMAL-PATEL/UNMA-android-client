package com.paperplanes.unma.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.paperplanes.unma.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 19/11/17.
 */

public final class FileUtil {

    private static List<String> sImageExts = new ArrayList<>();
    private static List<String> sWordExts = new ArrayList<>();
    private static List<String> sExcelExts = new ArrayList<>();
    private static List<String> sPresentExts = new ArrayList<>();
    private static String sPdfExt = ".pdf";

    static {
        sImageExts.add(".png");
        sImageExts.add(".jpg");
        sImageExts.add(".bmp");

        sWordExts.add(".doc");
        sWordExts.add(".docx");

        sExcelExts.add(".xls");
        sExcelExts.add(".xlsx");

        sPresentExts.add(".ppt");
        sPresentExts.add(".pptx");
    }

    public static int getDrawableResourceForFileExt(String filename) {
        int extIdx = filename.lastIndexOf(".");
        if (extIdx >= 0) {
            String ext = filename.substring(extIdx).toLowerCase();
            if (sImageExts.contains(ext)) {
                return R.drawable.ic_file_image;
            }
            else if (sWordExts.contains(ext)) {
                return R.drawable.ic_file_word;
            }
            else if (sExcelExts.contains(ext)) {
                return R.drawable.ic_file_spreadsheet;
            }
            else if (sPresentExts.contains(ext)){
                return R.drawable.ic_file_presentation;
            }
            else if (sPdfExt.equalsIgnoreCase(ext)) {
                return R.drawable.ic_file_pdf;
            }
        }

        return R.drawable.ic_file_unknown;
    }

    public static String getFileExtension(String filename) {
        int extIdx = filename.lastIndexOf(".");
        if (extIdx >= 0) {
            return filename.substring(extIdx).replaceAll("\\.", "").toUpperCase();
        }

        return "Unknown";
    }

    public static String getFormattedFileSize(long size) {
        int count = 0;
        double fileSize = (double)size;
        while (fileSize >= 1024) {
            fileSize /= 1024;
            count++;
        }

        String suffix = "";
        switch (count) {
            case 0 : suffix = "B"; break;
            case 1 : suffix = "KB"; break;
            case 2 : suffix = "MB"; break;
            case 3 : suffix = "GB"; break;
            case 4 : suffix = "TB"; break;
        }

        DecimalFormat dec = new DecimalFormat("0.0");
        return dec.format(fileSize).concat(suffix);
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadOnly() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean isExists(String filepath) {
        return new File(filepath).exists();
    }

    public static void viewFile(Context context, String filepath, String mime) {
        if (filepath == null) return;

        if (!isExists(filepath)) {
            Toast.makeText(context, "File " + filepath + " doesn't exist", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Uri uri = Uri.parse("file://" + filepath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);

        Intent chooser = Intent.createChooser(intent, "Select app to open this file");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }
}
