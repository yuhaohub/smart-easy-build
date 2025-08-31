package com.yuhao.smarteasybuild.service;


public interface ScreenshotService {
   /**
    * 截屏并上传
    * @param webUrl
    * @return
    */
   String uploadScreenshot(String webUrl);
}
