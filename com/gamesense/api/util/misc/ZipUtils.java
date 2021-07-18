// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.File;

public final class ZipUtils
{
    public static void zip(final File source, final File dest) {
        final List<String> list = new ArrayList<String>();
        createFileList(source, source, list);
        try {
            final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));
            for (final String file : list) {
                final ZipEntry ze = new ZipEntry(file);
                final FileInputStream in = new FileInputStream(file);
                final byte[] buffer = new byte[1024];
                zos.putNextEntry(ze);
                while (true) {
                    final int len = in.read(buffer);
                    if (len <= 0) {
                        break;
                    }
                    zos.write(buffer, 0, len);
                }
                in.close();
                zos.closeEntry();
            }
            zos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    private static void createFileList(final File file, final File source, final List<String> list) {
        if (file.isFile()) {
            list.add(file.getPath());
        }
        else if (file.isDirectory()) {
            for (final String subfile : file.list()) {
                createFileList(new File(file, subfile), source, list);
            }
        }
    }
}
