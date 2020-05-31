package com.sandy.courier.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.util.Strings;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * @Description: @createTime：2020/5/20 18:47
 * @author: chengyu3
 **/
public class FileUtil {

    public static <T> T readLines(String path, LineProcessor<T> processor) throws IOException {
        Preconditions.checkArgument(Strings.isNotBlank(path));
        Preconditions.checkNotNull(path);
        File file = new File(path);
        BufferedReader bufferedReader = Files.newReader(file, Charsets.UTF_8);
        return CharStreams.readLines(bufferedReader, processor);
    }

    public static void writeToFile(File file, String line, boolean append) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(Strings.isNotBlank(line));
        Preconditions.checkArgument(!file.isDirectory(), "file is dir:" + file.getName());
        if (append) {
            Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND).write(line);
        } else {
            Files.asCharSink(file, Charsets.UTF_8).write(line);
        }
    }

}
