
package com.decent.game.manager.user.controller;

import com.decent.game.common.entity.MessageBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;


/**
 * 图片上传与查询
 * 注意:代码中的MessageBean为自定义的一个实体读者可以根据需要自行定义一个
 */
@RestController
@RequestMapping("TestController")
public class TestController {
    /**
     * 上传
     * @param request  请求
     * @return
     */
    //图片上传方法2:借助spring封装后的方法，需要在dispatcher配置文件中配置CommonsMultipartResolver
    @RequestMapping("uploadImg")
    public MessageBean uploadLicenseImg(MultipartRequest request)  {
        try {
            String dirName = Paths.get("D:","userLisence").toString();
            File dir=new File(dirName);
            if(!dir.exists())dir.mkdir();
            //允许上传的文件魔术数字
            HashMap<String, String> magicNumber = new HashMap<>();
            magicNumber.put("jpg", "ffd8");
            magicNumber.put("png", "8950");
            magicNumber.put("bmp", "424d");
            magicNumber.put("gif", "4749");
            MultipartFile businessLicenceImage = request.getFile("imgFile");
            if(businessLicenceImage.isEmpty())return MessageBean.success();
            String fileExt =  businessLicenceImage.getOriginalFilename().substring( businessLicenceImage.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.asList(("gif,jpg,png,bmp").split(",")).contains(fileExt)) {
                return MessageBean.fail("上传文件扩展名是不允许的扩展名只允许gif,jpg,png,bmp 格式。");
            }
            long maxSize = 1000000;//字节，限制最大1M
            //检查文件大小
            if (businessLicenceImage.getSize() > maxSize) {
                return MessageBean.fail("上传文件大小超过限制。");
            }
            byte[] bt = new byte[2];
            int read = businessLicenceImage.getInputStream().read(bt);
            if (!StringUtils.equals(magicNumber.get(fileExt), bytesToHexString(bt))) {
                return MessageBean.fail("扩展名与文件不符!");
            }
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(businessLicenceImage.getInputStream());
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
            if (!imageReaders.hasNext()) {
                return MessageBean.fail("只能上传图片文件");
            }
            String child = StringUtils.replace(UUID.randomUUID().toString(), "-", "") + "." + fileExt;
            String  fullFileName=dirName+File.separator+child;//返回具体路径
            File uploadedFile = new File(dirName , child);
            businessLicenceImage.transferTo(uploadedFile);
            return new MessageBean(true,fullFileName);
        } catch (Exception e) {
            return MessageBean.fail("上传文件失败！");
        }
    }
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }




    /**
     * 请求图片
     * @param response              响应流
     * @param filePath                图片路径
     */
    @RequestMapping("queryPicture")
    public void queryPicture(HttpServletResponse response,String filePath) {
        ServletOutputStream outputStream=null;
        try {
            File file=new File(filePath);
            if(!file.exists())return;
            String fileExtension = StringUtils.substringAfter(file.getName(), ".");
            if(!Arrays.asList(ImageIO.getWriterFormatNames()).contains(fileExtension))return;//检查该文件是否支持
            RenderedImage read = ImageIO.read(file);
            outputStream = response.getOutputStream();
            ImageIO.write(read,fileExtension, outputStream);
            outputStream.flush();
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }


//
//    //图片上传方法1:不借助Spring，需要在dispatcher配置文件中去除CommonsMultipartResolver配置
//    //否则再次处理无法取到文件
//    @RequestMapping("pictureDemo")
//    public MessageBean pictureDemo(HttpServletRequest request) throws FileUploadException, IOException {
//        String fileNames = "D:/IMGS/" ;
//        File dir=new File(fileNames);
//        if(!dir.exists())dir.mkdir();
//        //允许上传的文件魔术数字
//        HashMap<String, String> magicNumber = new HashMap<>();
//        magicNumber.put("jpg", "ffd8");
//        magicNumber.put("png", "8950");
//        magicNumber.put("bmp", "424d");
//        magicNumber.put("gif", "4749");
//        long maxSize = 1000000;
//        FileItemFactory factory = new DiskFileItemFactory();
//        ServletFileUpload upload = new ServletFileUpload(factory);
//        upload.setHeaderEncoding("UTF-8");
//        List items = upload.parseRequest(request);
//        for (Object item1 : items) {
//            FileItem item = (FileItem) item1;
//            String fileName = item.getName();
//            if (!item.isFormField()) {
//                //检查文件大小
//                if (item.getSize() > maxSize) {
//                    return MessageBean.fail("上传文件大小超过限制。");
//                }
//                //检查扩展名
//                String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//                if (!Arrays.asList(("gif,jpg,png,bmp").split(",")).contains(fileExt)) {
//                    return MessageBean.fail("上传文件扩展名是不允许的扩展名只允许gif,jpg,png,bmp 格式。");
//                }
//                byte[] bt = new byte[2];
//                item.getInputStream().read(bt);
//                if (!StringUtils.equals(magicNumber.get(fileExt), bytesToHexString(bt))) {
//                    return MessageBean.fail("扩展名与文件不符!");
//                }
//                ImageInputStream imageInputStream = ImageIO.createImageInputStream(item.getInputStream());
//                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
//                if (!imageReaders.hasNext()) {
//                    return MessageBean.fail("只能上传图片文件");
//                }
//                try {
//                    File uploadedFile = new File(fileNames , "GA01." + fileExt);
//                    item.write(uploadedFile);
//                } catch (Exception e) {
//                    return MessageBean.fail("上传文件失败。");
//                }
//            }
//        }
//        return MessageBean.success();
//    }



}
