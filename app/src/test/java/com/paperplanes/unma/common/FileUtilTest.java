package com.paperplanes.unma.common;

import com.paperplanes.unma.R;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by abdularis on 20/11/17.
 */

public class FileUtilTest {

//    @Test
//    public void fileUtil_getFormatedFileSize() {
//        long s500_bytes = 500;
//        long s2_kb = 2 * 1024;
//        long s1_5_mb = (long) (1.5 * 1024 * 1024);
//        long s100_mb = 100 * 1024 * 1024;
//        long s1_gb = 1024 * 1024 * 1024;
//
//        assertThat(FileUtil.getFormattedFileSize(s500_bytes), equalTo("500B"));
//        assertThat(FileUtil.getFormattedFileSize(s2_kb), equalTo("2KB"));
//        assertThat(FileUtil.getFormattedFileSize(s1_5_mb), equalTo("1.5MB"));
//        assertThat(FileUtil.getFormattedFileSize(s100_mb), equalTo("100MB"));
//        assertThat(FileUtil.getFormattedFileSize(s1_gb), equalTo("1GB"));
//    }

    @Test
    public void fileUtil_getDrawableResourceForFileExt() {
        String fPdf = "/home/user/.folder/important_document.pdf";
        String fWord = "/home/user/.folder/important_document.doc";
        String fWord2 = "/home/user/.folder/important_document.docx";
        String fExcel = "/home/user/.folder/important_report.xls";
        String fExcel2 = "/home/user/.folder/important_report.xlsx";
        String fPres = "/home/user/.folder/important_presentation.ppt";
        String fPres2 = "/home/user/.folder/important_presentation.pptx";
        String fImg = "/home/user/.folder/important_image.png";
        String fImg2 = "/home/user/.folder/important_image.jpg";
        String fImg3 = "/home/user/.folder/important_image.jpeg";
        String fImg4 = "/home/user/.folder/important_image.bmp";
        String fUnk = "/home/user/.folder/executable.exe";
        String fUnk2 = "/home/user/.folder/archive.rar";

        assertThat(FileUtil.getDrawableResourceForFileExt(fPdf), equalTo(R.drawable.ic_file_pdf));

        assertThat(FileUtil.getDrawableResourceForFileExt(fWord), equalTo(R.drawable.ic_file_word));
        assertThat(FileUtil.getDrawableResourceForFileExt(fWord2), equalTo(R.drawable.ic_file_word));

        assertThat(FileUtil.getDrawableResourceForFileExt(fExcel), equalTo(R.drawable.ic_file_spreadsheet));
        assertThat(FileUtil.getDrawableResourceForFileExt(fExcel2), equalTo(R.drawable.ic_file_spreadsheet));

        assertThat(FileUtil.getDrawableResourceForFileExt(fPres), equalTo(R.drawable.ic_file_presentation));
        assertThat(FileUtil.getDrawableResourceForFileExt(fPres2), equalTo(R.drawable.ic_file_presentation));

        assertThat(FileUtil.getDrawableResourceForFileExt(fImg), equalTo(R.drawable.ic_file_image));
        assertThat(FileUtil.getDrawableResourceForFileExt(fImg2), equalTo(R.drawable.ic_file_image));
        assertThat(FileUtil.getDrawableResourceForFileExt(fImg3), equalTo(R.drawable.ic_file_image));
        assertThat(FileUtil.getDrawableResourceForFileExt(fImg4), equalTo(R.drawable.ic_file_image));

        assertThat(FileUtil.getDrawableResourceForFileExt(fUnk), equalTo(R.drawable.ic_file_unknown));
        assertThat(FileUtil.getDrawableResourceForFileExt(fUnk2), equalTo(R.drawable.ic_file_unknown));
    }

}
