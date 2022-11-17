package life.forever.cf.activtiy;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;


public class FileUtils {
    public static final String SUFFIX_NB = ".nb";
    public static final String SUFFIX_TXT = ".txt";
    public static final String SUFFIX_EPUB = ".epub";
    public static final String SUFFIX_PDF = ".pdf";


    public static File getFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //获取文件
    public static synchronized File getFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                //创建父类文件夹
                getFolder(file.getParent());
                //创建文件
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();



        }
        return file;
    }

    //获取Cache文件夹
    public static String getCachePath() {
        if (isSdCardExist()) {
            return PlotRead.getContext()
                    .getExternalCacheDir()
                    .getAbsolutePath();
        } else {
            return PlotRead.getContext()
                    .getCacheDir()
                    .getAbsolutePath();
        }
    }

    //获取Cache文件夹
    public static String getNewCachePath() {
        if (isSdCardExist()) {
            try {
                return PlotRead.getContext()
                        .getExternalCacheDir()
                        .getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
                return PlotRead.getContext()
                        .getCacheDir()
                        .getAbsolutePath();
            }
        } else {
            return PlotRead.getContext()
                    .getCacheDir()
                    .getAbsolutePath();
        }
    }

    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 本来是获取File的内容的。但是为了解决文本缩进、换行的问题
     * 这个方法就是专门用来获取书籍的...
     * <p>
     * 应该放在BookRepository中。。。
     *
     * @param file
     * @return
     */
    public static String getFileContent(File file) {
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                //过滤空语句
                if (!str.equals("")) {
                    //由于sb会自动过滤\n,所以需要加上去
                    sb.append("    " + str + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return sb.toString();
    }

    //判断是否挂载了SD卡
    public static boolean isSdCardExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    //递归删除文件夹下的数据
    public static synchronized void deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                String path = subFile.getPath();
                deleteFile(path);
            }
        }
        //删除文件
        file.delete();
    }

    //由于递归的耗时问题，取巧只遍历内部三层

    //获取txt文件
    public static List<File> getTxtFiles(String filePath, int layer) {
        List txtFiles = new ArrayList();
        File file = new File(filePath);

        //如果层级为 3，则直接返回
        if (layer == 3) {
            return txtFiles;
        }

        //获取文件夹
        File[] dirs = file.listFiles(
                pathname -> {
                    if (pathname.isDirectory() && !pathname.getName().startsWith(".")) {
                        return true;
                    }
                    //获取txt文件
                    else if (pathname.getName().endsWith(".txt")) {
                        txtFiles.add(pathname);
                        return false;
                    } else {
                        return false;
                    }
                }
        );
        //遍历文件夹
        for (File dir : dirs) {
            //递归遍历txt文件
            txtFiles.addAll(getTxtFiles(dir.getPath(), layer + 1));
        }
        return txtFiles;
    }

    //由于遍历比较耗时
    public static Single<List<File>> getSDTxtFile() {
        //外部存储卡路径
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        return Single.create(new SingleOnSubscribe<List<File>>() {
            @Override
            public void subscribe(SingleEmitter<List<File>> e) throws Exception {
                List<File> files = getTxtFiles(rootPath, 0);
                e.onSuccess(files);
            }
        });
    }

    //获取文件的编码格式
    public static Charset getCharset(String fileName) {
        BufferedInputStream bis = null;
        Charset charset = Charset.GBK;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = Charset.UTF8;
                checked = true;
            }
            /*
             * 不支持 UTF16LE 和 UTF16BE
            else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = Charset.UTF16LE;
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = Charset.UTF16BE;
                checked = true;
            } else */

            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = Charset.UTF8;
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bis);
        }
        return charset;
    }


    //储存内容
    // 将字符串写入到文本文件中
    public static void writeJsonToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                LogUtils.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();

            }
            file.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            raf.seek(file.length());
