package ru.aakumykov.me.mvp.utils.MVPUtils;

public class FileInfo {

    private Integer width;
    private Integer height;
    private String fileExtension;


    FileInfo(int width, int height, String fileExtension) {
        this.fileExtension = fileExtension;
        new FileInfo(width, height);
    }

    FileInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getFileExtension() {
        return fileExtension;
    }
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
