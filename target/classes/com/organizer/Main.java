package com.organizer;

import com.organizer.core.FileOrganizer;
import com.organizer.core.OrganizePreview;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String dir = ".";
        boolean recursive = false;
        boolean execute = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dir":
                    if (i + 1 < args.length) {
                        dir = args[++i];
                    } else {
                        System.err.println("错误: --dir 需要一个目录参数");
                        printUsage();
                        return;
                    }
                    break;
                case "--recursive":
                    recursive = true;
                    break;
                case "--execute":
                    execute = true;
                    break;
                case "--help":
                case "-h":
                    printUsage();
                    return;
                default:
                    System.err.println("未知参数: " + args[i]);
                    printUsage();
                    return;
            }
        }

        try {
            Path root = Paths.get(dir);
            FileOrganizer organizer = new FileOrganizer(root, recursive);
            OrganizePreview preview = organizer.preview();

            System.out.println(preview.toPreviewText());

            if (execute) {
                if (preview.getTotalCount() == 0) {
                    System.out.println("没有需要移动的文件。");
                    return;
                }
                int moved = organizer.execute(preview);
                System.out.println("整理完成，共移动 " + moved + " 个文件。");
            } else {
                System.out.println("以上为预览，未移动任何文件。");
                System.out.println("确认无误后加 --execute 参数执行整理。");
            }
        } catch (Exception e) {
            System.err.println("整理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("用法: java -jar file-organizer.jar [--dir <目录>] [--recursive] [--execute]");
        System.out.println("  --dir <目录>   指定要整理的文件夹，默认当前目录");
        System.out.println("  --recursive    递归处理子目录");
        System.out.println("  --execute      真正执行移动（默认只预览）");
        System.out.println("  --help         显示帮助");
    }
}