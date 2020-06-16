package com.tanghs.tmall.web;

import com.tanghs.tmall.dao.ProductDAO;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.ProductImage;
import com.tanghs.tmall.service.ProductImageService;
import com.tanghs.tmall.service.ProductService;
import com.tanghs.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class ProductImageController {
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductDAO productDAO;

    @GetMapping("/products/{pid}/productImage")
    public List<ProductImage> list(@RequestParam("type") String type,@PathVariable("pid") Integer pid) throws Exception{
        System.out.println(type);
        Product product = productService.get(pid);
        if (ProductImageService.type_single.equals(type)) {
            return  productImageService.listSingleProductImages(product);
        } else if (ProductImageService.type_detail.equals(type)) {
            return productImageService.listDetailProductImages(product);
        } else {
            return new ArrayList<>();
        }
    }
    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 17:44
     * @Version 1.0
     */
    @PostMapping("/productImage")
    public Object add(ProductImage bean,@RequestParam("pid") int pid,@RequestParam("type") String type,MultipartFile image, HttpServletRequest request) throws Exception {
        System.out.println(image);
        Product product = productService.get(pid);
        bean.setProduct(product);
        productImageService.add(bean);

        String folder = "img/";
        if(ProductImageService.type_single.equals(type)){
            folder += "productSingle";

        }else{
            folder += "productDetail";
        }
        File imageFolder= new File(request.getServletContext().getRealPath(folder));   //获取路径
        File file = new File(imageFolder,bean.getId()+".jpg");                  //将图片名称加入路径
        if(!file.getParentFile().exists())                                            //路径不存在则创建
            file.getParentFile().mkdirs();
        String fileName = file.getName();
        try {
            image.transferTo(file);
            BufferedImage bufferedImage= ImageUtil.change2jpg(file);
            ImageIO.write(bufferedImage,"jpg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");   //放置缩略图的地址
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle"); //放置中间图的地址
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);      //压缩图片为缩略图
            ImageUtil.resizeImage(file, 217, 190, f_middle);   //压缩图片为中间图
        }
        return bean;
    }

    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:44
     * @Version 1.0
     */
    @DeleteMapping("/productImage/{id}")
    public String delete(@PathVariable("id") Integer id,HttpServletRequest request) throws Exception{
        productImageService.delete(id);  //删除分类
        return null;
    }

}