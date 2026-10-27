package com.organizer.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;

import com.organizer.core.FileOrganizer;
import com.organizer.core.OrganizePreview;
import com.organizer.log.Loggers;

public final class CliApp {
    private static final Logger LOGGER = Loggers.getLogger(CliApp.class);

    private CliApp() {
    }

    public static void main(String[] args) {
        try {
            CliOptions options = CliOptions.parse(args);
            Path root = options.root();
            boolean recursive = options.recursive();

            LOGGER.info("开始整理目录: {}，递归模式: {}", root, recursive);
            System.out.println("正在扫描目录: " + root.toAbsolutePath());

            FileOrganizer organizer = new FileOrganizer(root, recursive);
            OrganizePreview preview = organizer.preview();
            System.out.println(preview.toPreviewText());

            if (preview.getTotalCount() == 0) {
                LOGGER.info("目录中没有需要整理的文件: {}", root);
                System.out.println("没有需要整理的文件。");
                return;
            }

            System.out.print("确认执行移动？输入 y 继续：");
            try (Scanner scanner = new Scanner(System.in)) {
                String input = scanner.nextLine().trim();
                if (!"y".equalsIgnoreCase(input)) {
                    LOGGER.info("用户取消整理，目录: {}", root);
                    System.out.println("已取消。");
                    return;
                }
            }

            int moved = organizer.execute(preview);
            LOGGER.info("整理完成，共移动 {} 个文件，目录: {}", moved, root);
            System.out.println("完成，共移动 " + moved + " 个文件。");
        } catch (IllegalArgumentException e) {
            LOGGER.error("命令参数错误: {}", e.getMessage(), e);
            printUsage();
        } catch (Exception e) {
            LOGGER.error("执行失败: {}", e.getMessage(), e);
            System.err.println("执行失败: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("用法: java com.organizer.Main <目录> [true|false]");
        System.out.println("示例: java com.organizer.Main D:/Downloads true");
    }

    public record CliOptions(Path root, boolean recursive) {
        public static CliOptions parse(String[] args) {
            if (args == null || args.length < 1) {
                throw new IllegalArgumentException("至少需要传入目标目录");
            }

            Path root = Paths.get(args[0]);
            boolean recursive = args.length >= 2 && Boolean.parseBoolean(args[1]);
            return new CliOptions(root, recursive);
        }
    }
}
