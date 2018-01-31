package schoolmate.view.element;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class MyFileFilter extends FileFilter {
    String[] ext;
    public MyFileFilter(String[] ext) {
        this.ext = ext;
    }

    /* 在accept()方法中,当程序所抓到的是一个目录而不是文件时,我们返回true值,表示将此目录显示出来. */
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            // 表示文件名称不为".xxx"现"xxx."之类型
            String extension = fileName.substring(index + 1).toLowerCase();
            // 若所抓到的文件扩展名等于我们所设置要显示的扩展名(即变量ext值),则返回true,表示将此文件显示出来,否则返回
            // true.
            for(int i=0;i<ext.length;i++)
	            if (extension.equals(ext[i]))
	                return true;
        }
        return false;
    }
    public String getDescription() {
    	for(int i=0;i<ext.length;i++)
	        if (ext[i].equals("db"))
	            return "数据库(*.db)";
	        else if(ext[i].equals("xls")||ext[i].equals("xlsx"))
	        	return "Excel(*.xls,*.xlsx)";
        return "";
    }
}