//            raf.setLength(0);
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            LogUtils.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            LogUtils.i("error:", e + "");
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getJsonFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("json")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (FileNotFoundException e) {
                    LogUtils.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    LogUtils.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }

    //写入文件
    public static void writeBookContentToFile(String strcontent, String bookID,String chapterID,String sourceID) {
        //生成文件夹之后，再生成文件，不然会出错
        String filePath = FileUtils.getCachePath()+File.separator
                + "book_cache"+ File.separator ;

        String fileName = chapterID+"_"+chapterID  + ".txt";

        filePath = filePath+bookID+"/"+sourceID+ File.separator;

        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                LogUtils.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();

            }
            file.createNewFile();


            strContent = Bus.Base64Encode(strContent);

            FileOutputStream fos = new FileOutputStream(strFilePath);
            fos.write(strContent.getBytes());
            fos.flush();
            fos.close();

        } catch (Exception e) {
            LogUtils.e("TestFile", "Error on write File:" + e);
        }

    }

    public static boolean isExsitBookContentFile( String bookID,String chapterID,String sourceID)
    {
        String filePath = FileUtils.getCachePath()+File.separator
                + "book_cache"+ File.separator ;

        String fileName = chapterID+"_"+chapterID + ".txt";

        filePath = filePath+bookID+"/"+sourceID+ File.separator;

        String strFilePath = filePath + fileName;

        File file = new File(strFilePath);


        return file.exists();
    }
    //写入文件
    public static String getBookContentToFile(String bookID,String chapterID,String sourceID) {
        //生成文件夹之后，再生成文件，不然会出错
        String filePath = FileUtils.getCachePath() + File.separator
                + "book_cache" + File.separator;

        String fileName = chapterID + "_" + chapterID + ".txt";

        filePath = filePath + bookID + "/" + sourceID + File.separator;

        String strFilePath = filePath + fileName;

        String returnContent = null;
        File file = new File(strFilePath);
        if(file.exists())
        {
            //打开文件输入流
            FileInputStream inputStream = null;
            StringBuilder sb = new StringBuilder("");
            try {
                inputStream = new FileInputStream(strFilePath);
                byte[] buffer = new byte[1024];
                int len = inputStream.read(buffer);
                //读取文件内容
                while(len > 0){
                    sb.append(new String(buffer,0,len));

                    //继续将数据放到buffer中
                    len = inputStream.read(buffer);
                }
                //关闭输入流
                inputStream.close();
                returnContent =  sb.toString();

                returnContent = Bus.Base64Decode(returnContent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                return returnContent;
            }
        }else {
            return null;
        }
    }


    public static String getFileContentFromButterReadFile(File bufferFile)
    {
        String lastStr = null;
        if(bufferFile.exists())
        {
            Reader reader = null;
            try {
                reader = new FileReader(bufferFile);
                BufferedReader in = new BufferedReader(reader);
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null){
                    buffer.append(line);
                }
                lastStr =  buffer.toString();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
            }
        }
        return lastStr;
    }



    /**
     * 根据保存zip的文件路径和zip文件相对路径名，返回一个实际的文件
     * 因为zip文件解压后，里边可能是多重文件结构
     * */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length == 1){
            substr = dirs[0];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        }
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        }
        return ret;
    }

    /**
     * zipFile 压缩文件
     * folderPath 解压后的文件路径
     * */
    public static void unZipFile(String zipFile, String folderPath) {
        try {
            File outputFloder = new File(folderPath);
            if(!outputFloder.exists()){
                outputFloder.mkdirs();//如果路径不存在就先创建路径
            }
            File file = new File(zipFile);
            ZipFile zfile = new ZipFile(file);
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();

                if (ze.isDirectory()) {
                    String dirstr = folderPath + File.separator + ze.getName();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    File f = new File(dirstr);
                    boolean mdir = f.mkdir();
                    continue;
                }

                OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                Log.e("unZip:", "---->getRealFileName(folderPath,ze.getName()): " + getRealFileName(folderPath, ze.getName()).getPath() + "  name:" + getRealFileName(folderPath, ze.getName()).getName());
                InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
                int readLen = 0;
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();

        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show("资源解压失败，请重试");
        }

        //解压完成之后删除压缩包
        deleteFile(zipFile);
    }
}
