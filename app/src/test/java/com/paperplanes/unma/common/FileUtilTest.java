package com.paperplanes.unma.common;

import com.paperplanes.unma.R;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by abdularis on 20/11/17.
 */

public class FileUtilTest {

    @Test
    public void getFormattedFileSize_test() {
        long s500_bytes = 500;
        long s2_kb = 2 * 1024;
        long s1_5_mb = (long) (1.5 * 1024 * 1024);
        long s100_mb = 100 * 1024 * 1024;
        long s1_gb = 1024 * 1024 * 1024;

        assertThat(FileUtil.getFormattedFileSize(s500_bytes), equalTo("500.0B"));
        assertThat(FileUtil.getFormattedFileSize(s2_kb), equalTo("2.0KB"));
        assertThat(FileUtil.getFormattedFileSize(s1_5_mb), equalTo("1.5MB"));
        assertThat(FileUtil.getFormattedFileSize(s100_mb), equalTo("100.0MB"));
        assertThat(FileUtil.getFormattedFileSize(s1_gb), equalTo("1.0GB"));
    }

    @Test
    public void getDrawableResourceForFileExt_imgFileTest() {
        String fImg = "/home/user/.folder/important_image.png";
        String fImg2 = "/home/user/.folder/important_image.jpg";
        String fImg4 = "/home/user/.folder/important_image.bmp";

        assertThat(FileUtil.getDrawableResourceForFileExt(fImg), equalTo(R.drawable.ic_file_image));
        assertThat(FileUtil.getDrawableResourceForFileExt(fImg2), equalTo(R.drawable.ic_file_image));
        assertThat(FileUtil.getDrawableResourceForFileExt(fImg4), equalTo(R.drawable.ic_file_image));
    }

    @Test
    public void getDrawableResourceForFileExt_docFileTest() {
        String fPdf = "/home/user/.folder/important_document.pdf";
        String fWord = "/home/user/.folder/important_document.doc";
        String fWord2 = "/home/user/.folder/important_document.docx";

        assertThat(FileUtil.getDrawableResourceForFileExt(fPdf), equalTo(R.drawable.ic_file_pdf));

        assertThat(FileUtil.getDrawableResourceForFileExt(fWord), equalTo(R.drawable.ic_file_word));
        assertThat(FileUtil.getDrawableResourceForFileExt(fWord2), equalTo(R.drawable.ic_file_word));
    }

    @Test
    public void getDrawableResourceForFileExt_spreadsheetFileTest() {
        String fExcel = "/home/user/.folder/important_report.xls";
        String fExcel2 = "/home/user/.folder/important_report.xlsx";

        assertThat(FileUtil.getDrawableResourceForFileExt(fExcel), equalTo(R.drawable.ic_file_spreadsheet));
        assertThat(FileUtil.getDrawableResourceForFileExt(fExcel2), equalTo(R.drawable.ic_file_spreadsheet));
    }

    @Test
    public void getDrawableResourceForFileExt_presentationFileTest() {
        String fPres = "/home/user/.folder/important_presentation.ppt";
        String fPres2 = "/home/user/.folder/important_presentation.pptx";

        assertThat(FileUtil.getDrawableResourceForFileExt(fPres), equalTo(R.drawable.ic_file_presentation));
        assertThat(FileUtil.getDrawableResourceForFileExt(fPres2), equalTo(R.drawable.ic_file_presentation));
    }

    @Test
    public void getDrawableResourceForFileExt_otherFileTest() {
        String fUnk = "/home/user/.folder/executable.exe";
        String fUnk2 = "/home/user/.folder/archive.rar";

        assertThat(FileUtil.getDrawableResourceForFileExt(fUnk), equalTo(R.drawable.ic_file_unknown));
        assertThat(FileUtil.getDrawableResourceForFileExt(fUnk2), equalTo(R.drawable.ic_file_unknown));
    }

    @Test
    public void getFileExtension_validNameTest() {
        String f1 = "filenam.jPg";
        String f2 = "file.name.jpeg";
        String f3 = "filename.image.Pdf";
        String f4 = "file.name.PDF";

        assertThat(FileUtil.getFileExtension(f1), equalTo("JPG"));
        assertThat(FileUtil.getFileExtension(f2), equalTo("JPEG"));
        assertThat(FileUtil.getFileExtension(f3), equalTo("PDF"));
        assertThat(FileUtil.getFileExtension(f4), equalTo("PDF"));

    }

    @Test
    public void getFileExtension_invalidNameTest() {
        String f1 = "filename";
        assertThat(FileUtil.getFileExtension(f1), equalTo("Unknown"));
    }
}
