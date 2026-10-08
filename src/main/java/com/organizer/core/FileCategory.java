package com.organizer.core;

import java.util.Set;

public enum FileCategory {
    IMAGES("图片", Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tiff", "raw", "heic")),
    DOCUMENTS("文档", Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "rtf", "odt", "csv", "epub")),
    VIDEOS("视频", Set.of("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "mpeg", "mpg")),
    AUDIO("音频", Set.of("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "ape")),
    ARCHIVES("压缩包", Set.of("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso")),
    CODE("代码", Set.of("java", "py", "c", "cpp", "h", "hpp", "js", "ts", "html", "css", "php", "go", "rs", "kt", "scala", "sh", "bat", "sql", "json", "xml", "yml", "yaml")),
    PROGRAMS("程序", Set.of("exe", "msi", "apk", "dmg", "pkg", "deb", "rpm")),
    OTHERS("其他", Set.of());

    private final String label;
    private final Set<String> extensions;

    FileCategory(String label, Set<String> extensions) {
        this.label = label;
        this.extensions = extensions;
    }

    public String getLabel() {
        return label;
    }

    public Set<String> getExtensions() {
        return extensions;
    }
}
