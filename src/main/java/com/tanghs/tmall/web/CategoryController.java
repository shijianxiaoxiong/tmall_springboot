package com.tanghs.tmall.web;
 
import com.tanghs.tmall.pojo.Category;
import com.tanghs.tmall.service.CategoryService;
import com.tanghs.tmall.util.ImageUtil;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;

//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class CategoryController {
    @Autowired CategoryService categoryService;

    /**
     * @Author tanghs
     * @Description: list分页查询跳转
     * @Date: 2020/5/9 9:38
     * @Version 1.0
     */
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Category> page =categoryService.list(start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 17:44
     * @Version 1.0
     */
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);  //新增分类
        if(image != null){
            saveOrUpdateImageFile(bean, image, request);  //新增图片
        }
        return bean;
    }
    //保存上传图片
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }   //图片路径存在则删除，避免图片名存在导致新增失败
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        /*img= (BufferedImage)ImageUtil.resizeImage(img,300,150); *///压缩指定尺寸
        ImageIO.write(img, ".jpg", file);
    }

    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:44
     * @Version 1.0
     */
    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") Integer id,HttpServletRequest request) throws Exception{
        categoryService.delete(id);  //删除分类
        File imageFolder = new File(request.getServletContext().getRealPath("img/category")); //获取图片路径
        File file = new File(imageFolder,id+".jpg");
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        in.close();        //关闭流，避免没关闭流导致删除失败
        file.delete();  //删除图片
        return null;
    }

    /**
     * @Author tanghs
     * @Description: 分类编辑
     * @Date: 2020/5/11 10:30
     * @Version 1.0
     */
    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") Integer id) throws Exception{
        return categoryService.get(id);
    }

    @PutMapping("/categories/{id}")
    public Category update(Category bean,MultipartFile image,HttpServletRequest request) throws Exception{
        categoryService.update(bean);
        if(image != null){
            saveOrUpdateImageFile(bean, image, request);  //修改图片
        }
        return bean;
    }

}