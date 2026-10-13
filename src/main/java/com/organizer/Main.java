package com.organizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.organizer.core.FileOrganizer;
import com.organizer.core.OrganizePreview;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("用法: java com.organizer.app.Main <目录> [true|false]");
            System.out.println("示例: java com.organizer.app.Main D:/Downloads true");
            return;
        }

        Path root = Paths.get(args[0]);
        boolean recursive = args.length >= 2 && Boolean.parseBoolean(args[1]);

        try {
            FileOrganizer organizer = new FileOrganizer(root, recursive);
            OrganizePreview preview = organizer.preview();

            System.out.println(preview.toPreviewText());

            if (preview.getTotalCount() == 0) {
                System.out.println("没有需要整理的文件。");
                return;
            }

            System.out.print("确认执行移动？输入 y 继续：");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();

            if (!input.equalsIgnoreCase("y")) {
                System.out.println("已取消。");
                scanner.close();
                return;
            }

            int moved = organizer.execute(preview);
            System.out.println("完成，共移动 " + moved + " 个文件。");
            scanner.close();
        } catch (Exception e) {
            System.err.println("执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}