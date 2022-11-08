package com.tony.reggie_take_out.controller;

import cn.hutool.http.HttpUtil;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.untils.QiniuUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${qiniu.path}")
    private String basePath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String suff = originalFilename.substring(index);
            String fileName = UUID.randomUUID() + suff;
            QiniuUtils.upload2Qiniu(file.getBytes(), fileName);
            return Result.success(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    //http://rl1b2mrrg.hn-bkt.clouddn.com/f5def44d-5fc2-4381-9137-cc23713bab6c.jpg
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        //从七牛云下载文件
        byte[] fileBytes = HttpUtil.downloadBytes(basePath + name);

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            outputStream.write(fileBytes);

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
