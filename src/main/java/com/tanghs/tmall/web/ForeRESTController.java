package com.tanghs.tmall.web;
 
import com.tanghs.tmall.comparator.*;
import com.tanghs.tmall.pojo.*;
import com.tanghs.tmall.service.*;
import com.tanghs.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

//用于页面跳转，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;
    /**
     * @Author tanghs
     * @Description: 首页展示
     * @Date: 2020/5/20 13:45
     * @Version 1.0
     */
    @GetMapping("/fore_home")
    public List<Category> home() {
        List<Category> categorys= categoryService.list();
        productService.fill(categorys);                    //为分类填充产品合集
        productService.fillByRow(categorys);             // 为多个分类填充推荐产品集合
        categoryService.removeCategoryFromProduct(categorys);
        return categorys;
    }

    /**
     * @Author tanghs
     * @Description:   用户注册
     * @Date: 2020/5/20 13:46
     * @Version 1.0
     */
    @PostMapping("/fore_register")
    public Object register(@RequestBody User user){
        String name = user.getName();
        System.out.println(name);
        String passWord = user.getPassword();
        name = HtmlUtils.htmlEscape(name);              //将用户名进行转义
        user.setName(name);
        boolean isExist = userService.isExist(name);
        if(isExist){
            String message = "该用户名已被使用，请更换！";
            return  Result.fail(message);               //返回相关错误消息
        }

        //shiro加密处理
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();    //生盐
        int times = 2;     //加密次数
        String algorithmName = "md5";   //加密方式
        String encodedPassword = new SimpleHash(algorithmName, passWord, salt, times).toString();    //对密码进行加密

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        userService.add(user);
        return Result.success();
    }

    //1. 账号密码注入到 userParam 对象上
    //2. 把账号通过HtmlUtils.htmlEscape进行转义
    //3. 根据账号和密码获取User对象
    //3.1 如果对象为空，则返回错误信息
    //3.2 如果对象存在，则把用户对象放在 session里，并且返回成功信息
    //注 为什么要用 HtmlUtils.htmlEscape？ 因为注册的时候，ForeRESTController.register()，就进行了转义，
    // 所以这里也需要转义。在恶意注册的时候，会使用诸如 <script>alert('papapa')</script> 这样的名称，会导致网页打开就弹出一个对话框。 那么在转义之后，就没有这个问题了。

    /**
     * @Author tanghs
     * @Description: 用户登录
     * @Date: 2020/5/22 10:10
     * @Version 1.0
     */
    @PostMapping("/fore_login")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);             //账户名进行转义（如一些特殊符号‘_ / >’）

        Subject subject = SecurityUtils.getSubject();    //获取用户信息
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());
        String message ="账号密码错误";
        try {
                subject.login(token);
                User user  = userService.getByName(name);
                session.setAttribute("user", user);
                return Result.success();   //返回相关错误消
        } catch (Exception e) {
            return Result.fail(message);
        }
    }

    /**
     * @Author tanghs
     * @Description:   产品详情页，获取对应产品信息
     * @Date: 2020/5/21 15:36
     * @Version 1.0
     */
    @GetMapping("/fore_product/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);   //根据对象product，获取这个产品对应的单个图片集合
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);   //根据对象product，获取这个产品对应的详情图片集合
        product.setProductSingleImages(productSingleImages);             //存储产品单张图片
        product.setProductDetailImages(productDetailImages);            //存储产品详情图片

        List<PropertyValue> pvs = propertyValueService.list(product);  //获取产品的所有属性值
        List<Review> reviews = reviewService.list(product);            //获取产品对应的所有的评价
        productService.setSaleAndReviewNumber(product);                //设置产品的销量和评价数量
        productImageService.setFirstProductImage(product);              //设置产品的默认图片

        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);                                    // //把上述取值放在 map 中,通过 Result 把这个 map 返回到前端去
    }

    /**
     * @Author tanghs
     * @Description:  立即购买和加入购物车验证是否已登录
     * @Date: 2020/5/22 15:14
     * @Version 1.0
     */
    @GetMapping("fore_checkLogin")
    public Object checkLogin(HttpSession session){
        User user = (User)session.getAttribute("user");
        if(null != user){
            return Result.success();
        }
        return Result.fail("未登录");
    }

    /**
     * @Author tanghs
     * @Description:   分类排序，根据综合，销量，人气，新品，评价数量
     * @Date: 2020/5/25 16:37
     * @Version 1.0
     */
    @GetMapping("fore_category/{cid}")
    public Object category(@PathVariable Integer cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        if(null != sort){
            switch (sort){
                case "review":              //评价/人气
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date":               //新品
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount":          //销量
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;
                case "price":              //价格
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;
                case "all":                //综合
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }

        }
        return category;
    }

    /**
     * @Author tanghs
     * @Description:  分类页搜索
     * @Date: 2020/6/4 10:34
     * @Version 1.0
     */
    @PostMapping("fore_search")
    public Object search( String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> products= productService.search(keyword,0,20);
        productImageService.setFirstProductImages(products);
        productService.setSaleAndReviewNumber(products);
        return products;
    }

    /**
     * @Author tanghs
     * @Description:   立即购买与添加到购物车
     * @Date: 2020/6/9 9:57
     * @Version 1.0
     */
    @GetMapping("fore_buyOne")
    public Object buyOne(int pid, int num, HttpSession session) {
        return buyOneAndAddCart(pid,num,session);
    }
    /**
     * @Author tanghs
     * @Description:  添加到购物车
     * @Date: 2020/6/9 15:31
     * @Version 1.0
     */
    @GetMapping("fore_addCart")
    public Object addCart(int pid,int num,HttpSession session){
        buyOneAndAddCart(pid,num,session);
        return Result.success();
    }

    private int buyOneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;

        User user =(User)  session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }

    /**
     * @Author tanghs
     * @Description: 1. 通过字符串数组获取参数oiid
     * 为什么这里要用字符串数组试图获取多个oiid，而不是int类型仅仅获取一个oiid? 因为根据购物流程环节与表关系，
     * 结算页面还需要显示在购物车中选中的多条OrderItem数据，所以为了兼容从购物车页面跳转过来的需求，要用字符串数组获取多个oiid
     * 2. 准备一个泛型是OrderItem的集合ois
     * 3. 根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入ois集合中
     * 4. 累计这些ois的价格总数，赋值在total上
     * 5. 把订单项集合放在session的属性 "ois" 上
     * 6. 把订单集合和total 放在map里
     * 7. 通过 Result.success 返回
     * @Date: 2020/6/9 10:25
     * @Version 1.0
     */
    @GetMapping("fore_buys")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }


        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * @Author tanghs
     * @Description:   购物车展示页面
     * @Date: 2020/6/9 15:53
     * @Version 1.0
     */
    @GetMapping("fore_cart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }
    /**
     * @Author tanghs
     * @Description:   购物车删除商品
     * @Date: 2020/6/9 16:52
     * @Version 1.0
     */
    @GetMapping("fore_deleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    /**
     * @Author tanghs
     * @Description:  提交生成订单
     * 1. 从session中获取user对象
     * 2. 根据当前时间加上一个4位随机数生成订单号
     * 3. 根据上述参数，创建订单对象
     * 4. 把订单状态设置为等待支付
     * 5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中 )
     * 7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
     * 8. 统计本次订单的总金额
     * 9. 返回总金额
     * @Date: 2020/6/9 17:25
     * @Version 1.0
     */
    @PostMapping("fore_createOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }
    /**
     * @Author tanghs
     * @Description:  订单支付
     * @Date: 2020/6/9 17:35
     * @Version 1.0
     */
    @GetMapping("fore_payed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    /**
     * @Author tanghs
     * @Description:  订单页查询
     * @Date: 2020/6/10 9:09
     * @Version 1.0
     */

    @GetMapping("fore_bought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    /**
     * @Author tanghs
     * @Description:  确认收货
     * @Date: 2020/6/10 10:25
     * @Version 1.0
     */
    @GetMapping("fore_confirmPay")
    public Object confirmPay(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.totalAmount(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    /**
     * @Author tanghs
     * @Description:   确认收获成功
     * @Date: 2020/6/10 10:30
     * @Version 1.0
     */
    @GetMapping("fore_orderConfirmed")
    public Object orderConfirmed( int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return Result.success();
    }
    /**
     * @Author tanghs
     * @Description:  删除订单
     * 1.1 获取参数oid
     * 1.2 根据oid获取订单对象o
     * 1.3 修改状态
     * 1.4 更新到数据库
     * 1.5 返回成功
     * @Date: 2020/6/10 15:07
     * @Version 1.0
     */
    @PutMapping("fore_deleteOrder")
    public Object deleteOrder(int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return Result.success();
    }

    /**
     * @Author tanghs
     * @Description:  订单评价
     * @Date: 2020/6/10 15:21
     * @Version 1.0
     */
    @GetMapping("fore_review")
    public Object review(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        Map<String,Object> map = new HashMap<>();
        map.put("p", product);
        map.put("o", order);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    /**
     * @Author tanghs
     * @Description: 提交评价
     * 1.1 获取参数oid
     * 1.2 根据oid获取订单对象o
     * 1.3 修改订单对象状态
     * 1.4 更新订单对象到数据库
     * 1.5 获取参数pid
     * 1.6 根据pid获取产品对象
     * 1.7 获取参数content (评价信息)
     * 1.8 对评价信息进行转义，道理同注册ForeRESTController.register()
     * 1.9 从session中获取当前用户
     * 1.10 创建评价对象review
     * 1.11 为评价对象review设置 评价信息，产品，时间，用户
     * 1.12 增加到数据库
     * 1.13.返回成功
     * @Date: 2020/6/10 16:38
     * @Version 1.0
     */
    @PostMapping("fore_doReview")
    public Object doReview( HttpSession session,int oid,int pid,String content) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }

}